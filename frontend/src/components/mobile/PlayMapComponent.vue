<script setup>
import { ref, onMounted, onUnmounted, watch, inject, computed } from 'vue'
import { message } from 'ant-design-vue'
const { VITE_KAKAO_MAP_KEY, VITE_APP_MODE } = import.meta.env
import FindSpot from '@/components/mobile/modal/FindSpot.vue'
import { useLocationStore } from '@/stores/location'

const axios = inject('axios')

const [messageApi, contextHolder] = message.useMessage()
const mapContainer = ref(null)
let mapInstance = null
let currentPositionMarker = null
const modalOpen = ref(false)

let inRangeTargetMarker = null

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

const locationStore = useLocationStore()
if (VITE_APP_MODE === 'DEBUG') {
  watch(locationStore.coords, () => {
    // 남은 스팟이 있고
    if (nextTargetSpot) {
      const isTargetAround = isAround(
        locationStore.coords.latitude,
        locationStore.coords.longitude,
        nextTargetSpot,
      )
      // 근처에 있으며
      if (isTargetAround) {
        // 하이라이팅 만들어져 있지 않다면
        if (!inRangeTargetMarker) {
          // 첫 진입이므로 화면에 표시
          const markerIcon = new window.kakao.maps.MarkerImage(
            '/icon/numbers/spot-on-map-10.png',
            // '/icon/active-spot.gif',
            new window.kakao.maps.Size(60, 60),
            {
              offset: new window.kakao.maps.Point(30.5, 32.5),
              shape: 'poly',
            },
          )
          inRangeTargetMarker = new window.kakao.maps.Marker({
            position: new window.kakao.maps.LatLng(
              nextTargetSpot.value.latitude,
              nextTargetSpot.value.longitude,
            ),
            image: markerIcon,
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

    const currentPosition = new window.kakao.maps.LatLng(
      locationStore.coords.latitude,
      locationStore.coords.longitude,
    )
    mapInstance.setCenter(currentPosition)

    if (currentPositionMarker !== null) {
      currentPositionMarker.setMap(null)
    }

    // 현재 위치 마커 생성
    currentPositionMarker = new window.kakao.maps.Marker({
      map: mapInstance,
      position: currentPosition,
    })
    // 위치 이동하면 마커 업데이트
    currentPositionMarker.setPosition(currentPosition)
  })
}

const playLogs = ref([])
const spots = ref([])
let spotMarkers = []
let spotMarkerPolyline = null

const nextTargetSpot = computed(() => {
  // 배치 순서대로 정렬
  playLogs.value.sort((l1, l2) => l1.orderIndex - l2.orderIndex)

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

        // 현재 위치 마커 생성
        currentPositionMarker = new window.kakao.maps.Marker({
          map: mapInstance,
          position: newCenter,
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
