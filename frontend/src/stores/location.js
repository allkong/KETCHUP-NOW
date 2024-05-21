import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLocationStore = defineStore('locationStore', () => {
  // 초기 위치는 멀티캠퍼스
  const coords = ref({
    latitude: 37.5015109,
    longitude: 127.0397022,
  })

  navigator.geolocation.getCurrentPosition((position) => {
    const { latitude, longitude } = position.coords
    coords.value.latitude = latitude
    coords.value.longitude = longitude
  })

  return { coords }
})
