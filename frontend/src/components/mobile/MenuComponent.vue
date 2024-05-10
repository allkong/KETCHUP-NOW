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
  <!-- <a-menu id="menu-items">
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'search' }"><span>검색</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'play' }"><span>플레이</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'search' }"><span>검색</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'story:cleared-list' }"><span>추천</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'user:my-page' }"><span>MY</span></RouterLink>
    </a-menu-item>
  </a-menu> -->
  <a-row id="menu-items">
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'search' }"><span>검색</span></RouterLink>
    </a-col>
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'play' }"><span>플레이</span></RouterLink>
    </a-col>
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'search' }"><span>검색</span></RouterLink>
    </a-col>
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'story:cleared-list' }"><span>추천</span></RouterLink>
    </a-col>
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'user:my-page' }"><span>MY</span></RouterLink>
    </a-col>
  </a-row>
</template>

<style scoped lang="scss">
#menu-items {
  position: absolute;
  bottom: 0;
  justify-content: space-between;
  background-color: gray;
  height: $footerHeight;
  width: 100%;
}
</style>
