<script setup>
import MenuComponent from '@/components/mobile/MenuComponent.vue'
</script>

<template>
  <a-affix :offset-bottom="0" class="bottom-navbar">
    <MenuComponent />
  </a-affix>
</template>

<style scoped lang="scss">
.bottom-navbar {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: $navigationHeight;
  background-color: white;
  border-top: 1px solid #eaeaea;
}
</style>
