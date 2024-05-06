<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'KakaoMap',
  setup() {
    const mapContainer = ref(null)

    onMounted(() => {
      loadKakaoMap(mapContainer.value)
    })

    const loadKakaoMap = (container) => {
      const apiKey = import.meta.env.VITE_KAKAO_MAP_KEY
      const script = document.createElement('script')
      script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}&autoload=false`
      document.head.appendChild(script)

      script.onload = () => {
        window.kakao.maps.load(() => {
          const options = {
            center: new window.kakao.maps.LatLng(37.501286, 127.0396029), // 지도 중심 좌표
            level: 2, // 지도 확대 레벨
            maxLevel: 4, // 지도 축소 제한 레벨
          }
          const mapInstance = new window.kakao.maps.Map(container, options)

          // 마커 표시
          const markerPosition = new window.kakao.maps.LatLng(
            37.501286,
            127.0396029
          )
          const marker = new window.kakao.maps.Marker({
            position: markerPosition,
          })
          marker.setMap(mapInstance)
        })
      }
    }

    return { mapContainer }
  },
}
</script>

<template>
  <div ref="mapContainer" style="width: 100%; height: 70vh"></div>
</template>

<style scoped></style>
