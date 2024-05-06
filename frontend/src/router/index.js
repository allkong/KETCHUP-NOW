import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

import UserView from '@/views/UserView.vue'
import LoginComponent from '@/components/LoginComponent.vue'
import SignUpComponent from '@/components/SignUpComponent.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
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
    }
  ]
})

export default router
