<script setup>
import { ref, onMounted, inject } from 'vue'
import { SearchOutlined, FlagOutlined } from '@ant-design/icons-vue'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const axios = inject('axios')

const leftCollapsed = ref(false)
const rightCollapsed = ref(false)
const selectedKeys = ref(['1'])
const keyword = ref('')

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
let places = null
let attractionMarkers = []
let keywordMarkers = []
const selectTag = ref(false)

onMounted(() => {
  loadKakaoMap(mapContainer.value)
})

const onRightCollapse = (collapsedValue) => {
  rightCollapsed.value = collapsedValue
}

const loadKakaoMap = (container) => {
  const script = document.createElement('script')
  script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${VITE_KAKAO_MAP_KEY}&autoload=false&libraries=services`
  document.head.appendChild(script)

  script.onload = () => {
    window.kakao.maps.load(() => {
      const options = {
        center: new window.kakao.maps.LatLng(37.5789, 126.977), // 지도 중심 좌표
        level: 3, // 지도 확대 레벨
        maxLevel: 5, // 지도 축소 제한 레벨
      }

      mapInstance = new window.kakao.maps.Map(container, options) // 지도 생성
      places = new window.kakao.maps.services.Places()

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
  const bounds = mapInstance.getBounds()
  mapInfo['left-bottom-latitude'] = bounds.getSouthWest().getLat()
  mapInfo['left-bottom-longitude'] = bounds.getSouthWest().getLng()
  mapInfo['right-top-latitude'] = bounds.getNorthEast().getLat()
  mapInfo['right-top-longitude'] = bounds.getNorthEast().getLng()

  // 관광지 데이터 요청
  axios
    .get('/attractions', { params: mapInfo }, { withCredentials: true })
    .then((response) => response.data.data)
    .then((attractions) => {
      // 받아온 관광지들로 마커 생성하여 attractionsMarkers 배열에 추가
      removeAllMarkers(attractionMarkers)
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
  markers.length = 0
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
const searchByKeyword = () => {
  places.keywordSearch(keyword.value, (result, status) => {
    console.log(result)
    if (status === window.kakao.maps.services.Status.OK) {
      removeAllMarkers(keywordMarkers)

      let bounds = new window.kakao.maps.LatLngBounds()

      result.forEach((place) => {
        const placePosition = new window.kakao.maps.LatLng(place.y, place.x)
        const marker = new window.kakao.maps.Marker({
          position: placePosition,
          title: place.place_name,
        })

        // LatLngBounds 객체에 좌표 추가
        bounds.extend(placePosition)

        // 마커 표시
        keywordMarkers.push(marker)
        marker.setMap(mapInstance)
        // 지도 범위 재설정
        mapInstance.setBounds(bounds)
      })
    } else {
      console.error(status)
    }
  })
}
</script>

<template>
  <a-layout-header>
    <!-- <a-row align="middle">
      <a-col>
        <img src="@/assets/logo.png" class="logo-image" />
      </a-col>
    </a-row> -->
  </a-layout-header>
  <a-layout>
    <a-layout-sider v-model:collapsed="leftCollapsed" width="20rem" class="left-sider-shadow">
      <a-menu v-model:selectedKeys="selectedKeys" theme="light" mode="inline">
        <a-menu-item key="1">
          <SearchOutlined />
          <span>키워드</span>
        </a-menu-item>
        <a-menu-item key="2">
          <FlagOutlined />
          <span>관광지</span>
        </a-menu-item>
      </a-menu>
      <!-- <div v-show="!leftCollapsed">
        <a-row justify="center">
          <a-col>
            <a-input v-model:value="keyword" placeholder="키워드 검색" />
          </a-col>
          <a-col>
            <a-button type="primary" ghost @click="searchByKeyword">검색</a-button>
          </a-col>
        </a-row>
        <a-row>
          <ul id="placesList"></ul>
        </a-row>
      </div> -->
    </a-layout-sider>

    <a-layout-content>
      <div id="map-wrap">
        <div ref="mapContainer" style="width: 100%; height: 100vh"></div>
        <a-checkable-tag
          class="map-button attraction-button"
          v-model:checked="selectTag"
          @change="handleChange"
        >
          관광지
        </a-checkable-tag>
      </div>
    </a-layout-content>

    <a-layout-sider
      breakpoint="lg"
      collapsed-width="0"
      :collapsed="rightCollapsed"
      @collapse="onRightCollapse"
      :class="{ 'right-sider-shadow': !rightCollapsed }"
      collapsible
      reverseArrow
    >
      내 스팟
    </a-layout-sider>
  </a-layout>
</template>

<style scoped>
.logo-image {
  height: 6vh;
}

.a-layout-sider {
  z-index: 10;
}

.ant-layout-header {
  background: lightgray;
}

.ant-layout-sider {
  background: white;
}

.left-sider-shadow {
  box-shadow: 5px 0 5px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.right-sider-shadow {
  box-shadow: -5px 0 5px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.attraction-button {
  left: 1rem;
}

.keyword-box {
  top: 3rem;
}
</style>
