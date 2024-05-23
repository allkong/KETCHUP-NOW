<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import { useLocationStore } from '@/stores/location'
const { VITE_APP_MODE } = import.meta.env

const pressedKeys = new Set()

const locationStore = useLocationStore()
if (VITE_APP_MODE === 'DEBUG') {
  console.log('VIRTUAL LOCATION MODE ENABLED')
  const MOVING_UNIT = 0.0001

  window.addEventListener(
    'keydown',
    (e) => {
      pressedKeys.add(e.key)
      if (pressedKeys.has('Shift')) {
        if (pressedKeys.has('ArrowRight')) {
          locationStore.coords.longitude += MOVING_UNIT
        } else if (pressedKeys.has('ArrowLeft')) {
          locationStore.coords.longitude -= MOVING_UNIT
        } else if (pressedKeys.has('ArrowUp')) {
          locationStore.coords.latitude += MOVING_UNIT
        } else if (pressedKeys.has('ArrowDown')) {
          locationStore.coords.latitude -= MOVING_UNIT
        }
      }
    },
    false,
  )
  window.addEventListener('keyup', (e) => {
    pressedKeys.delete(e.key)
  })
}
</script>

<!-- 
  [ Antdv Layout ]
  https://antdv.com/components/layout
 -->
<template>
  <a-layout>
    <RouterView />
  </a-layout>
</template>

<style>
body {
  margin: 0;
}

@font-face {
  font-family: 'Pretendard';
  src: url('https://fastly.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Regular.woff')
    format('woff');
  font-weight: 400;
  font-style: normal;
}

@font-face {
  font-family: 'KCC-Hanbit';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/2403-2@1.0/KCC-Hanbit.woff2')
    format('woff2');
  font-weight: normal;
  font-style: normal;
}

.ant-layout :not(#logo-text-ketchup, #logo-text-now),
.ant-modal,
.ant-col,
.ant-tabs,
.ant-card,
.ant-btn {
  font-family: 'Pretendard';
}

.ant-menu-horizontal,
.ant-menu-item,
.ant-menu-item::after {
  border: none !important;
  color: inherit !important;
}

/* 스크롤바 컨테이너 스타일 */
body::-webkit-scrollbar {
  width: 0.6rem;
}

/* 스크롤바 핸들 스타일 */
body::-webkit-scrollbar-thumb {
  background: rgb(255, 131, 117);
  border-radius: 0.5rem;
}

/* 스크롤바 트랙 스타일 */
body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 1rem;
}
</style>
