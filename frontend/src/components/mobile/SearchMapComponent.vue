<script setup>
import { ref, onMounted, inject, watch } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env
import StoryMarkerIcon from '@/assets/icon/marker/flag-marker-red.png'
import StoryModal from '@/components/mobile/modal/StoryModal.vue'
import SearchListModal from '@/components/mobile/modal/SearchListModal.vue'
import RegionButton from '@/components/button/RegionButton.vue'

import latLngByArea from '@/assets/data/latlng-by-area.json'

const axios = inject('axios')
const props = defineProps({
  initialFocusStoryUuid: {
    type: String,
    required: false,
  },
})

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

const storyModalOpen = ref(false)
const searchListModalOpen = ref(false)
const clickedMarker = ref()
const selectedStory = ref(null)

const sido = ref(null)
const gungu = ref(null)

onMounted(async () => {
  let initialFocusStory = null
  if (props.initialFocusStoryUuid) {
    initialFocusStory = (await axios.get(`/stories/${props.initialFocusStoryUuid}`)).data
  }
  await loadKakaoMap(mapContainer.value, initialFocusStory)
})

const loadKakaoMap = async (container, initialFocusStory) => {
  const script = document.createElement('script')
  script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${VITE_KAKAO_MAP_KEY}&autoload=false`
  document.head.appendChild(script)

  let initialLatitude = 37.578
  let initialLongitude = 126.976

  script.onload = async () => {
    window.kakao.maps.load(async () => {
      // 초깃값이 주어졌으면 해당 스토리의 첫 번째 스팟 위치로 화면 이동
      if (initialFocusStory) {
        const spots = (await axios.get(`/stories/${initialFocusStory.uuid}/spots`)).data
        spots.sort((s1, s2) => s1.orderIdx - s2.orderIdx)
        initialLatitude = spots[0].latitude
        initialLongitude = spots[0].longitude
      }

      const options = {
        center: new window.kakao.maps.LatLng(initialLatitude, initialLongitude), // 지도 중심 좌표
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
      if (initialFocusStory) {
        // 초깃값이 주어졌으면 해당 스토리 모달 자동 열기
        storyModalOpen.value = true
        selectedStory.value = initialFocusStory
      }
    })
  }
}

const drawStoryMarkers = () => {
  stories.value.forEach((place) => {
    const markerPosition = new window.kakao.maps.LatLng(place[0], place[1])

    const storyMarkerImage = new window.kakao.maps.MarkerImage(
      StoryMarkerIcon,
      new window.kakao.maps.Size(35, 35),
      { offset: new window.kakao.maps.Point(17, 35) },
    )

    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
      image: storyMarkerImage,
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
  storyModalOpen.value = false
}

const closeSearchListModal = () => {
  searchListModalOpen.value = false
}

const openSearchListModal = () => {
  searchListModalOpen.value = true
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
      const firstSpot = spots.reduce((prev, cur) => (prev.orderIndex < cur.orderIndex ? prev : cur))
      const markerPosition = new window.kakao.maps.LatLng(firstSpot.latitude, firstSpot.longitude)
      const storyMarkerImage = new window.kakao.maps.MarkerImage(
        StoryMarkerIcon,
        new window.kakao.maps.Size(50, 50),
        { offset: new window.kakao.maps.Point(25, 50) },
      )
      const marker = new window.kakao.maps.Marker({
        position: markerPosition,
        image: storyMarkerImage,
        clickable: true,
      })

      window.kakao.maps.event.addListener(marker, 'click', () => {
        clickedMarker.value = marker
        storyModalOpen.value = true
        selectedStory.value = story
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

function onStorySelectedInListModal(...args) {
  selectedStory.value = args[0]
  searchListModalOpen.value = false
  storyModalOpen.value = true
}
</script>

<template>
  <StoryModal
    v-if="storyModalOpen"
    :modal-open="storyModalOpen"
    :clicked-marker="clickedMarker"
    :story="selectedStory"
    @close-story-modal="closeStoryModal"
  />
  <SearchListModal
    v-if="searchListModalOpen"
    :modal-open="searchListModalOpen"
    @close-search-list-modal="closeSearchListModal"
    :stories="stories"
    @story-selected="onStorySelectedInListModal"
  />
  <div id="map-wrap">
    <div ref="mapContainer" style="height: 100%"></div>
    <a-button class="map-button list-button" @click="openSearchListModal">목록</a-button>
    <RegionButton class="map-button region-button" @area-select-event="onAreaFilterUpdate" />
  </div>
</template>

<style scoped>
.list-button {
  right: 1rem;
}

.region-button {
  left: 1rem;
}
</style>
