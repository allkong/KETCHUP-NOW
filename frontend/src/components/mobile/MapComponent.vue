<script setup>
import { ref, onMounted } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const mapContainer = ref(null)
let mapInstance = null

onMounted(() => {
  loadKakaoMap(mapContainer.value)
})

const loadKakaoMap = (container) => {
  const script = document.createElement('script')
  script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${VITE_KAKAO_MAP_KEY}&autoload=false`
  document.head.appendChild(script)

  script.onload = () => {
    window.kakao.maps.load(() => {
      const options = {
        center: new window.kakao.maps.LatLng(37.5789, 126.977), // 지도 중심 좌표
        level: 5, // 지도 확대 레벨
        maxLevel: 5, // 지도 축소 제한 레벨
      }

      mapInstance = new window.kakao.maps.Map(container, options) // 지도 생성

      // HTML5의 geolocation 사용 가능한지 확인
      if (navigator.geolocation) {
        // 접속 위치 얻어오기
        navigator.geolocation.getCurrentPosition((position) => {
          let lat = position.coords.latitude
          let lng = position.coords.longitude

          let currentPosition = new window.kakao.maps.LatLng(lat, lng)

          displayCurrentMarker(currentPosition)
        })
      } else {
        console.log('geolocation 사용 불가')
      }
    })
  }
}

const displayCurrentMarker = (currentPosition) => {
  const marker = new window.kakao.maps.Marker({
    map: mapInstance,
    position: currentPosition,
  })

  mapInstance.setCenter(currentPosition)
}
</script>

<template>
  <div ref="mapContainer"></div>
</template>

<style scoped></style>
