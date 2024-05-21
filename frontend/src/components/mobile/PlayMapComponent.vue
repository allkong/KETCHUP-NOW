<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env
import FindSpot from '@/components/mobile/modal/FindSpot.vue'

const [messageApi, contextHolder] = message.useMessage()
const mapContainer = ref(null)
let mapInstance = null
let currentPositionMarker = null
const modalOpen = ref(false)

const loadKakaoMap = (container) => {
  const script = document.createElement('script')
  script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${VITE_KAKAO_MAP_KEY}&autoload=false`
  document.head.appendChild(script)
  script.addEventListener('load', () => {
    window.kakao.maps.load(() => {
      const options = {
        center: new window.kakao.maps.LatLng(37.5789, 126.977), // 지도 중심 좌표
        level: 3, // 지도 확대 레벨
        maxLevel: 5, // 지도 축소 제한 레벨
      }

      mapInstance = new window.kakao.maps.Map(container, options) // 지도 생성
    })

    displayCurrentPosition()
  })
}

let positionInterval = null
const displayCurrentPosition = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
      const lat = position.coords.latitude
      const lng = position.coords.longitude
      const currentPosition = new window.kakao.maps.LatLng(lat, lng)

      // 현재 위치 마커 생성
      if (!currentPositionMarker) {
        currentPositionMarker = new window.kakao.maps.Marker({
          map: mapInstance,
          position: currentPosition,
        })
      }
      // 위치 이동하면 마커 업데이트
      else {
        currentPositionMarker.setPosition(currentPosition)
      }

      mapInstance.setCenter(currentPosition)
      positionInterval = setInterval(() => {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            let lat = position.coords.latitude
            let lng = position.coords.longitude
            let newPosition = new window.kakao.maps.LatLng(lat, lng)

            // console.log('현재 위치:', lat, lng)
            // messageApi.info(`현재 위치: ${lat}, ${lng}`)
            currentPositionMarker.setPosition(newPosition)
            mapInstance.setCenter(newPosition)

            // 이동평균
            // 마커 부드럽게
          },
          (error) => {
            console.error('위치 업데이트 에러:', error)
          },
        )
      }, 1000)
    })
  } else {
    console.log('geolocation 사용 불가')
  }
}

onMounted(() => {
  loadKakaoMap(mapContainer.value)
})

onUnmounted(async () => {
  if (positionInterval) {
    clearInterval(positionInterval)
  }
})

const closeFindSpot = () => {
  modalOpen.value = false
}
</script>

<template>
  <FindSpot v-if="modalOpen" :modal-open="modalOpen" @close-modal="closeFindSpot" />
  <div id="map-wrap">
    <div ref="mapContainer" style="height: 100%">
      <!-- <context-holder /> -->
    </div>
  </div>
</template>

<style scoped></style>
