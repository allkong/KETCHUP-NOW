<script setup>
import { ref, computed, onMounted } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const mapContainer = ref(null)
let mapInstance = null
let markers = []
const places = [
  [37.5788222356, 126.9769930325],
  [37.576072552, 126.9768042386],
  [37.5766530058, 126.974893286],
]

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
        center: new window.kakao.maps.LatLng(37.578, 126.976), // 지도 중심 좌표
        level: 3, // 지도 확대 레벨
        maxLevel: 5, // 지도 축소 제한 레벨
      }

      mapInstance = new window.kakao.maps.Map(container, options) // 지도 생성

      getStorys()

      window.kakao.maps.event.addListener(mapInstance, 'dragend', () => {
        getStorys()
      })

      markers.forEach((marker) => {
        window.kakao.maps.event.addListener(marker, 'click', () => {
          console.log(marker)
        })
      })
    })
  }
}

const getStorys = () => {
  places.forEach((place) => {
    const markerPosition = new window.kakao.maps.LatLng(place[0], place[1])

    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
    })

    markers.push(marker)
    marker.setMap(mapInstance)
  })
}

// 지도 높이 구하기
const customMapHeight = computed(() => {
  return `--map-height: ${window.innerHeight - 94}px`
})
</script>

<template>
  <div :style="customMapHeight" class="map-style">
    <div ref="mapContainer" style="height: 100%"></div>
  </div>
</template>

<style scoped>
.map-style {
  height: var(--map-height);
}
</style>
