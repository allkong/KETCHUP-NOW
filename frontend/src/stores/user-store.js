import { ref, inject, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('userStore', () => {
  const axios = inject('axios') // Provide한 axios

  const userInfo = ref(null)
  const isLoggedIn = computed(() => userInfo.value !== null)

  axios.post('/auth/token').then((resp) => {
    setAccessToken(resp.headers.getAuthorization())
  })

  /**
   * actions
   */
  const login = async (loginInfo) => {
    return (
      axios
        .post('/auth/login', loginInfo, { withCredentials: true })
        // 로그인 성공시
        .then(async (response) => {
          console.log(response.headers)

          const newAccessToken = response.headers.getAuthorization()
          setAccessToken(newAccessToken)
          // 사용자 정보 state 갱신
          userInfo.value = (await axios.get('/users/me')).data
        })
    )
  }

  function setAccessToken(accessToken) {
    // 모든 요청에 Access token이 포함되도록 함
    axios.defaults.headers.common['Authorization'] = accessToken
    // Pinia 바깥에서 로그인 여부를 확인하기 위해 세션 스토리지에도 써줌
    sessionStorage.setItem('accessToken', accessToken)
  }

  return {
    userInfo,
    isLoggedIn,
    login,
  }
})
