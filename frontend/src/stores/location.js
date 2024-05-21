import { defineStore } from 'pinia'
import { ref } from 'vue'

const 경복궁 = {
  latitude: 37.576624,
  longitude: 126.978565,
}

const 멀티캠퍼스_역삼 = {
  latitude: 37.5015109,
  longitude: 127.0397022,
}

export const useLocationStore = defineStore('locationStore', () => {
  // 초기 위치
  const coords = ref({
    ...경복궁,
  })

  return { coords }
})
