<script setup>
import MenuComponent from '@/components/desktop/MenuComponent.vue'
</script>

<!-- 
  [ Antdv Grid ]
  row 공간에 col 집합을 설정하며, 하나의 열은 최대 24까지의 범위를 가진다
  https://antdv.com/components/grid
 -->
<template>
  <a-layout-header class="header">
    <a-row justify="center" align="middle">
      <a-col :span="10">
        <RouterLink :to="{ name: 'home' }">
          <img src="@/assets/text-logo.png" alt="" :style="{ display: 'flex', height: '40px' }" />
        </RouterLink>
      </a-col>
      <a-col :span="10">
        <MenuComponent />
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<style scoped lang="scss">
.header {
  background-color: white;
  height: $headerHeight;
  line-height: $headerHeight;
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid rgb(238, 238, 238);
}
</style>
