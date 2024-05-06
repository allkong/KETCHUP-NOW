import { ref, inject } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('userStore', () => {
  const axios = inject('axios') // Provide한 axios

  const menuList = ref([
    { name: '스토리창작', routeName: 'creator' },
    { name: '로그인', routeName: 'login' },
    { name: '회원가입', routeName: 'signup' },
  ])

  /**
   * actions
   */
  const login = async (userInfo) => {
    await axios
      .post('/auth/login', userInfo, { withCredentials: true })
      .then((response) => {
        console.log(response)
      })
      .catch((error) => console.error(error))
  }

  return {
    menuList,
    login,
  }
})
