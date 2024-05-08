<script setup>
import { RouterView } from 'vue-router'
import HeaderView from '@/views/desktop/includes/HeaderView.vue'
import FooterView from '@/views/desktop/includes/FooterView.vue'
</script>

<!-- 
  [ Antdv Layout ]
  https://antdv.com/components/layout
 -->
<template>
  <a-layout class="layout">
    <HeaderView />
    <RouterView />
    <FooterView />
  </a-layout>
</template>

<style>
body {
  margin: 0;
}

.ant-menu-horizontal,
.ant-menu-item,
.ant-menu-item::after {
  border: none !important;
  color: inherit !important;
}
</style>
