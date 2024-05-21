<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router'
import draggable from 'vuedraggable'
const { VITE_KAKAO_MAP_KEY } = import.meta.env
import { message, Modal } from 'ant-design-vue'
import {
  SearchOutlined,
  FlagOutlined,
  RobotOutlined,
  VerticalAlignBottomOutlined,
  EditOutlined,
  PlusSquareOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import AttractionMarkerIcon from '@/assets/icon/marker/star-marker-sky.png'
import SelectedAttractionMarkerIcon from '@/assets/icon/marker/star-marker-blue.png'
import KeywordMarkerIcon from '@/assets/icon/marker/star-marker-orange.png'
import SelectedKeywordMarkerIcon from '@/assets/icon/marker/star-marker-pink.png'
import AddSpotModal from '@/components/desktop/AddSpotModal.vue'
import AddSpotEventModal from '@/components/desktop/AddSpotEventModal.vue'
import DefaultImage from '@/assets/default-image.jpg'
import AIStoryGenerationBoard from '@/components/desktop/AIStoryGenerationBoard.vue'

const route = useRoute()
const router = useRouter()
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

const attractionList = ref([])
let attractionMarkers = []
const selectAttractionTag = ref(false)

let places = null
const placeList = ref([])
let keywordMarkers = []
let selectedPlaceMarker = null

const isAddSpotModalOpen = ref(false)
const isAddSpotEventModalOpen = ref(false)

const clickedMarker = ref()
const clickedSpot = ref()
const spots = ref([])
const MAX_SPOT_COUNT = 10
let spotMarkers = []
let spotMarkerPolyline = null

const story = ref({
  status: 'PUBLISHED',
})

onMounted(() => {
  loadKakaoMap(mapContainer.value)
  fetchStory()
})

async function fetchStory() {
  axios.get(`/stories/${route.params.uuid}`).then((resp) => {
    story.value = resp.data
  })
}

const onLeftCollapse = (collapsed) => {
  leftCollapsed.value = collapsed
}

const onRightCollapse = (collapsed) => {
  rightCollapsed.value = collapsed
}

const openLeftSider = () => {
  leftCollapsed.value = false
}

const onExit = () => {
  leftCollapsed.value = true
  router.push({ name: 'my-stories' })
}

onBeforeRouteLeave((to, from, next) => {
  Modal.confirm({
    title: '정말 나가시겠습니까?',
    content: '현재 페이지를 벗어나면 변경 사항이 저장되지 않을 수 있으니 꼭 저장해 주세요!',
    okText: '그래도 나갈래요',
    cancelText: '안나갈래요',
    onOk() {
      next()
    },
    onCancel() {
      next(false)
    },
  })
})

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

      onRightCollapse(true)
      fetchSpots().then(() => {
        drawSpotMarkers()
      })

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

        marker.placeData = { ...attraction, placeType: 'attraction' }
        attraction.marker = marker

        window.kakao.maps.event.addListener(marker, 'click', () => {
          if (spots.value.length <= MAX_SPOT_COUNT) {
            clickedMarker.value = marker
            isAddSpotModalOpen.value = true
          } else {
            message.error('스팟은 최대 10개까지만 담을 수 있어요.')
          }
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
  if (keyword.value === '') {
    removeAllMarkers(keywordMarkers)
    return
  }
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

        marker.placeData = { ...place, placeType: 'keyword' }

        window.kakao.maps.event.addListener(marker, 'click', () => {
          if (spots.value.length <= MAX_SPOT_COUNT) {
            clickedMarker.value = marker
            isAddSpotModalOpen.value = true
          } else {
            message.error('스팟은 최대 10개까지만 담을 수 있어요.')
          }
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

const moveToPlaceLocation = (place) => {
  const newPosition = new window.kakao.maps.LatLng(place.y, place.x)
  mapInstance.setCenter(newPosition)

  if (selectedPlaceMarker) {
    resetMarkerImage(selectedPlaceMarker)
  }

  // 새로 선택한 마커 이미지 변경
  const selectedKeywordMarkerImage = new window.kakao.maps.MarkerImage(
    SelectedKeywordMarkerIcon,
    new window.kakao.maps.Size(45, 45),
    { offset: new window.kakao.maps.Point(23, 35) },
  )
  place.marker.setImage(selectedKeywordMarkerImage)
  place.marker.setZIndex(1)
  selectedPlaceMarker = place.marker

  if (selectAttractionTag.value) {
    getAttractions()
  }
}

const moveToAttractionLocation = (attraction) => {
  const newPosition = new window.kakao.maps.LatLng(attraction.latitude, attraction.longitude)
  mapInstance.setCenter(newPosition)

  if (selectedPlaceMarker) {
    resetMarkerImage(selectedPlaceMarker)
  }

  // 새로 선택한 마커 이미지 변경
  const selectedAttractionMarkerImage = new window.kakao.maps.MarkerImage(
    SelectedAttractionMarkerIcon,
    new window.kakao.maps.Size(45, 45),
    { offset: new window.kakao.maps.Point(23, 35) },
  )
  attraction.marker.setImage(selectedAttractionMarkerImage)
  attraction.marker.setZIndex(1)
  selectedPlaceMarker = attraction.marker
}

const resetMarkerImage = (marker) => {
  const type = marker.placeData.placeType
  if (type === 'keyword') {
    // 기존에 선택한 마커 이미지 되돌리기
    const keywordMarkerImage = new window.kakao.maps.MarkerImage(
      KeywordMarkerIcon,
      new window.kakao.maps.Size(35, 35),
      { offset: new window.kakao.maps.Point(17, 35) },
    )
    selectedPlaceMarker.setImage(keywordMarkerImage)
  } else if (type === 'attraction') {
    // 기존에 선택한 마커 이미지 되돌리기
    const attractionMarkerImage = new window.kakao.maps.MarkerImage(
      AttractionMarkerIcon,
      new window.kakao.maps.Size(35, 35),
      { offset: new window.kakao.maps.Point(17, 35) },
    )
    selectedPlaceMarker.setImage(attractionMarkerImage)
  }
}

const onCloseAddSpotModal = () => {
  isAddSpotModalOpen.value = false
}

// 스팟 등록 후 스팟 목록 업데이트
const onUpdateSpots = () => {
  fetchSpots()
  isAddSpotModalOpen.value = false
}

const onCloseAddSpotEventModal = () => {
  isAddSpotEventModalOpen.value = false
}

const fetchSpots = async () => {
  return axios.get(`/stories/${route.params.uuid}/spots`).then((response) => {
    spots.value = response.data
    spots.value.sort((a, b) => a.orderIndex - b.orderIndex)
  })
}

const onChangeSpot = (e) => {
  const targetSpotElement = e.moved.element
  const previousSpotIndex = e.moved.newIndex - 1
  let previousSpotUuid = null

  // 스팟 순서를 첫 번째로 이동하면 previousSpotUuid는 null
  if (e.moved.newIndex !== 0) {
    previousSpotUuid = spots.value[previousSpotIndex].uuid
  }

  axios
    .put(`stories/${route.params.uuid}/spots/${targetSpotElement.uuid}`, {
      previousSpotUuid: previousSpotUuid,
      latitude: targetSpotElement.latitude,
      longitude: targetSpotElement.longitude,
      title: targetSpotElement.title,
      description: targetSpotElement.description,
      eventType: targetSpotElement.eventType,
    })
    .then((response) => {
      fetchSpots().then(() => {
        // 기존에 그려져 있던 스팟 마커와 경로 모두 삭제
        removeAllMarkers(spotMarkers)
        spotMarkers = []
        spotMarkerPolyline.setMap(null)
        spotMarkerPolyline = null

        // 다시 그리기
        drawSpotMarkers()
      })
    })
    .catch((error) => console.error(error))
}

async function drawSpotMarkers() {
  let bounds = new window.kakao.maps.LatLngBounds()
  const polylinePaths = []

  spots.value.sort((s1, s2) => {
    return s1.orderIndex - s2.orderIndex
  })

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

    marker.spotUuid = spot.uuid

    bounds.extend(markerPosition)

    spotMarkers.push(marker)
    marker.setMap(mapInstance)
    mapInstance.setBounds(bounds)
  })

  spotMarkerPolyline = new window.kakao.maps.Polyline({
    map: mapInstance,
    path: polylinePaths,
    strokeWeight: 2,
    strokeColor: 'red',
    strokeOpacity: 0.8,
    strokeStyle: 'solid',
  })
}

const focusToSpotMarker = (spot) => {
  for (let spotMarker of spotMarkers) {
    if (spot.uuid === spotMarker.spotUuid) {
      mapInstance.setCenter(new window.kakao.maps.LatLng(spot.latitude, spot.longitude))
    }
  }
}

const onEidtSpot = (spot) => {}

const onAddSpotEvent = (spot) => {}

const onDeleteSpot = (spot) => {
  // /stories/f03c3bb1-0f83-11ef-bec7-0242ac110002/spots/f03e2f02-0f83-11ef-bec7-0242ac110002
  // axios
  //   .delete(`/stories/${route.params.uuid}/spots/${spot.uuid}`)
  //   .then((response) => {
  //     const spotIndex = spots.value.indexOf(spot)
  //     spots.value.splice(spotIndex, 1)
  //     // 기존에 그려져 있던 스팟 마커와 경로 모두 삭제
  //     removeAllMarkers(spotMarkers)
  //     spotMarkers = []
  //     spotMarkerPolyline.setMap(null)
  //     spotMarkerPolyline = null
  //     // 다시 그리기
  //     drawSpotMarkers()
  //   })
  //   .catch((error) => console.error(error))
}
</script>

<template>
  <!-- <a-layout-header> </a-layout-header> -->
  <AddSpotModal
    v-if="isAddSpotModalOpen"
    :modal-open="isAddSpotModalOpen"
    :place="clickedMarker.placeData"
    :last-spot-uuid="spots.length > 0 ? spots[spots.length - 1].uuid : null"
    @close-add-spot-modal="onCloseAddSpotModal"
    @update-spots="onUpdateSpots"
  />
  <AddSpotEventModal
    v-if="isAddSpotEventModalOpen"
    :modal-open="isAddSpotEventModalOpen"
    :spot="clickedSpot"
    @close-add-spot-event-modal="onCloseAddSpotEventModal"
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
        <a-menu-item key="3" @click="openLeftSider" v-if="story.status === 'WRITING'">
          <RobotOutlined />
        </a-menu-item>
        <a-menu-item key="4" @click="onExit">
          <VerticalAlignBottomOutlined style="transform: rotate(90deg)" />
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
        <div v-show="selectedKeys[0] === '1'" class="full-height">
          <h2 style="text-align: center">키워드 검색</h2>
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
          <a-row class="full-height">
            <a-card class="sider-cards">
              <a-card-grid
                v-for="place in placeList"
                :key="place.id"
                class="attraction-item"
                @click="moveToPlaceLocation(place)"
              >
                <h3>{{ place.place_name }}</h3>
                <p>{{ place.address_name }}</p>
                <p>{{ place.road_address_name }}</p>
                <p>{{ place.phone }}</p>
              </a-card-grid>
            </a-card>
          </a-row>
        </div>
        <div v-show="selectedKeys[0] === '2'" class="full-height">
          <h2 style="text-align: center">관광지 목록</h2>
          <a-card class="sider-cards">
            <p v-show="attractionList.length === 0">관광지 버튼을 클릭해 주세요!</p>
            <a-card-grid
              v-for="attraction in attractionList"
              :key="attraction.id"
              class="attraction-item"
              @click="moveToAttractionLocation(attraction)"
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
        <div v-show="selectedKeys[0] === '3'" class="full-height">
          <AIStoryGenerationBoard :spots="spots" />
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
        <h2>담은 스팟</h2>
        <draggable
          :disabled="story.status === 'PUBLISHED'"
          v-model="spots"
          item-key="orderIndex"
          class="sider-cards"
          @change="onChangeSpot"
        >
          <template #item="{ element }">
            <a-card hoverable class="spot-card">
              <a-row style="margin: 1rem" @click="focusToSpotMarker(element)">
                <a-col :span="8">
                  <img
                    :src="element.thumnailUri || DefaultImage"
                    alt=""
                    class="spot-cover-image"
                    @error="replaceDefaultImage"
                  />
                </a-col>
                <a-col :span="16" class="card-text">
                  <a-card-meta :title="element.title" :description="element.description">
                  </a-card-meta>
                </a-col>
              </a-row>
              <a-divider class="horizontal-divider" />

              <a-row align="middle" justify="center" class="card-actions">
                <a-col :span="8" class="full-height" @click.stop="onEditSpot(element)">
                  <div class="action-container">
                    <EditOutlined />
                  </div>
                </a-col>
                <a-col :span="8" class="full-height" @click.stop="onAddSpotEvent(element)">
                  <div class="action-container middle-border">
                    <PlusSquareOutlined />
                  </div>
                </a-col>
                <a-col :span="8" class="full-height" @click.stop="onDeleteSpot(element)">
                  <div class="action-container">
                    <DeleteOutlined />
                  </div>
                </a-col>
              </a-row>
            </a-card>
          </template>
        </draggable>
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
  padding: 1rem;
  height: 100%;
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

.sider-cards {
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

.spot-card {
  width: 100%;
  /* height: 8rem; */
  margin-bottom: 0.5rem;
}

:deep(.spot-card .ant-card-body) {
  height: 100%;
  padding: 0;
}

.spot-cover-image {
  width: 100%;
  height: 5rem;
  object-fit: cover;
}

.card-text {
  padding-left: 1rem;
  height: 100%;
}

:deep(.ant-card-meta-description) {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.horizontal-divider {
  margin: 0;
  height: 1px;
  background-color: #e8e8e8;
}

.card-actions {
  display: flex;
  justify-content: space-around;
  height: 3rem;
}

card-actions {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin: 1rem 0;
}

.action-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.middle-border {
  position: relative;
}

.middle-border::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  height: 50%;
  border-left: 1px solid #e8e8e8;
  border-right: 1px solid #e8e8e8;
}

.full-height {
  height: 100%;
}
</style>
