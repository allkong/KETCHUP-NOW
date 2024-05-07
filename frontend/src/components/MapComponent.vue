<script setup>
import { ref, onMounted, inject } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const axios = inject('axios')

const mapInfo = {
  listRequestDto: {
    page: 1,
    size: 10,
    orderKey: 'createdAt',
    orderDirection: 'ASC',
  },
  'left-bottom-latitude': 0,
  'left-bottom-longitude': 0,
  'right-top-latitude': 0,
  'right-top-longitude': 0,
}

const getAttractions = (mapInfo) => {
  axios.get('/attractions').then((response) => console.log(response))
}

const mapContainer = ref(null)
let mapInstance = null
const markers = []

const positions = [
  {
    title: '장소1',
    latitude: 33.450936,
    longitude: 126.569477,
  },
  {
    title: '장소2',
    latitude: 33.450879,
    longitude: 126.56994,
  },
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
        center: new window.kakao.maps.LatLng(37.5789, 126.977), // 지도 중심 좌표
        level: 3, // 지도 확대 레벨
        maxLevel: 5, // 지도 축소 제한 레벨
      }
      mapInstance = new window.kakao.maps.Map(container, options) // 지도 생성

      mapInfo['left-bottom-latitude'] = mapInstance
        .getBounds()
        .getSouthWest().Ma
      mapInfo['left-bottom-longitude'] = mapInstance
        .getBounds()
        .getSouthWest().La
      mapInfo['right-top-latitude'] = mapInstance.getBounds().getNorthEast().Ma
      mapInfo['right-top-longitude'] = mapInstance.getBounds().getNorthEast().La

      getAttractions(mapInfo)

      // 마커 추가
      positions.forEach((position) => {
        const markerPosition = new window.kakao.maps.LatLng(
          position.latitude,
          position.longitude
        )
        const marker = new window.kakao.maps.Marker({
          position: markerPosition,
          title: position.title,
        })
        markers.push(marker)
      })
    })
  }
}

// 관광지 마커 표시하기/끄기
const toggleMarkers = () => {
  markers.forEach((marker) => {
    if (marker.getMap() === mapInstance) {
      marker.setMap(null)
    } else {
      marker.setMap(mapInstance)
    }
  })
}
</script>

<template>
  <div>
    <div ref="mapContainer" style="width: 100%; height: 70vh"></div>
    <button @click="toggleMarkers">관광지</button>
  </div>
</template>

<style scoped></style>
