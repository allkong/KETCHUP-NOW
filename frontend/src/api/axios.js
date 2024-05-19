import axios from 'axios'
import { Mutex } from 'async-mutex'

import HttpStatus from './http-status'
import router from '@/router'

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

let tokenRefreshingMutex = new Mutex()

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

      // 다른 요청이 토큰 갱신을 시도하고 있으면, 완료될 때까지 기다림
      if (tokenRefreshingMutex.isLocked()) {
        await tokenRefreshingMutex.waitForUnlock()
      }

      // 토큰이 없는 것을 가장 먼저 확인한 요청이 토큰 발급을 시도함
      // Critical Section below ===================================================================
      try {
        tokenRefreshingMutex.acquire()

        // 새 액세스 토큰이 응답되면,
        const refreshResponse = await instance.post('/auth/token', { withCredentials: true })

        // 모든 요청에 access token이 포함되어 가도록 보장
        const newAccessToken = refreshResponse.headers.authorization
        instance.defaults.headers.common['Authorization'] = newAccessToken
      } catch (error) {
        // 토큰 갱신에 실패하면 Circuit break
        router.push({ name: 'auth:login' })
        return Promise.reject(error)
      } finally {
        tokenRefreshingMutex.release()
      }
      // Critical Section above ==================================================================

      // Critical section 내부의 작업을 통해 모든 요청에 access token이 가는 것이 보장됨
      return instance(originalRequest)
    } else if (response.status === HttpStatus.UNAUTHORIZED) {
      router.push({ name: 'auth:login' })
    }

    return Promise.reject(error)
  },
)
