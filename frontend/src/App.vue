<script setup>
import { RouterView } from 'vue-router'
import HeaderView from '@/views/includes/HeaderView.vue'
</script>

<template>
<a-layout class="layout">
  <HeaderView />
  
  <RouterView />
</a-layout>
</template>

<style>
body {
  margin: 0
}
</style>