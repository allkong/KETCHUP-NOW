<script setup>
import { ref, inject, watch, onMounted } from 'vue'
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router'
import draggable from 'vuedraggable'
import HttpStatus from '@/api/http-status'
const { VITE_KAKAO_MAP_KEY } = import.meta.env
import { message, Modal } from 'ant-design-vue'
import {
  FormOutlined,
  SearchOutlined,
  FlagFilled,
  RobotFilled,
  ExportOutlined,
  VerticalAlignBottomOutlined,
  PhoneFilled,
  EditOutlined,
  StarFilled,
  CaretRightFilled,
  EnvironmentFilled,
  PlusSquareOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import AttractionMarkerIcon from '@/assets/icon/marker/star-marker-sky.png'
import SelectedAttractionMarkerIcon from '@/assets/icon/marker/star-marker-blue.png'
import KeywordMarkerIcon from '@/assets/icon/marker/star-marker-orange.png'
import SelectedKeywordMarkerIcon from '@/assets/icon/marker/star-marker-pink.png'
import DefaultImage from '@/assets/default-image.jpg'
import AIStoryGenerationBoard from '@/components/desktop/AIStoryGenerationBoard.vue'

import EditStoryModal from '@/components/desktop/modal/EditStoryModal.vue'
import AddSpotModal from '@/components/desktop/modal/AddSpotModal.vue'
import EditSpotModal from '@/components/desktop/modal/EditSpotModal.vue'
import AddSpotEventModal from '@/components/desktop/modal/AddSpotEventModal.vue'

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

const isEditStoryModalOpen = ref(false)
const isAddSpotModalOpen = ref(false)
const isEditSpotModalOpen = ref(false)
const isAddSpotEventModalOpen = ref(false)
const isDeleting = ref(false)

const clickedMarker = ref()
const clickedSpot = ref()
const spots = ref([])
const MAX_SPOT_COUNT = 10
let spotMarkers = []
let spotMarkerPolyline = null

const story = ref({})

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

watch(leftCollapsed, (newValue) => {
  // 왼쪽 안쪽 사이드바 옵션을 클릭했을 때
  // 왼쪽 바깥쪽 사이드바를 사용하는 옵션이 아니면 열지 못하게 막는다
  if (!newValue && !['1', '2', '3', '4'].includes(selectedKeys.value[0])) {
    leftCollapsed.value = true
    message.info('메뉴를 선택하세요.')
  }
})

const onExit = (routerName) => {
  leftCollapsed.value = true
  router.push({ name: routerName })
}

onBeforeRouteLeave((to, from, next) => {
  // 스토리를 삭제하려는 경우, 모달창을 띄우지 않음
  if (isDeleting.value) {
    next()
  } else {
    Modal.confirm({
      title: '정말 나가시겠습니까?',
      content: '스토리는 자동 저장되니 걱정하지 마세요.',
      okText: '나가기',
      cancelText: '취소',
      onOk() {
        next()
      },
      onCancel() {
        next(false)
      },
    })
  }
})

const loadKakaoMap = (container) => {
  onRightCollapse(true)
  onLeftCollapse(true)

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
    placeList.value = []
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

// 스팟 등록 후 스팟 목록 업데이트
const onUpdateSpots = async () => {
  await fetchSpots()

  // 기존에 그려져 있던 스팟 마커와 경로 모두 삭제
  removeAllMarkers(spotMarkers)
  spotMarkers = []
  spotMarkerPolyline.setMap(null)
  spotMarkerPolyline = null
  // 다시 그리기
  drawSpotMarkers()

  isAddSpotModalOpen.value = false
}

const fetchSpots = async () => {
  return axios.get(`/stories/${route.params.uuid}/spots`).then((response) => {
    spots.value = response.data
    spots.value.sort((a, b) => a.orderIndex - b.orderIndex)
  })
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
      },
    )

    const markerPosition = new window.kakao.maps.LatLng(spot.latitude, spot.longitude)
    polylinePaths.push(markerPosition)

    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
      image: markerIcon,
    })

    marker.spotUuid = spot.uuid
    marker.setZIndex(5)

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

const onPublishStory = () => {
  leftCollapsed.value = true

  Modal.confirm({
    title: '정말 배포하시겠습니까?',
    content: '한 번 배포하면 플레이어를 위해 수정할 수 없어요.',
    okText: '배포',
    cancelText: '취소',
    onOk() {
      axios
        .put(`stories/${route.params.uuid}`, {
          status: 'PUBLISHED',
          title: story.value.title,
          description: story.value.description,
          sido: story.value.sido,
          gungu: story.value.gungu,
        })
        .then((response) => {
          fetchStory()
          message.success('당신의 스토리가 완성되었어요! ✨')
        })
        .catch((error) => console.error(error))
    },
    onCancel() {},
  })
}

const onUpdateStory = () => {
  fetchStory()
  isEditStoryModalOpen.value = false
}

const onEditStoryModal = () => {
  isEditStoryModalOpen.value = true
}

const onCloseEditStoryModal = () => {
  isEditStoryModalOpen.value = false
}

const onDeleteStory = () => {
  Modal.confirm({
    title: '정말 삭제하시겠습니까?',
    content: '한 번 삭제한 스토리는 되돌릴 수 없어요.',
    okText: '삭제',
    cancelText: '취소',
    onOk() {
      isDeleting.value = true
      axios
        .delete(`stories/${route.params.uuid}`)
        .then((response) => {
          message.success('스토리가 삭제되었습니다. 🗑')
          router.push({ name: 'my-stories' })
        })
        .catch((error) => {
          // 플레이 중인 유저가 있으면 스토리 삭제 불가
          if (
            error.response.status === HttpStatus.CONFLICT &&
            error.response.data.detailCode === 'E0003'
          ) {
            message.error('이미 유저가 플레이한 스토리는 삭제할 수 없어요.')
          }
          isDeleting.value = false
        })
    },
    onCancel() {},
  })
}

const focusToSpotMarker = (spot) => {
  for (let spotMarker of spotMarkers) {
    if (spot.uuid === spotMarker.spotUuid) {
      mapInstance.setCenter(new window.kakao.maps.LatLng(spot.latitude, spot.longitude))
    }
  }
}

const onUpdateEditedSpot = async () => {
  await fetchSpots()
  isEditSpotModalOpen.value = false
}

const onCloseAddSpotModal = () => {
  isAddSpotModalOpen.value = false
}

const onEditSpotModal = (spot) => {
  const spotIndex = spots.value.indexOf(spot)

  if (spotIndex === 0) {
    spot.previousSpotUuid = null
  } else {
    spot.previousSpotUuid = spots.value[spotIndex - 1].uuid
  }

  clickedSpot.value = spot
  isEditSpotModalOpen.value = true
}

const onCloseEditSpotModal = () => {
  isEditSpotModalOpen.value = false
}

const onAddSpotEventModal = (spot) => {
  const spotIndex = spots.value.indexOf(spot)

  if (spotIndex === 0) {
    spot.previousSpotUuid = null
  } else {
    spot.previousSpotUuid = spots.value[spotIndex - 1].uuid
  }

  clickedSpot.value = spot
  isAddSpotEventModalOpen.value = true
}

const onUpdateSpotEvent = () => {
  fetchSpots()
  isAddSpotEventModalOpen.value = false
}

const onCloseAddSpotEventModal = () => {
  isAddSpotEventModalOpen.value = false
}

const onDeleteSpot = (spot) => {
  axios
    .delete(`/stories/${route.params.uuid}/spots/${spot.uuid}`)
    .then((response) => {
      const spotIndex = spots.value.indexOf(spot)
      spots.value.splice(spotIndex, 1)
      // 기존에 그려져 있던 스팟 마커와 경로 모두 삭제
      removeAllMarkers(spotMarkers)
      spotMarkers = []
      spotMarkerPolyline.setMap(null)
      spotMarkerPolyline = null
      // 다시 그리기
      drawSpotMarkers()
    })
    .catch((error) => console.error(error))
}
</script>

<template>
  <EditStoryModal
    v-if="isEditStoryModalOpen"
    :modal-open="isEditStoryModalOpen"
    :story="story"
    @close-edit-story-modal="onCloseEditStoryModal"
    @update-story="onUpdateStory"
  />
  <AddSpotModal
    v-if="isAddSpotModalOpen"
    :modal-open="isAddSpotModalOpen"
    :place="clickedMarker.placeData"
    :last-spot-uuid="spots.length > 0 ? spots[spots.length - 1].uuid : null"
    @close-add-spot-modal="onCloseAddSpotModal"
    @update-spots="onUpdateSpots"
  />
  <EditSpotModal
    v-if="isEditSpotModalOpen"
    :modal-open="isEditSpotModalOpen"
    :spot="clickedSpot"
    @close-edit-spot-modal="onCloseEditSpotModal"
    @update-edited-spot="onUpdateEditedSpot"
  />
  <AddSpotEventModal
    v-if="isAddSpotEventModalOpen"
    :modal-open="isAddSpotEventModalOpen"
    :spot="clickedSpot"
    @close-add-spot-event-modal="onCloseAddSpotEventModal"
    @update-spot-event="onUpdateSpotEvent"
  />
  <a-layout>
    <!-- 좌측 안쪽 사이드바 -->
    <a-layout-sider width="4.5rem" class="left-inner-sider">
      <a-menu v-model:selectedKeys="selectedKeys" theme="light">
        <div style="height: 5rem; border-bottom: 1px solid #e8e8e8 !important">
          <a-menu-item key="0" @click="onExit('home')" id="home-item">
            <img src="@/assets/logo.png" alt="" style="width: 2.5rem; height: 2.5rem" />
          </a-menu-item>
        </div>
        <a-menu-item key="1" @click="openLeftSider">
          <a-row justify="center">
            <a-col :span="24" class="center-content">
              <FormOutlined />
            </a-col>
            <a-col :span="24" class="center-content">
              <span>스토리</span>
            </a-col>
          </a-row>
        </a-menu-item>
        <a-menu-item v-if="story.status === 'WRITING'" key="2" @click="openLeftSider">
          <a-row justify="center">
            <a-col :span="24" class="center-content">
              <SearchOutlined />
            </a-col>
            <a-col :span="24" class="center-content">
              <span>키워드</span>
            </a-col>
          </a-row>
        </a-menu-item>
        <a-menu-item v-if="story.status === 'WRITING'" key="3" @click="openLeftSider">
          <a-row justify="center">
            <a-col :span="24" class="center-content">
              <FlagFilled />
            </a-col>
            <a-col :span="24" class="center-content">
              <span>관광지</span>
            </a-col>
          </a-row>
        </a-menu-item>
        <a-menu-item v-if="story.status === 'WRITING'" key="4" @click="openLeftSider">
          <a-row justify="center">
            <a-col :span="24" class="center-content">
              <RobotFilled />
            </a-col>
            <a-col :span="24" class="center-content">
              <span>AI</span>
            </a-col>
          </a-row>
        </a-menu-item>
        <a-menu-item v-if="story.status === 'WRITING'" key="5" @click="onPublishStory">
          <a-row justify="center">
            <a-col :span="24" class="center-content">
              <ExportOutlined />
            </a-col>
            <a-col :span="24" class="center-content">
              <span>배포</span>
            </a-col>
          </a-row>
        </a-menu-item>
        <a-menu-item key="6" @click="onExit('my-stories')">
          <a-row justify="center">
            <a-col :span="24" class="center-content">
              <VerticalAlignBottomOutlined style="transform: rotate(90deg)" />
            </a-col>
            <a-col :span="24" class="center-content">
              <span>나가기</span>
            </a-col>
          </a-row>
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
      <div v-if="selectedKeys[0] === '1'" class="story-info sider-content">
        <h2 style="text-align: center">스토리 정보</h2>
        <div>
          <a-image
            :src="story.imageUri || DefaultImage"
            alt=""
            class="story-cover-image"
            @error="$replaceDefaultImage"
          />
        </div>
        <a-descriptions bordered layout="horizontal" size="middle">
          <a-descriptions-item label="상태" :span="3">{{ story.status }}</a-descriptions-item>
          <a-descriptions-item label="제목" :span="3">{{ story.title }}</a-descriptions-item>
          <a-descriptions-item label="설명" :span="3">{{ story.description }}</a-descriptions-item>
          <a-descriptions-item label="위치" :span="3"
            ><EnvironmentFilled style="color: crimson" /> {{ story.sido }}
            {{ story.gungu }}</a-descriptions-item
          >
          <a-descriptions-item v-if="story.status === 'PUBLISHED'" label="별점" :span="3"
            ><StarFilled style="color: #fadb14" />
            {{ story.averageReviewScore }}</a-descriptions-item
          >
          <a-descriptions-item v-if="story.status === 'PUBLISHED'" label="플레이수" :span="3"
            ><CaretRightFilled style="color: cornflowerblue" />
            {{ story.totalPlayCount }}</a-descriptions-item
          >
        </a-descriptions>
        <a-button
          v-if="story.status === 'WRITING'"
          type="primary"
          danger
          style="margin-top: 1rem; width: 100%"
          @click="onEditStoryModal"
          >수정하기</a-button
        >
        <a-button danger style="margin-top: 1rem; width: 100%" @click="onDeleteStory"
          >삭제하기</a-button
        >
      </div>
      <div v-if="selectedKeys[0] === '2'" class="sider-content">
        <div style="height: 12vh">
          <h2 style="text-align: center">키워드 검색</h2>
          <a-input-search
            v-model:value="keyword"
            placeholder="키워드 검색"
            size="large"
            enter-button
            style="width: 100%; margin-bottom: 1rem"
            @search="searchByKeyword"
          />
        </div>

        <a-card class="sider-cards" style="height: 80vh">
          <p v-show="placeList.length === 0">키워드로 장소를 검색해 보세요!</p>
          <a-card-grid
            v-for="place in placeList"
            :key="place.id"
            class="attraction-item"
            @click="moveToPlaceLocation(place)"
          >
            <h3>{{ place.place_name }}</h3>
            <p>
              <EnvironmentFilled style="color: orange; margin-right: 0.3rem" />{{
                place.road_address_name || place.address_name
              }}
            </p>
            <p v-if="place.phone">
              <PhoneFilled style="color: gray; margin-right: 0.3rem" />{{ place.phone }}
            </p>
          </a-card-grid>
        </a-card>
      </div>
      <div v-if="selectedKeys[0] === '3'" class="sider-content">
        <h2 style="text-align: center">관광지 목록</h2>
        <a-card class="sider-cards">
          <p v-show="attractionList.length === 0">지도 위의 관광지 버튼을 클릭해 주세요!</p>
          <a-card-grid
            v-for="attraction in attractionList"
            :key="attraction.id"
            class="attraction-item"
            @click="moveToAttractionLocation(attraction)"
          >
            <a-image
              :src="attraction.secondImageUrl"
              class="spot-cover-image"
              style="height: 9rem"
              @error="$replaceDefaultImage"
            />
            <h3>{{ attraction.title }}</h3>
            <span
              ><EnvironmentFilled style="color: cornflowerblue; margin-right: 0.3rem" />{{
                attraction.address
              }}</span
            >
          </a-card-grid>
        </a-card>
      </div>
      <div v-if="selectedKeys[0] === '4'" class="sider-content">
        <AIStoryGenerationBoard :spots="spots" :story="story" @refresh-spots="fetchSpots" />
      </div>
    </a-layout-sider>
    <!-- 지도 -->
    <a-layout-content>
      <div id="map-wrap">
        <div ref="mapContainer" style="width: 100%; height: 100vh"></div>
        <a-switch
          v-if="story.status === 'WRITING'"
          v-model:checked="selectAttractionTag"
          checked-children="관광지"
          un-checked-children="관광지"
          class="map-button attraction-button"
          style="top: 1.5rem"
          @change="handleChange"
        />
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
        <h2 style="text-align: center">담은 스팟</h2>
        <p v-show="spots.length === 0">스팟을 생성해 보세요!</p>
        <draggable
          :disabled="story.status === 'PUBLISHED'"
          v-model="spots"
          item-key="orderIndex"
          class="sider-cards"
          @change="onChangeSpot"
        >
          <template #item="{ element, index }">
            <a-card hoverable class="spot-card">
              <a-row style="margin: 1rem" @click="focusToSpotMarker(element)">
                <a-col :span="8">
                  <a-image
                    :src="element.imageUri || DefaultImage"
                    alt=""
                    class="spot-cover-image"
                    style="height: 5rem"
                    @error="$replaceDefaultImage"
                  />
                </a-col>
                <a-col :span="16" class="card-text">
                  <a-card-meta
                    :title="`${index + 1}. ${element.title}`"
                    :description="element.description"
                  >
                  </a-card-meta>
                </a-col>
              </a-row>
              <div v-if="story.status === 'WRITING'">
                <a-divider class="horizontal-divider" />

                <a-row align="middle" justify="center" class="card-actions">
                  <a-col :span="8" class="full-height" @click="onEditSpotModal(element)">
                    <div class="action-container">
                      <EditOutlined />
                    </div>
                  </a-col>
                  <a-col :span="8" class="full-height" @click="onAddSpotEventModal(element)">
                    <div class="action-container middle-border">
                      <PlusSquareOutlined />
                    </div>
                  </a-col>
                  <a-col :span="8" class="full-height" @click="onDeleteSpot(element)">
                    <div class="action-container">
                      <DeleteOutlined />
                    </div>
                  </a-col>
                </a-row>
              </div>
            </a-card>
          </template>
        </draggable>
      </div>
    </a-layout-sider>
  </a-layout>
</template>

<style scoped lang="scss">
.center-content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

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
  border-inline-end: 1px solid #e8e8e8;
}

.left-inner-sider span {
  font-size: 0.75rem;
}

:deep(.ant-menu-item) {
  width: inherit;
  height: 4rem;
  line-height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.left-inner-sider .anticon {
  font-size: 1.2rem;
  margin-bottom: 0.4rem;
}

:deep(#home-item) {
  height: 100%;
  margin: 0;
}

:deep(.ant-menu-item-selected) {
  background: tomato;
  color: white !important;
}

:deep(#home-item.ant-menu-item-selected) {
  background: white;
}

:deep(.ant-menu-title-content) {
  display: flex;
  justify-content: center;
  align-items: center;
}

.ant-menu-light.ant-menu-root.ant-menu-inline {
  border-inline-end: none;
}

.left-sider-shadow {
  box-shadow: 5px 0 5px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

:deep(.ant-image) {
  width: 100%;
}

:deep(.story-cover-image),
:deep(.story-info .ant-image .ant-image-mask) {
  width: 100%;
  height: 15rem;
  object-fit: cover;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
}

.right-sider-shadow {
  box-shadow: -5px 0 5px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.attraction-button {
  left: 2rem;
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

:deep(.spot-cover-image),
:deep(.spot-card .ant-image .ant-image-mask) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0.5rem;
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

/* antdv */
:deep(.ant-btn-primary) {
  background-color: tomato;
}

:deep(.ant-btn-primary:not(:disabled):hover) {
  background-color: salmon;
}

:deep(.ant-input:hover) {
  border-color: tomato;
}

:deep(.ant-input:focus) {
  border-color: tomato;
  box-shadow: 0 0 0 2px rgb(255 5 5 / 10%);
}

.sider-cards {
  overflow: auto;
  height: 90%;
}

/* 스크롤바 컨테이너 스타일 */
.sider-cards::-webkit-scrollbar {
  width: 0.3rem;
}

/* 스크롤바 핸들 스타일 */
.sider-cards::-webkit-scrollbar-thumb {
  background: #cacaca;
  border-radius: 10px;
}

/* 스크롤바 트랙 스타일 */
.sider-cards::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 1rem;
}

:deep(.ant-layout-sider-zero-width-trigger-left) {
  background: rgb(255, 148, 164);
  top: 5rem;
  border-start-end-radius: 2rem;
  border-end-end-radius: 2rem;
}

:deep(.ant-layout-sider-zero-width-trigger-right) {
  background: rgb(255, 148, 164);
  top: 5rem;
  border-start-start-radius: 2rem;
  border-end-start-radius: 2rem;
}

/* 관광지 토글 버튼 */
.ant-switch {
  background: gray;
  transform: scale(1.3);
}

.ant-switch.ant-switch-checked {
  background: #4096ff;
}
</style>
