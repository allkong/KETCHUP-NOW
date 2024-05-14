<script setup>
import { ref, onMounted } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env
import StoryModal from '@/components/mobile/modal/StoryModal.vue'
import RegionButton from '@/components/mobile/button/RegionButton.vue'

const mapContainer = ref(null)
let mapInstance = null
let markers = []
const places = [
  [37.5788222356, 126.9769930325],
  [37.576072552, 126.9768042386],
  [37.5766530058, 126.974893286],
]
const modalOpen = ref(false)
const clickedMarker = ref()

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

      getStories()

      window.kakao.maps.event.addListener(mapInstance, 'dragend', () => {
        getStories()
      })

      markers.forEach((marker) => {
        window.kakao.maps.event.addListener(marker, 'click', () => {
          // console.log(marker)
          modalOpen.value = true
          clickedMarker.value = marker
        })
      })
    })
  }
}

const getStories = () => {
  places.forEach((place) => {
    const markerPosition = new window.kakao.maps.LatLng(place[0], place[1])

    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
    })

    markers.push(marker)
    marker.setMap(mapInstance)
  })
}

const closeStoryModal = () => {
  modalOpen.value = false
}
</script>

<template>
  <StoryModal
    v-if="modalOpen"
    :modal-open="modalOpen"
    :clicked-marker="clickedMarker"
    @close-modal="closeStoryModal"
  />
  <div id="map-wrap">
    <div ref="mapContainer" style="height: 100%"></div>
    <a-button class="map-button list-button">목록</a-button>
    <RegionButton />
  </div>
</template>

<style scoped>
.list-button {
  left: 1rem;
}
</style>
