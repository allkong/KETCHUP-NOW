<script setup>
import { RouterView } from 'vue-router'
import HeaderView from '@/views/desktop/includes/HeaderView.vue'
import FooterView from '@/views/desktop/includes/FooterView.vue'
</script>

<template>
  <HeaderView />
  <a-layout-content>
    <RouterView />
  </a-layout-content>
  <FooterView />
</template>

<style scoped>
.ant-layout-content {
  min-height: 85vh;
}
</style>
