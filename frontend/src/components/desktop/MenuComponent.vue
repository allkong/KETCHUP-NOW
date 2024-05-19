<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user-store'
import { message } from 'ant-design-vue'

const userStore = useUserStore()
const router = useRouter()

function doLogout() {
  userStore.logout()
  message.success('로그아웃 되었습니다. 또 오실거죠? 🥺')
  router.push({ name: 'home' })
}
</script>

<template>
  <a-menu mode="horizontal" class="menu-items" v-if="userStore.userInfo !== null">
    <a-menu-item>
      <RouterLink :to="{ name: 'creator' }">스토리 창작</RouterLink>
    </a-menu-item>
    <a-menu-item @click="doLogout">로그아웃</a-menu-item>
  </a-menu>
  <a-menu mode="horizontal" class="menu-items" v-if="userStore.userInfo === null">
    <a-menu-item>
      <RouterLink :to="{ name: 'auth:login' }">로그인</RouterLink>
    </a-menu-item>
    <a-menu-item>
      <RouterLink :to="{ name: 'auth:sign-up' }">회원가입</RouterLink>
    </a-menu-item>
  </a-menu>
</template>

<style scoped>
.menu-items {
  float: right;
}
</style>
