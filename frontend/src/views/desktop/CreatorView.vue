<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute } from 'vue-router'
import { SearchOutlined, FlagOutlined, RobotOutlined } from '@ant-design/icons-vue'
import AttractionMarkerIcon from '@/assets/icon/marker/star-marker-blue.png'
import KeywordMarkerIcon from '@/assets/icon/marker/star-marker-orange.png'
import SelectedKeywordMarkerIcon from '@/assets/icon/marker/star-marker-pink.png'
import AddSpotModal from '@/components/desktop/AddSpotModal.vue'
import DefaultImage from '@/assets/default-image.jpg'
const { VITE_KAKAO_MAP_KEY } = import.meta.env

const route = useRoute()
const axios = inject('axios')

const leftCollapsed = ref(false)
const rightCollapsed = ref(true)
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

const attractionList = ref([])
let attractionMarkers = []
const selectAttractionTag = ref(false)

let places = null
const placeList = ref([])
let keywordMarkers = []
let selectedKeywordMarker = null

const isAddSpotModalOpen = ref(false)
const spots = ref([])

onMounted(() => {
  onRightCollapse(true)
  loadKakaoMap(mapContainer.value)
})

const onLeftCollapse = (collapsed) => {
  leftCollapsed.value = collapsed
}

const onRightCollapse = (collapsed) => {
  rightCollapsed.value = collapsed
}

const openLeftSider = () => {
  leftCollapsed.value = false
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

      fetchSpots()

      // 지도 이동 이벤트
      window.kakao.maps.event.addListener(mapInstance, 'dragend', () => {
        // 관광지 버튼이 활성화인 상태로 지도를 이동하면 이동한 지도 영역의 관광지 마커를 생성
        if (selectAttractionTag.value) {
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
      attractionList.value = attractions
      console.log(attractionList.value)
      // 받아온 관광지들로 마커 생성하여 attractionsMarkers 배열에 추가
      removeAllMarkers(attractionMarkers)
      attractions.forEach((attraction) => {
        const markerPosition = new window.kakao.maps.LatLng(
          attraction.latitude,
          attraction.longitude,
        )

        const attractionMarkerImage = new window.kakao.maps.MarkerImage(
          AttractionMarkerIcon,
          new window.kakao.maps.Size(35, 35),
          { offset: new window.kakao.maps.Point(17, 35) },
        )

        const marker = new window.kakao.maps.Marker({
          position: markerPosition,
          title: attraction.title,
          image: attractionMarkerImage,
        })

        window.kakao.maps.event.addListener(marker, 'click', () => {
          isAddSpotModalOpen.value = true
          console.log('관광지 클릭')
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
  if (selectAttractionTag.value) {
    getAttractions()
  } else {
    removeAllMarkers(attractionMarkers)
    attractionList.value = []
  }
}

// 키워드 검색
const searchByKeyword = () => {
  places.keywordSearch(keyword.value, (data, status, pagination) => {
    if (status === window.kakao.maps.services.Status.OK) {
      removeAllMarkers(keywordMarkers)
      let bounds = new window.kakao.maps.LatLngBounds()

      const keywordMarkerImage = new window.kakao.maps.MarkerImage(
        KeywordMarkerIcon,
        new window.kakao.maps.Size(35, 35),
        { offset: new window.kakao.maps.Point(17, 35) },
      )

      placeList.value = data.map((place) => {
        const placePosition = new window.kakao.maps.LatLng(place.y, place.x)
        const marker = new window.kakao.maps.Marker({
          position: placePosition,
          title: place.place_name,
          image: keywordMarkerImage,
        })

        window.kakao.maps.event.addListener(marker, 'click', () => {
          isAddSpotModalOpen.value = true
          console.log('키워드 클릭')
        })

        // LatLngBounds 객체에 좌표 추가
        bounds.extend(placePosition)

        // 마커 표시
        marker.setMap(mapInstance)
        keywordMarkers.push(marker)

        return {
          ...place,
          marker,
        }
      })

      // 지도 범위 재설정
      mapInstance.setBounds(bounds)

      // 새로운 위치의 관광지 가져오기
      if (selectAttractionTag.value) {
        getAttractions()
      }
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
      alert('검색 결과가 존재하지 않습니다.')
      return
    } else if (status === kakao.maps.services.Status.ERROR) {
      alert('검색 결과 중 오류가 발생했습니다.')
      return
    }
  })
}

const moveToLocation = (place) => {
  const newPosition = new window.kakao.maps.LatLng(place.y, place.x)
  mapInstance.setCenter(newPosition)

  if (selectedKeywordMarker) {
    // 기존에 선택한 마커 이미지 되돌리기
    const keywordMarkerImage = new window.kakao.maps.MarkerImage(
      KeywordMarkerIcon,
      new window.kakao.maps.Size(35, 35),
      { offset: new window.kakao.maps.Point(17, 35) },
    )
    selectedKeywordMarker.setImage(keywordMarkerImage)
  }

  // 새로 선택한 마커 이미지 변경
  const selectedKeywordMarkerImage = new window.kakao.maps.MarkerImage(
    SelectedKeywordMarkerIcon,
    new window.kakao.maps.Size(45, 45),
    { offset: new window.kakao.maps.Point(23, 35) },
  )
  place.marker.setImage(selectedKeywordMarkerImage)
  place.marker.setZIndex(1)
  selectedKeywordMarker = place.marker

  if (selectAttractionTag.value) {
    getAttractions()
  }
}

const onCloseAddSpotModal = () => {
  isAddSpotModalOpen.value = false
}

const fetchSpots = () => {
  axios.get(`/stories/${route.params.uuid}/spots`).then((response) => {
    spots.value = response.data
    spots.value.forEach((spot) => console.log(spot))
  })
}
</script>

<template>
  <a-layout-header> </a-layout-header>
  <AddSpotModal
    v-if="isAddSpotModalOpen"
    :modal-open="isAddSpotModalOpen"
    @close-add-spot-modal="onCloseAddSpotModal"
  />
  <a-layout>
    <!-- 좌측 안쪽 사이드바 -->
    <a-layout-sider width="4rem" class="left-inner-sider">
      <a-menu v-model:selectedKeys="selectedKeys" theme="light" mode="inline">
        <a-menu-item key="1" @click="openLeftSider">
          <SearchOutlined />
        </a-menu-item>
        <a-menu-item key="2" @click="openLeftSider">
          <FlagOutlined />
        </a-menu-item>
        <a-menu-item key="3">
          <RobotOutlined />
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <!-- 좌측 바깥쪽 사이드바 -->
    <a-layout-sider
      breakpoint="lg"
      collapsed-width="0"
      width="20rem"
      :collapsed="leftCollapsed"
      @collapse="onLeftCollapse"
      collapsible
      class="left-sider-shadow"
    >
      <div class="sider-content">
        <div v-show="selectedKeys[0] === '1'" style="height: 100%">
          <h2>키워드 검색</h2>
          <a-row justify="center">
            <a-col>
              <div>
                <a-input
                  v-model:value="keyword"
                  placeholder="키워드 검색"
                  @keyup.enter="searchByKeyword"
                >
                  <template #prefix>
                    <SearchOutlined
                      style="color: tomato; margin-right: 0.5rem"
                      @click="searchByKeyword"
                    />
                  </template>
                </a-input>
              </div>
            </a-col>
          </a-row>
          <a-row style="height: 100%">
            <a-card class="left-outer-sider-cards">
              <a-card-grid
                v-for="place in placeList"
                :key="place.id"
                class="attraction-item"
                @click="moveToLocation(place)"
              >
                <h3>{{ place.place_name }}</h3>
                <p>{{ place.address_name }}</p>
                <p>{{ place.road_address_name }}</p>
                <p>{{ place.phone }}</p>
              </a-card-grid>
            </a-card>
          </a-row>
        </div>
        <div v-show="selectedKeys[0] === '2'" style="height: 100%">
          <h2>관광지 목록</h2>
          <a-card class="left-outer-sider-cards">
            <p v-show="attractionList.length === 0">관광지 버튼을 클릭해 주세요!</p>
            <a-card-grid
              v-for="attraction in attractionList"
              :key="attraction.id"
              class="attraction-item"
            >
              <img
                :src="attraction.secondImageUrl"
                @error="$replaceDefaultImage"
                style="width: 10rem"
              />
              <h3>{{ attraction.title }}</h3>
              <p>{{ attraction.address }}</p>
            </a-card-grid>
          </a-card>
        </div>
      </div>
    </a-layout-sider>
    <!-- 지도 -->
    <a-layout-content>
      <div id="map-wrap">
        <div ref="mapContainer" style="width: 100%; height: 100vh"></div>
        <a-checkable-tag
          class="map-button attraction-button"
          v-model:checked="selectAttractionTag"
          @change="handleChange"
        >
          관광지
        </a-checkable-tag>
      </div>
    </a-layout-content>
    <!-- 오른쪽 사이드바 -->
    <a-layout-sider
      breakpoint="lg"
      collapsed-width="0"
      width="20rem"
      :collapsed="rightCollapsed"
      @collapse="onRightCollapse"
      :class="{ 'right-sider-shadow': !rightCollapsed }"
      collapsible
      reverseArrow
    >
      <div class="sider-content">
        <h2>스팟 저장소</h2>
        <div v-for="spot in spots">
          <a-card hoverable style="width: 100%">
            <a-row>
              <a-col :span="10">
                <img
                  style="width: 5rem"
                  :src="spot.thumnailUri || DefaultImage"
                  alt=""
                  @error="$replaceDefaultImage"
                />
              </a-col>
              <a-col :span="14">
                <a-card-meta :title="spot.title" :description="spot.description"> </a-card-meta>
              </a-col>
            </a-row>
          </a-card>
        </div>
      </div>
    </a-layout-sider>
  </a-layout>
</template>

<style scoped>
.logo-image {
  height: 6vh;
}

.ant-layout-header {
  background: lightgray;
}

.ant-layout-sider {
  background: white;
  height: 100vh;
}

.sider-content {
  margin: 1rem;
}

.left-inner-sider {
  border-inline-end: 1px solid rgba(5, 5, 5, 0.06);
}

.ant-menu-light.ant-menu-root.ant-menu-inline {
  border-inline-end: none;
}

.left-sider-shadow {
  box-shadow: 5px 0 5px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.left-outer-sider-cards {
  overflow: auto;
  height: 100%;
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

.attraction-item {
  width: 100%;
}
</style>
