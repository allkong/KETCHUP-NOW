import { createRouter, createWebHistory } from 'vue-router'

import desktopHomeView from '@/views/desktop/HomeView.vue'
import desktopUserView from '@/views/desktop/UserView.vue'
import desktopLoginComponent from '@/components/desktop/LoginComponent.vue'
import desktopSignUpComponent from '@/components/desktop/SignUpComponent.vue'
import desktopCreatorView from '@/views/desktop/CreatorView.vue'

import mobileHomeView from '@/views/mobile/HomeView.vue'
import mobileSearchView from '@/views/mobile/SearchView.vue'
import mobilePlayView from '@/views/mobile/PlayView.vue'
import mobileRecordView from '@/views/mobile/StoryRecordView.vue'
import mobileMyPageView from '@/views/mobile/MyPageView.vue'

const desktopRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: desktopHomeView,
    },
    {
      path: '/user',
      name: 'user',
      component: desktopUserView,
      children: [
        {
          path: 'login',
          name: 'login',
          component: desktopLoginComponent,
        },
        {
          path: 'signup',
          name: 'signup',
          component: desktopSignUpComponent,
        },
      ],
    },
    {
      path: '/creator',
      name: 'creator',
      component: desktopCreatorView,
    },
  ],
})

const mobileRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: mobileHomeView,
    },
    {
      path: '/search',
      name: 'search',
      component: mobileSearchView,
    },
    {
      path: '/play',
      name: 'play',
      component: mobilePlayView,
    },
    {
      path: '/record',
      name: 'record',
      component: mobileRecordView,
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: mobileMyPageView,
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
  router = desktopRouter
}

export default router
