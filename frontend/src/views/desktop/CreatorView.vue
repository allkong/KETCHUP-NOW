<script setup>
import HeaderView from '@/views/desktop/includes/HeaderView.vue'
import MapComponent from '@/components/desktop/MapComponent.vue'
</script>

<template>
  <HeaderView />
  <a-layout>
    <a-layout-sider v-model:collapsed="collapsed" collapsible width="200" style="background: #fff"
      >사이드바</a-layout-sider
    >
    <a-layout-content>
      <MapComponent />
    </a-layout-content>
  </a-layout>
</template>

<style scoped></style>
