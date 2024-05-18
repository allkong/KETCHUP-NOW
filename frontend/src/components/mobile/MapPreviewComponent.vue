<script setup>
import { ref, onMounted } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const props = defineProps({
  storyId: {
    type: String,
    required: true,
  },
})

const mapContainer = ref(null)
let mapInstance = null

let spots = ref([])
let spotMarkers = []

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
        level: 3, // 지도 확대 레벨
      }

      mapInstance = new window.kakao.maps.Map(container, options) // 지도 생성

      drawSpotMarkers()

      window.kakao.maps.event.addListener(mapInstance, 'dragend', () => {
        drawSpotMarkers()
      })
    })
  }
}

const drawSpotMarkers = () => {
  let bounds = new window.kakao.maps.LatLngBounds()

  spots.value.forEach((spot) => {
    //
    const markerPosition = new window.kakao.maps.LatLng(spot.lat, spot, lng)

    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
    })

    bounds.extend(markerPosition)

    spotMarkers.push(marker)
    marker.setMap(mapInstance)

    mapInstance.setBounds(bounds)
  })
}
</script>

<template>
  <div id="map-wrap">
    <div ref="mapContainer" style="height: 100%"></div>
  </div>
</template>

<style scoped></style>
