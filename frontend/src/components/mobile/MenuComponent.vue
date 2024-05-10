<script setup>
import { ref } from 'vue'

const menuList = ref([
  { name: '검색', routeName: 'search' },
  { name: '플레이', routeName: 'play' },
  { name: '스토리', routeName: 'story:cleared-list' },
  { name: 'MY', routeName: 'user:my-page' },
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
/* .menu-items {
  float: right;
} */
</style>
