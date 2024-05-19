import { createRouter, createWebHistory } from 'vue-router'

import DesktopHomeView from '@/views/desktop/HomeView.vue'
import DesktopUserView from '@/views/desktop/UserView.vue'
import DesktopLoginComponent from '@/components/desktop/LoginComponent.vue'
import DesktopSignUpComponent from '@/components/desktop/SignUpComponent.vue'
import DesktopCreatorView from '@/views/desktop/CreatorView.vue'
import DesktopNotFoundView from '@/views/desktop/NotFoundView.vue'

import MobileHomeView from '@/views/mobile/HomeView.vue'
import MobileSearchView from '@/views/mobile/SearchView.vue'
import MobilePlayView from '@/views/mobile/PlayView.vue'
import MobileClearedStoryListView from '@/views/mobile/ClearedStoryListView.vue'
import MobileStoryRecordView from '@/views/mobile/MobileStoryRecordView.vue'
import MobileMyPageView from '@/views/mobile/MyPageView.vue'
import MobileMyReviewListView from '@/views/mobile/MyReviewListView.vue'
import MobileNotFoundView from '@/views/mobile/NotFoundView.vue'
import MobileLoginView from '@/views/mobile/LoginView.vue'
import MobileSignUpView from '@/views/mobile/SignUpView.vue'
import { message } from 'ant-design-vue'

const desktopRouter = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: DesktopHomeView,
    },
    {
      path: '/auth',
      name: 'auth',
      component: DesktopUserView,
      children: [
        {
          path: 'login',
          name: 'auth:login',
          component: DesktopLoginComponent,
        },
        {
          path: 'signup',
          name: 'auth:sign-up',
          component: DesktopSignUpComponent,
        },
      ],
    },
    {
      path: '/creator',
      name: 'creator',
      component: DesktopCreatorView,
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: DesktopNotFoundView,
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
      path: '/stories/cleared',
      name: 'story:cleared-list',
      component: MobileClearedStoryListView,
    },
    {
      path: '/stories/cleared/:storyUuid/:uuid',
      name: 'story:cleared-record',
      component: MobileStoryRecordView,
    },
    {
      path: '/user/my-page',
      name: 'user:my-page',
      component: MobileMyPageView,
    },
    {
      path: '/user/my-reviews',
      name: 'user:my-reviews',
      component: MobileMyReviewListView,
    },
    {
      path: '/auth/login',
      name: 'auth:login',
      component: MobileLoginView,
    },
    {
      path: '/auth/sign-up',
      name: 'auth:sign-up',
      component: MobileSignUpView,
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: MobileNotFoundView,
    },
  ],
})

// 화면 너비에 따른 mobile, web(pc) 분기
let router
const isMobile = window.innerWidth < 768

let ANONYMOUS_ONLY_ROUTE_NAMES = []
let LOGIN_REQUIRED_ROUTE_NAMES = []

if (isMobile) {
  console.log('mobile')
  router = mobileRouter

  ANONYMOUS_ONLY_ROUTE_NAMES = ['auth:login', 'auth:sign-up']
  LOGIN_REQUIRED_ROUTE_NAMES = [
    'search',
    'play',
    'story:cleared-list',
    'story:cleared-record',
    'user:my-page',
    'user:my-reviews',
  ]
} else {
  console.log('pc')
  router = desktopRouter

  ANONYMOUS_ONLY_ROUTE_NAMES = ['auth:login', 'auth:sign']
  LOGIN_REQUIRED_ROUTE_NAMES = ['creator']
}

router.beforeEach((to, from, next) => {
  const isLoggedIn = sessionStorage.getItem('accessToken') !== null

  // 로그인이 필요한 화면에 익명 사용자가 접근한 경우
  if (!isLoggedIn && LOGIN_REQUIRED_ROUTE_NAMES.filter((name) => name === to.name).length > 0) {
    message.error('로그인이 필요합니다.')
    router.push({ name: 'auth:login' })
  }
  // 익명 사용자만 접근 가능한 화면에 로그인한 사용자가 접근한 경우
  else if (isLoggedIn && ANONYMOUS_ONLY_ROUTE_NAMES.filter((name) => name === to.name).length > 0) {
    message.error('이미 로그인 되어 있습니다.')
    router.push({ name: 'home' })
  } else {
    next()
  }
})

export default router
