<script setup>
import MapComponent from '@/components/desktop/MapComponent.vue'
</script>

<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">사이드바</a-layout-sider>
    <a-layout-content>
      <MapComponent />
    </a-layout-content>
  </a-layout>
</template>

<style scoped></style>
