<script setup>
const { VITE_KAKAO_MAP_KEY, VITE_APP_MODE } = import.meta.env
import { ref, onMounted, onUnmounted, watch, inject, computed, createVNode, h } from 'vue'
import { message, Modal, FloatButton } from 'ant-design-vue'
import { useLocationStore } from '@/stores/location'
import HttpStatus from '@/api/http-status'
import { CrownOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import SpotEventModal from '@/components/mobile/modal/SpotEventModal.vue'
import CurrentPositionMarkerIcon from '@/assets/icon/marker/current-marker.gif'

const axios = inject('axios')
const router = useRouter()

const [messageApi, contextHolder] = message.useMessage()
const mapContainer = ref(null)
let mapInstance = null
let currentPositionMarker = null
let inRangeTargetMarker = null

const isSpotEventModalOpen = ref(false)

function getHaversineMeter(lat1, lon1, lat2, lon2) {
  // 라디안 단위로 변환하는 함수
  function toRadians(degrees) {
    return (degrees * Math.PI) / 180
  }

  // 지구의 반지름 (미터)
  const R = 6371000

  // 위경도를 라디안 단위로 변환
  const φ1 = toRadians(lat1)
  const φ2 = toRadians(lat2)
  const Δφ = toRadians(lat2 - lat1)
  const Δλ = toRadians(lon2 - lon1)

  // 하버사인 공식 적용
  const a =
    Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
    Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

  // 두 점 사이의 거리 계산
  const distance = R * c

  return distance
}

function isAround(currentLat, currentLng, spot) {
  const meterDistance = getHaversineMeter(
    currentLat,
    currentLng,
    spot.value.latitude,
    spot.value.longitude,
  )

  return meterDistance <= 30.0
}

function updateGame({ latitude, longitude }) {
  // 남은 스팟이 있고
  if (nextTargetSpot) {
    const isTargetAround = isAround(latitude, longitude, nextTargetSpot)
    // 근처에 있으며
    if (isTargetAround) {
      // 하이라이팅이 만들어져 있지 않다면
      if (!inRangeTargetMarker) {
        // 첫 진입이므로 화면에 표시
        const markerIcon = new window.kakao.maps.MarkerImage(
          // '/icon/numbers/spot-on-map-10.png',
          '/icon/active-spot.gif',
          new window.kakao.maps.Size(60, 60),
          {
            offset: new window.kakao.maps.Point(30.5, 32.5),
          },
        )
        inRangeTargetMarker = new window.kakao.maps.Marker({
          position: new window.kakao.maps.LatLng(
            nextTargetSpot.value.latitude,
            nextTargetSpot.value.longitude,
          ),
          image: markerIcon,
        })

        window.kakao.maps.event.addListener(inRangeTargetMarker, 'click', () => {
          isSpotEventModalOpen.value = true
        })

        // 마커에 이벤트 수행 물어보는 이벤트 등록
        inRangeTargetMarker.setMap(mapInstance)
        message.info('이벤트 발생!')
      }
    }
    // 또는, 근처에 없으나
    else {
      // 하이라이팅이 화면에 남아 있다면, 제거
      if (inRangeTargetMarker) {
        inRangeTargetMarker.setMap(null)
        inRangeTargetMarker = null
      }
    }
  }

  // 사용자 위치 갱신 =========================================================
  const currentPosition = new window.kakao.maps.LatLng(latitude, longitude)
  mapInstance.setCenter(currentPosition)

  if (currentPositionMarker !== null) {
    currentPositionMarker.setMap(null)
  }

  const currentPositionMarkerImage = new window.kakao.maps.MarkerImage(
    CurrentPositionMarkerIcon,
    new window.kakao.maps.Size(39, 39),
    { offset: new window.kakao.maps.Point(19, 39) },
  )

  // 현재 위치 마커 생성
  currentPositionMarker = new window.kakao.maps.Marker({
    map: mapInstance,
    position: currentPosition,
    image: currentPositionMarkerImage,
  })
  // 위치 이동하면 마커 업데이트
  currentPositionMarker.setPosition(currentPosition)
  // 사용자 위치 갱신 =========================================================
}

const onSpotEventClear = () => {
  isSpotEventModalOpen.value = false

  axios
    .post('/playings/current/clear', {
      spotUuid: nextTargetSpot.value.uuid,
      jsonEventContent: '',
    })
    .then(async (resp) => {
      return fetchPlayLogs()
    })
    .then(async (logs) => {
      drawSpotMarkers()

      // 스팟 활성화 하이라이팅 삭제
      inRangeTargetMarker.setMap(null)
      inRangeTargetMarker = null

      // 클리어 스팟 목록 갱신
      fetchPlayLogs()
        .then(() => {
          message.success('이벤트 클리어! 다음 이벤트로 이동하세요 🎉')
        })
        .catch((error) => {
          return Promise.resolve(error)
        })
    })
    .catch((error) => {
      // 만약 다음 타겟 스팟이 없으면 게임이 종료되었다는 의미
      if (
        error.response.status === HttpStatus.CONFLICT &&
        error.response.data.detailCode === 'E0005'
      ) {
        Modal.confirm({
          title: '스토리 클리어!',
          icon: () => createVNode(CrownOutlined),
          content: '긴 여정이 끝났습니다. 여행자님의 생생한 후기를 들려주세요!',
          content: () =>
            h('div', {}, [
              h('div', '긴 여정이 끝났습니다.'),
              h('div', '여행자님의 생생한 후기를 들려 주세요!'),
            ]),
          okText: '좋아요 😍',
          onOk: async () => {
            const playings = (await axios.get('/playings')).data
            const storyPlayingUuid = playLogs.value[0].storyPlayingUuid
            const currentPlaying = playings.filter((p) => p.uuid === storyPlayingUuid)[0]
            router.push({
              name: 'story:review:register',
              params: { storyUuid: currentPlaying.storyUuid },
            })
          },
          cancelText: '쉬고 싶어요 😅',
          onCancel: () => {
            message.info('플레이 해주셔서 감사합니다!')
            router.push({ name: 'story:cleared-list' })
          },
        })
      }
    })
}

const locationStore = useLocationStore()
if (VITE_APP_MODE === 'DEBUG') {
  watch(locationStore.coords, () => {
    updateGame(locationStore.coords)
  })
}

const playLogs = ref([])
const spots = ref([])
let spotMarkers = []
let clearedSpotMarkerPolyline = null
let unclearedSpotMarkerPolyline = null

let animationFrameId = null

const nextTargetSpot = computed(() => {
  // 배치 순서대로 정렬
  playLogs.value.sort((l1, l2) => l1.createdAt - l2.createdAt)

  // 아직 아무 스팟도 클리어 하지 않았다면
  if (playLogs.value.length === 0) {
    // 첫 번째 스팟 반환
    return spots.value[0]
  } else {
    // 마지막으로 클리어한 스팟의 다음 스팟을 찾음
    const lastPlayLog = playLogs.value[playLogs.value.length - 1]
    for (let spotIdx = 0; spotIdx < spots.value.length; spotIdx++) {
      const spot = spots.value[spotIdx]
      // 마지막으로 클리어한 스팟을 발견
      if (spot.uuid === lastPlayLog.spotUuid) {
        // 해당 스팟이 마지막 스팟이었다면, 아무 것도 반환하지 않음 (다음 목표 없음)
        if (spotIdx === spots.value.length - 1) {
          return null
        }
        // 해당 스팟이 마지막 스팟이 아니라면, 그 다음 스팟을 반환
        else {
          return spots.value[spotIdx + 1]
        }
      }
    }
    return null
  }
})

async function fetchPlayLogs() {
  return axios
    .get(`/playings/current/logs`)
    .then((resp) => {
      playLogs.value = resp.data
      return Promise.resolve(resp.data)
    })
    .catch((error) => {
      return Promise.reject(error)
    })
}

async function fetchSpots() {
  const playingInfo = (await axios.get('/playings/now')).data
  return axios
    .get(`/stories/${playingInfo.storyUuid}/spots`)
    .then((resp) => {
      spots.value = resp.data
      drawSpotMarkers()
      return Promise.resolve(resp.data)
    })
    .catch((error) => {
      return Promise.reject(error)
    })
}

async function drawSpotMarkers() {
  let bounds = new window.kakao.maps.LatLngBounds()
  const clearedSpotMarkerPositions = []
  const unclearedSpotMarkerPositions = []

  spots.value.sort((s1, s2) => {
    return s1.orderIndex - s2.orderIndex
  })

  let foundNextTarget = false
  spots.value.forEach((spot, idx) => {
    if (nextTargetSpot.value && spot.uuid === nextTargetSpot.value.uuid) {
      foundNextTarget = true
    }

    const markerPosition = new window.kakao.maps.LatLng(spot.latitude, spot.longitude)
    if (foundNextTarget) {
      clearedSpotMarkerPositions.push(markerPosition)
    } else {
      unclearedSpotMarkerPositions.push(markerPosition)
    }

    const iconPath = foundNextTarget
      ? `/icon/numbers/spot-on-map-${idx + 1}.png`
      : `/icon/numbers/cleared-spot-on-map-${idx + 1}.png`

    const markerIcon = new window.kakao.maps.MarkerImage(
      iconPath,
      new window.kakao.maps.Size(23, 23),
      {
        offset: new window.kakao.maps.Point(12, 15),
      },
    )

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

  if (clearedSpotMarkerPositions.length > 0) {
    unclearedSpotMarkerPositions.push(clearedSpotMarkerPositions[0])
  }

  if (clearedSpotMarkerPolyline) {
    clearedSpotMarkerPolyline.setMap(null)
    clearedSpotMarkerPolyline = null
  }
  clearedSpotMarkerPolyline = new window.kakao.maps.Polyline({
    map: mapInstance,
    path: clearedSpotMarkerPositions,
    strokeWeight: 2,
    strokeColor: 'red',
    strokeOpacity: 0.8,
    strokeStyle: 'solid',
  })

  if (unclearedSpotMarkerPolyline) {
    unclearedSpotMarkerPolyline.setMap(null)
    unclearedSpotMarkerPolyline = null
  }
  unclearedSpotMarkerPolyline = new window.kakao.maps.Polyline({
    map: mapInstance,
    path: unclearedSpotMarkerPositions,
    strokeWeight: 2,
    strokeColor: 'gray',
    strokeOpacity: 0.8,
    strokeStyle: 'solid',
  })
}

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

      if (VITE_APP_MODE === 'DEBUG') {
        const newCenter = new window.kakao.maps.LatLng(
          locationStore.coords.latitude,
          locationStore.coords.longitude,
        )
        mapInstance.setCenter(newCenter)

        if (currentPositionMarker !== null) {
          currentPositionMarker.setMap(null)
        }

        const currentPositionMarkerImage = new window.kakao.maps.MarkerImage(
          CurrentPositionMarkerIcon,
          new window.kakao.maps.Size(39, 39),
          { offset: new window.kakao.maps.Point(19, 39) },
        )

        // 현재 위치 마커 생성
        currentPositionMarker = new window.kakao.maps.Marker({
          map: mapInstance,
          position: newCenter,
          image: currentPositionMarkerImage,
        })
        // 위치 이동하면 마커 업데이트
        currentPositionMarker.setPosition(newCenter)
      } else {
        startSyncPositionAndMarker()
      }
    })
  })
}

let positionInterval = null
const startSyncPositionAndMarker = () => {
  const updatePosition = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          let { latitude, longitude } = position.coords
          updateGame({ latitude, longitude })
          animationFrameId = requestAnimationFrame(updatePosition)
        },
        (error) => {
          console.error('위치 업데이트 에러:', error)
        },
      )
    } else {
      console.log('geolocation 사용 불가')
    }
  }

  animationFrameId = requestAnimationFrame(updatePosition)
}

function stopSyncPositionAndMarker() {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
    animationFrameId = null
  }
}

async function onPlayingGiveUpBtnClicked() {
  Modal.confirm({
    title: '플레이 중단',
    content: () =>
      h('div', {}, [
        h('div', '플레이 기록이 모두 사라지며, 되돌릴 수 없어요.'),
        h('div', '그래도 종료하시겠어요?'),
      ]),
    okText: '네',
    onOk: async () => {
      axios.delete('/playings/now').then(() => {
        message.success('플레이가 중단되었어요.')
        router.push({ name: 'search' })
      })
    },
    cancelText: '아니오',
  })
}

onMounted(async () => {
  loadKakaoMap(mapContainer.value)
  fetchPlayLogs().then((fetchedLogs) => {
    playLogs.value = fetchedLogs
  })
  fetchSpots().then((fetchedSpots) => {
    spots.value = fetchedSpots
  })
})

onUnmounted(async () => {
  if (positionInterval) {
    clearInterval(positionInterval)
  }
})

const onCloseSpotEventModal = () => {
  stopSyncPositionAndMarker()
}
</script>

<template>
  <SpotEventModal
    v-if="isSpotEventModalOpen"
    :modal-open="isSpotEventModalOpen"
    :spot="nextTargetSpot"
    @close-spot-event-modal="onCloseSpotEventModal"
    @spot-event-clear="onSpotEventClear"
  />
  <div id="map-wrap">
    <div ref="mapContainer" style="height: 100%">
      <!-- <context-holder /> -->
    </div>
    <FloatButton @click="onPlayingGiveUpBtnClicked">
      <template #icon>
        <DeleteOutlined />
      </template>
    </FloatButton>
  </div>
</template>

<style scoped>
.ant-float-btn {
  background-color: tomato;
  bottom: 7rem;
}
</style>
