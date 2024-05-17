<script setup>
import { ref, onMounted, inject, watch } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env
import StoryModal from '@/components/mobile/modal/StoryModal.vue'
import RegionButton from '@/components/mobile/button/RegionButton.vue'

import latLngByArea from '@/assets/data/latlng-by-area.json'

const axios = inject('axios')

const mapInfo = {
  'left-bottom-latitude': 0,
  'left-bottom-longitude': 0,
  'right-top-latitude': 0,
  'right-top-longitude': 0,
}

const mapContainer = ref(null)
let mapInstance = null

let firstSpotMarkers = []
const stories = ref([])
const spotsByStory = ref({})
let renderedSpotMarkers = []
let renderedStoryPathLine = null

const modalOpen = ref(false)
const clickedMarker = ref()

const sido = ref(null)
const gungu = ref(null)

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
      fetchStories().then(() => {
        drawStoryMarkers()
      })

      window.kakao.maps.event.addListener(mapInstance, 'dragend', () => {
        fetchStories().then(() => {
          drawStoryMarkers()
        })
      })

      firstSpotMarkers.forEach((marker) => {
        window.kakao.maps.event.addListener(marker, 'click', () => {
          // console.log(marker)
          modalOpen.value = true
          clickedMarker.value = marker
        })
      })
    })
  }
}

const drawStoryMarkers = () => {
  stories.value.forEach((place) => {
    const markerPosition = new window.kakao.maps.LatLng(place[0], place[1])

    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
    })

    firstSpotMarkers.push(marker)
    marker.setMap(mapInstance)
  })
}

async function fetchStories() {
  mapInfo['left-bottom-latitude'] = mapInstance.getBounds().getSouthWest().Ma
  mapInfo['left-bottom-longitude'] = mapInstance.getBounds().getSouthWest().La
  mapInfo['right-top-latitude'] = mapInstance.getBounds().getNorthEast().Ma
  mapInfo['right-top-longitude'] = mapInstance.getBounds().getNorthEast().La

  axios
    .get('/stories', {
      params: {
        ...mapInfo,
        sido: sido.value,
        gungu: gungu.value,
      },
    })
    .then((resp) => {
      stories.value = resp.data
    })
}

const closeStoryModal = () => {
  modalOpen.value = false
}

function updateMap(stories) {
  // 기존 마커 모두 제거
  firstSpotMarkers.forEach((marker) => marker.setMap(null))
  // 마커 배열 초기화
  firstSpotMarkers = []

  // 스토리별 스팟 정보 초기화
  spotsByStory.value = {}

  for (let story of stories) {
    axios.get(`/stories/${story.uuid}/spots`).then((resp) => {
      // 스팟 받아와서 순서대로 정렬
      const spots = resp.data
      spots.sort((s1, s2) => s1.orderIdx - s2.orderIdx)

      // 스토리별 스팟 정보 초기화
      spotsByStory.value[story.uuid] = {}
      spotsByStory.value[story.uuid]['story'] = story
      spotsByStory.value[story.uuid]['spots'] = spots

      // 스토리의 첫 마커를 화면에 찍어줌
      const firstSpot = spots.reduce((prev, cur) => (prev.orderIdx < cur.orderIdx ? prev : cur))
      const markerPosition = new window.kakao.maps.LatLng(firstSpot.latitude, firstSpot.longitude)
      const marker = new window.kakao.maps.Marker({
        position: markerPosition,
        clickable: true,
      })

      window.kakao.maps.event.addListener(marker, 'click', () => {
        // // 기존에 스토리가 선택되어 렌더링 되고 있던 스팟을 지워줌
        // renderedSpotMarkers.forEach((marker) => {
        //   marker.setMap(null)
        // })
        // // 새 스팟 마커 그리기
        // renderedSpotMarkers = []
        // for (let spot of spotsByStory.value[story.uuid].spots) {
        //   const spotMarker = new window.kakao.maps.Marker({
        //     position: new window.kakao.maps.LatLng(spot.latitude, spot.longitude),
        //   })
        //   spotMarker.setMap(mapInstance)
        //   renderedSpotMarkers.push(spotMarker)
        // }
        // // 스팟을 잇는 선 초기화
        // if (renderedStoryPathLine != null) {
        //   renderedStoryPathLine.setMap(null)
        // }
        // // 새 경로 선 그리기
        // const linePath = spots.map((s) => new window.kakao.maps.LatLng(s.latitude, s.longitude))
        // renderedStoryPathLine = new kakao.maps.Polyline({
        //   path: linePath,
        //   strokeWeight: 5,
        //   strokeColor: '#FFAE00',
        //   strokeOpacity: 0.7,
        //   strokeStyle: 'solid',
        // })
        // renderedStoryPathLine.setMap(mapInstance)
      })

      firstSpotMarkers.push(marker)
      marker.setMap(mapInstance)
    })
  }
}

watch(stories, async (newStories, oldStories) => {
  updateMap(newStories)
})

// 시/도, 군/구 필터 정보가 바뀌었음을 감지하면,
function onAreaFilterUpdate(...args) {
  // 해당 지역으로 다시 필터링하여 결과를 갱신해줌
  sido.value = args[0]
  gungu.value = args[1]

  // 위치 옮겨주기
  let centerInfo = latLngByArea[sido.value].filter((info) => info.gungu === gungu.value)
  if (centerInfo.length > 0) {
    centerInfo = centerInfo[0]
    mapInstance.setCenter(new window.kakao.maps.LatLng(centerInfo.latitude, centerInfo.longitude))
    mapInstance.setLevel(5)
  }

  fetchStories()
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
    <RegionButton @area-filter-updated="onAreaFilterUpdate" />
  </div>
</template>

<style scoped>
.list-button {
  right: 1rem;
}
</style>
