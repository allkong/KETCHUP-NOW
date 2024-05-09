import { createRouter, createWebHistory } from 'vue-router'

import DesktopHomeView from '@/views/desktop/HomeView.vue'
import DesktopUserView from '@/views/desktop/UserView.vue'
import DesktopLoginComponent from '@/components/desktop/LoginComponent.vue'
import DesktopSignUpComponent from '@/components/desktop/SignUpComponent.vue'
import DesktopCreatorView from '@/views/desktop/CreatorView.vue'

import MobileHomeView from '@/views/mobile/HomeView.vue'
import MobileSearchView from '@/views/mobile/SearchView.vue'
import MobilePlayView from '@/views/mobile/PlayView.vue'
import MobileRecordView from '@/views/mobile/StoryRecordView.vue'
import MobileMyPageView from '@/views/mobile/MyPageView.vue'

const desktopRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: DesktopHomeView,
    },
    {
      path: '/user',
      name: 'user',
      component: DesktopUserView,
      children: [
        {
          path: 'login',
          name: 'login',
          component: DesktopLoginComponent,
        },
        {
          path: 'signup',
          name: 'signup',
          component: DesktopSignUpComponent,
        },
      ],
    },
    {
      path: '/creator',
      name: 'creator',
      component: DesktopCreatorView,
    },
  ],
})

const mobileRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: MobileHomeView,
    },
    {
      path: '/search',
      name: 'search',
      component: MobileSearchView,
    },
    {
      path: '/play',
      name: 'play',
      component: MobilePlayView,
    },
    {
      path: '/record',
      name: 'record',
      component: MobileRecordView,
    },
    {
      path: '/user/my-page',
      name: 'user:my-page',
      component: MobileMyPageView,
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
