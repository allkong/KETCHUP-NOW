import { ref, inject } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('userStore', () => {
  const axios = inject('axios') // Provide한 axios

  const menuList = ref([
    { name: '스토리창작', routeName: 'creator' },
    { name: '로그인', routeName: 'login' },
    { name: '회원가입', routeName: 'signup' }
  ])

  const userInfo = ref(null)

  /**
   * actions
   */
  const login = async (loginInfo) => {
    await axios
      .post('/auth/login', loginInfo, { withCredentials: true })
      // 로그인 성공시
      .then(async (response) => {
        console.log(response)
        // 모든 요청에 Access token이 포함되도록 함
        const newAccessToken = response.headers.getAuthorization()
        axios.defaults.headers.common['Authorization'] = newAccessToken
        // 사용자 정보 state 갱신
        userInfo.value = (await axios.get('/users/me')).data
      })
      .catch((error) => console.error(error))
  }

  return {
    menuList,
    userInfo,
    login
  }
})
