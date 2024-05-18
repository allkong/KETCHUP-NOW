<script setup>
import { ref, onMounted, watch, inject } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const axios = inject('axios')

const props = defineProps({
  story: {
    type: Object,
    required: true,
  },
})

const mapContainer = ref(null)
let mapInstance = null

let spots = ref([])
let spotMarkers = []

onMounted(() => {
  loadKakaoMap(mapContainer.value)
  fetchSpots(props.story.uuid)
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

      fetchSpots(props.story.uuid)
      drawSpotMarkers()
    })
  }
}

const drawSpotMarkers = () => {
  let bounds = new window.kakao.maps.LatLngBounds()
  const polylinePaths = []

  spots.value.sort((s1, s2) => {
    return s1.orderIndex - s2.orderIndex
  })

  console.log(spots.value)
  spots.value.forEach((spot, idx) => {
    const markerIcon = new window.kakao.maps.MarkerImage(
      `/icon/numbers/spot-on-map-${idx + 1}.png`,
      new window.kakao.maps.Size(23, 23),
      {
        offset: new window.kakao.maps.Point(12, 15),
        shape: 'poly',
      },
    )

    const markerPosition = new window.kakao.maps.LatLng(spot.latitude, spot.longitude)
    polylinePaths.push(markerPosition)

    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
      image: markerIcon,
    })

    bounds.extend(markerPosition)

    spotMarkers.push(marker)
    marker.setMap(mapInstance)
    mapInstance.setBounds(bounds)
  })

  const polyline = new window.kakao.maps.Polyline({
    map: mapInstance,
    path: polylinePaths,
    strokeWeight: 2,
    strokeColor: 'red',
    strokeOpacity: 0.8,
    strokeStyle: 'solid',
  })
}

function fetchSpots(storyUuid) {
  axios.get(`/stories/${storyUuid}/spots`).then((resp) => {
    spots.value = resp.data
  })
}
</script>

<template>
  <div id="map-wrap">
    <div ref="mapContainer" style="height: 100%"></div>
  </div>
</template>

<style scoped></style>
