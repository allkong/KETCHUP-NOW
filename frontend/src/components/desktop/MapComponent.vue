<script setup>
import { ref, onMounted, inject } from 'vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const axios = inject('axios')

const mapInfo = {
  page: 1,
  size: 100,
  'left-bottom-latitude': 0,
  'left-bottom-longitude': 0,
  'right-top-latitude': 0,
  'right-top-longitude': 0,
}

const mapContainer = ref(null)
let mapInstance = null
let attractionMarkers = []
let keywordMarkers = []
const selectTag = ref(false)

const props = defineProps({
  keyword: String,
})

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

      // 지도 이동 이벤트
      window.kakao.maps.event.addListener(mapInstance, 'dragend', () => {
        // 관광지 버튼이 활성화인 상태로 지도를 이동하면 이동한 지도 영역의 관광지 마커를 생성
        if (selectTag.value) {
          getAttractions()
        }
      })
    })
  }
}

// 관광지 데이터로 지도에 마커 표시
const getAttractions = () => {
  // 현재 영역의 남서, 북동 위경도 세팅
  mapInfo['left-bottom-latitude'] = mapInstance.getBounds().getSouthWest().Ma
  mapInfo['left-bottom-longitude'] = mapInstance.getBounds().getSouthWest().La
  mapInfo['right-top-latitude'] = mapInstance.getBounds().getNorthEast().Ma
  mapInfo['right-top-longitude'] = mapInstance.getBounds().getNorthEast().La

  // 관광지 데이터 요청
  axios
    .get('/attractions', { params: mapInfo }, { withCredentials: true })
    .then((response) => response.data.data)
    .then((attractions) => {
      // 받아온 관광지들로 마커 생성하여 attractionsMarkers 배열에 추가
      attractions.forEach((attraction) => {
        const markerPosition = new window.kakao.maps.LatLng(
          attraction.latitude,
          attraction.longitude,
        )
        const marker = new window.kakao.maps.Marker({
          position: markerPosition,
          title: attraction.title,
        })
        attractionMarkers.push(marker)
        marker.setMap(mapInstance)
      })
    })
    .catch((error) => console.error(error))
}

// 모든 마커 지도에서 없애기
const removeAllMarkers = (markers) => {
  markers.forEach((marker) => marker.setMap(null))
  markers = []
}

// 관광지 버튼 활성화/비활성화
const handleChange = () => {
  if (selectTag.value) {
    getAttractions()
  } else {
    removeAllMarkers(attractionMarkers)
  }
}

// 키워드 검색
const searchByKeyword = (keyword) => {
  const places = new window.kakao.maps.services.Places()

  places.keywordSearch(keyword, (result, status) => {
    if (status === window.kakao.maps.services.Status.OK) {
      removeAllMarkers() // 기존 마커 제거
      // 검색 결과 마커 생성
      result.forEach((attraction) => {
        const markerPosition = new window.kakao.maps.LatLng(attraction.y, attraction.x)
        const marker = new window.kakao.maps.Marker({
          position: markerPosition,
          title: attraction.place_name,
        })
        keywordMarkers.push(marker) // 마커 배열에 추가
        marker.setMap(mapInstance) // 지도에 마커 표시
      })
    } else {
      console.error('키워드 검색 실패:', status)
    }
  })
}
</script>

<template>
  <div id="map-wrap">
    <div ref="mapContainer" style="width: 100%; height: 100vh"></div>
    <a-checkable-tag
      class="map-button attraction-button"
      v-model:checked="selectTag"
      @change="() => handleChange()"
    >
      관광지
    </a-checkable-tag>
  </div>
</template>

<style scoped>
.attraction-button {
  left: 1rem;
}

.keyword-box {
  top: 3rem;
}
</style>
