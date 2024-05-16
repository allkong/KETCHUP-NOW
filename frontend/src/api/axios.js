import axios from 'axios'
import { useRouter } from 'vue-router'

import HttpStatus from './http-status'

const { VITE_SERVER_URL } = import.meta.env

const router = useRouter()

export const instance = axios.create({
  baseURL: VITE_SERVER_URL,
  withCredentials: true,
})

// Request 발생 시 적용할 기본 속성 설정
instance.defaults.headers.post['Content-Type'] = 'application/json'
instance.defaults.headers.put['Content-Type'] = 'application/json'
instance.defaults.headers.patch['Content-Type'] = 'application/json'

let isTokenRefreshing = false
instance.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    // 에러가 발생한 요청과 응답 객체 저장
    const { prevRequest, response } = error

    // 토큰 만료 감지
    if (response.status === HttpStatus.FORBIDDEN && response.data.detailCode === 'E0001') {
      // 토큰 재발급이 이루어지는 도중에 새로운 요청이 들어오는 것을 방지
      if (!isTokenRefreshing) {
        // Lock
        isTokenRefreshing = true

        // Refresh token은 쿠키에 저장되므로 withCredentials 필요
        return await instance
          .post('/auth/token', {}, { withCredentials: true })
          // 재발급 성공
          .then((response) => {
            // Access token 갱신
            const newAccessToken = response.headers.getAuthorization()
            instance.defaults.headers.common['Authorization'] = newAccessToken

            // 이전에 시도하려던 요청에 새 Access token을 넣어서
            prevRequest.headers.setAuthorization(newAccessToken)
            sessionStorage.setItem('accessToken', newAccessToken)

            // 다시 요청
            return instance(prevRequest)
          })
          // 재발급 실패
          .catch((error) => {
            alert('다시 로그인 해주세요.')
            return Promise.reject(error)
          })
          .finally(() => {
            // Lock release
            isTokenRefreshing = false
          })
      }
    }
    // 아예 Access token도 없는 경우
    else if (response.status === HttpStatus.UNKNOWN_USER && response.data.detailCode === 'E0001') {
      // 로그인 페이지로 이동
      router.replace('login')
    }

    return Promise.reject(error)
  },
)
