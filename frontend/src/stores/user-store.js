import { ref, inject, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('userStore', () => {
  const axios = inject('axios') // Provide한 axios

  const userInfo = ref({
    nickname: '',
    loginId: '',
    createdAt: '',
  })
  const isLoggedIn = computed(() => userInfo.value !== null)

  axios.post('/auth/token').then((resp) => {
    setAccessToken(axios, resp.headers.getAuthorization())
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
          const newAccessToken = response.headers.getAuthorization()
          setAccessToken(axios, newAccessToken)
          // 사용자 정보 state 갱신
          // userInfo.value = (await axios.get('/users/me')).data
          fetchUserInfo()
        })
    )
  }

  const signUp = async ({ loginId, password, nickname }) => {
    return axios.post('/users/sign-up', {
      loginId,
      password,
      nickname,
    })
  }

  const fetchUserInfo = async () => {
    return axios.get('/users/me').then((resp) => {
      userInfo.value = resp.data
    })
  }

  function setAccessToken(axios, accessToken) {
    // 모든 요청에 Access token이 포함되도록 함
    axios.defaults.headers.common['Authorization'] = accessToken
    // Pinia 바깥에서 로그인 여부를 확인하기 위해 세션 스토리지에도 써줌
    sessionStorage.setItem('accessToken', accessToken)
  }

  function logout() {
    axios.post('/auth/logout')
    axios.defaults.headers.common['Authorization'] = null
    sessionStorage.removeItem('accessToken')
  }

  fetchUserInfo()
  return {
    userInfo,
    isLoggedIn,
    login,
    signUp,
    fetchUserInfo,
    logout,
  }
})
