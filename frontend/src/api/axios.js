import axios from 'axios'

import HttpStatus from './http-status'
import router from '@/router'
import { Mutex } from 'async-mutex'

const { VITE_SERVER_URL } = import.meta.env

export const instance = axios.create({
  baseURL: VITE_SERVER_URL,
  withCredentials: true,
})

// Request 발생 시 적용할 기본 속성 설정.
instance.defaults.headers.post['Content-Type'] = 'application/json'
instance.defaults.headers.put['Content-Type'] = 'application/json'

instance.interceptors.request.use(
  (request) => {
    return request
  },
  (error) => {
    return Promise.reject(error)
  },
)

let isTokenRefreshing = false
let refreshTokenPromise = null

instance.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    // 에러가 발생했던 요청과 그 응답 객체를 저장.
    const { config, response } = error

    // 응답으로 Unauthorized가 발생한 경우.
    // -> 새로고침으로 토큰이 사라졌거나, 만료, 조작된 경우.
    // -> RefreshToken을 이용하여 재발급 진행.
    if (response.status === HttpStatus.FORBIDDEN) {
      // /auth/refresh로 요청을 진행해야 하므로, 기존의 요청을 저장.
      const originalRequest = config

      // 토큰이 없는 것을 가장 먼저 확인한 요청이 토큰 발급을 시도함
      if (!isTokenRefreshing) {
        isTokenRefreshing = true

        // 토큰을 갱신하는 시도는 하나의 요청만 하면 됨
        refreshTokenPromise = instance
          .post('/auth/token', { withCredentials: true })
          // 새 액세스 토큰이 응답되면,
          .then((response) => {
            // 토큰을 재설정 해주고
            const newAccessToken = response.headers.authorization
            instance.defaults.headers.common['Authorization'] = newAccessToken
            // 토큰을 기다리는 다른 요청에서 콜백으로 쓸 수 있도록 토큰을 반환
            isTokenRefreshing = false
            return newAccessToken
          })
          // 토큰을 받는데 실패하면
          .catch((error) => {
            // Circuit break
            isTokenRefreshing = false
            router.push({ name: 'auth:login' })
            return Promise.reject(error)
          })
      }

      // 토큰이 없으나 다른 요청이 토큰 발급을 시도하고 있으므로 기다림
      return (
        refreshTokenPromise
          // 토큰 발급에 성공하면
          .then((newAccessToken) => {
            // 기존 요청의 헤더에 토큰을 넣어 다시 요청을 보낸 결과를 반환
            originalRequest.headers.authorization = newAccessToken
            return instance(originalRequest)
          })
          // 토큰 발급에 실패하면
          .catch((error) => {
            // 밖으로 에러를 던짐
            return Promise.reject(error)
          })
      )
    } else if (response.status === HttpStatus.UNAUTHORIZED) {
      router.push({ name: 'auth:login' })
    }

    return Promise.reject(error)
  },
)
