<script setup>
import { ref } from 'vue'

const menuList = ref([
  { name: '스토리창작', routeName: 'creator' },
  { name: '로그인', routeName: 'login' },
  { name: '회원가입', routeName: 'signup' }
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
