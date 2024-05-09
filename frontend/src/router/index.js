import { createRouter, createWebHistory } from 'vue-router'

import DesktopHomeView from '@/views/desktop/HomeView.vue'
import UserView from '@/views/desktop/UserView.vue'
import LoginComponent from '@/components/desktop/LoginComponent.vue'
import SignUpComponent from '@/components/desktop/SignUpComponent.vue'
import CreatorView from '@/views/desktop/CreatorView.vue'
import MobileHomeView from '@/views/mobile/HomeView.vue'

const desktopRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: DesktopHomeView
    },
    {
      path: '/user',
      name: 'user',
      component: UserView,
      children: [
        {
          path: 'login',
          name: 'login',
          component: LoginComponent
        },
        {
          path: 'signup',
          name: 'signup',
          component: SignUpComponent
        }
      ]
    },
    {
      path: '/creator',
      name: 'creator',
      component: CreatorView
    }
  ]
})

const mobileRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: MobileHomeView
    }
  ]
})

// 화면 너비에 따른 mobile, web(pc) 분기
let router
const isMobile = window.innerWidth < 768

if (isMobile) {
  console.log('mobile')
  router = mobileRouter
} else {
  console.log('pc')
  router = desktopRouter
}

export default router
