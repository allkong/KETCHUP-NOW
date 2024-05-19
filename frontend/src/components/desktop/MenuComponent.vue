<script setup>
import { ref } from 'vue'

const menuList = ref([
  { name: '내스토리', routeName: 'my-stories' },
  { name: '로그인', routeName: 'login' },
  { name: '회원가입', routeName: 'signup' },
])
</script>

<template>
  <a-menu mode="horizontal" class="menu-items">
    <a-menu-item v-for="menu in menuList" :key="menu.name">
      <RouterLink :to="{ name: menu.routeName }">{{ menu.name }}</RouterLink>
    </a-menu-item>
  </a-menu>
</template>

<style scoped>
.menu-items {
  float: right;
}
</style>
