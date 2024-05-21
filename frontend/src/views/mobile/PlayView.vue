<script setup>
import { ref, inject, onMounted } from 'vue'
import PlayMapComponent from '@/components/mobile/PlayMapComponent.vue'
import HeaderView from '@/views/mobile/includes/HeaderView.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const axios = inject('axios')
const router = useRouter()

const isPlaying = ref(false)

onMounted(() => {
  setVH()
  window.addEventListener('resize', setVH)
})

// 지도 영역 높이 계산
const setVH = () => {
  const vh = window.innerHeight * 0.01 // 뷰 포트 높이의 1vh 계산
  document.documentElement.style.setProperty('--vh', `${vh}px`)
}

const updatePlayingStatus = () => {
  return axios
    .get('playings/now')
    .then((response) => {
      if (response.data.status === 'PLAYING') {
        isPlaying.value = true
      } else {
        isPlaying.value = false
      }

      return Promise.resolve(response.data)
    })
    .catch((error) => {
      isPlaying.value = false
      return Promise.reject(error)
    })
}

onMounted(async () => {
  updatePlayingStatus().finally(() => {
    if (!isPlaying.value) {
      router.back()
    }
  })
})
</script>

<template>
  <!-- <HeaderView /> -->
  <a-layout-content id="map-size">
    <PlayMapComponent />
  </a-layout-content>
  <NavigationView />
</template>

<style scoped></style>
