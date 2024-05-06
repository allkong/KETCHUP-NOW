import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/views/HomeView.vue'
import UserView from '@/views/UserView.vue'
import LoginComponent from '@/components/LoginComponent.vue'
import SignUpComponent from '@/components/SignUpComponent.vue'

const webRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/user',
      name: 'user',
      component: UserView,
      children: [
        {
          path: 'login',
          name: 'login',
          component: LoginComponent,
        },
        {
          path: 'signup',
          name: 'signup',
          component: SignUpComponent,
        },
      ],
    },
  ],
})

const mobileRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/user',
      name: 'user',
      component: UserView,
      children: [
        {
          path: 'login',
          name: 'login',
          component: LoginComponent,
        },
        {
          path: 'signup',
          name: 'signup',
          component: SignUpComponent,
        },
      ],
    },
  ],
})

// 화면 너비에 따른 mobile, web(pc) 분기
let router
const isMobile = window.innerWidth < 768

if (isMobile) {
  console.log('mobile')
  router = mobileRouter
} else {
  console.log('pc')
  router = webRouter
}

export default router
