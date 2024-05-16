<script setup>
import { onMounted } from 'vue'
import HeaderView from '@/views/mobile/includes/HeaderView.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import SearchMapComponent from '@/components/mobile/SearchMapComponent.vue'

const setVH = () => {
  const vh = window.innerHeight * 0.01 // 뷰포트 높이의 1%를 계산
  document.documentElement.style.setProperty('--vh', `${vh}px`)
}

onMounted(() => {
  setVH()
  window.addEventListener('resize', setVH)
})
</script>

<template>
  <a-layout-content id="map-size">
    <SearchMapComponent />
  </a-layout-content>
  <NavigationView />
</template>

<style scoped></style>
