<script setup>
import MenuComponent from '@/components/mobile/MenuComponent.vue'
</script>

<template>
  <a-affix :offset-bottom="0" class="bottom-navbar">
    <a-layout-footer class="navbar-footer">
      <MenuComponent />
    </a-layout-footer>
  </a-affix>
</template>

<style scoped>
.bottom-navbar {
  position: fixed;
  bottom: 0;
  width: 100%;
}

.navbar-footer {
  background-color: #fff;
  border-top: 1px solid #eaeaea;
}
</style>
