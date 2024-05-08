<script setup>
import HeaderView from '@/views/desktop/includes/HeaderView.vue'
import FooterView from '@/views/desktop/includes/FooterView.vue'
</script>

<template>
  <HeaderView />
  <a-layout-content>
    <div :style="{ background: '#fff', padding: '24px', minHeight: '86vh' }">홈</div>
  </a-layout-content>
  <FooterView />
</template>

<style scoped></style>
