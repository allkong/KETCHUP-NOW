<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import HeaderView from '@/views/mobile/includes/HeaderView.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import SearchMapComponent from '@/components/mobile/SearchMapComponent.vue'

const route = useRoute()
let initialFocusStoryUuid = null
if (route.query['story-uuid']) {
  initialFocusStoryUuid = route.query['story-uuid']
}

// 지도 영역 높이 계산
const setVH = () => {
  const vh = window.innerHeight * 0.01 // 뷰 포트 높이의 1vh 계산
  document.documentElement.style.setProperty('--vh', `${vh}px`)
}

onMounted(() => {
  setVH()
  window.addEventListener('resize', setVH)
})
</script>

<template>
  <a-layout-content id="map-size">
    <SearchMapComponent :initial-focus-story-uuid="initialFocusStoryUuid" />
  </a-layout-content>
  <NavigationView />
</template>

<style scoped></style>
