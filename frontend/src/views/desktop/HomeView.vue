<script setup>
import { onMounted, ref } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import { useLocationStore } from '@/stores/location'
const { VITE_APP_MODE } = import.meta.env

const route = useRoute()

let sticky = ref()
let stickyParent = ref()

onMounted(async () => {
  // Adding scroll event listener
  document.addEventListener('scroll', horizontalScroll)

  //Selecting Elements
  sticky.value = document.querySelector('.sticky')
  stickyParent.value = document.querySelector('.sticky-parent')
  let scrollWidth = sticky.value.scrollWidth
  let verticalScrollHeight =
    stickyParent.value.getBoundingClientRect().height - sticky.value.getBoundingClientRect().height

  //Scroll function
  function horizontalScroll() {
    //Checking whether the sticky element has entered into view or not
    let stickyPosition = sticky.value.getBoundingClientRect().top
    if (stickyPosition > 1) {
      return
    } else {
      let scrolled = stickyParent.value.getBoundingClientRect().top //how much is scrolled?
      sticky.value.scrollLeft = (scrollWidth / verticalScrollHeight) * -scrolled * 0.85
    }
  }
})

// 가상 GPS 시뮬레이션 ===========================================================================
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

<template>
  <div class="sticky-parent">
    <div class="sticky">
      <div class="horizontal">
        <div id="first-page" class="dim" style="background-color: ghostwhite">
          <div class="logo-text-container">
            <div class="logo-text" id="logo-text-ketchup">KETCHUP,</div>
            <div>
              <img
                src="@/assets/logo.png"
                alt="Jumping Image"
                class="jumping-image"
                ref="jumpingImage"
              />
            </div>
            <div class="logo-text" id="logo-text-now">NOW!</div>
          </div>
          <div id="scroll-right">
            <div>SCROLL DOWN</div>
          </div>
        </div>
        <div class="dim" style="background-color: ghostwhite">
          <div class="logo-text-container-right">
            <div class="logo-text" id="logo-text-to-travel">여행에</div>
            <div class="logo-text" id="logo-text-add-story">이야기를 더하다!</div>
          </div>
        </div>
        <div class="dim" style="background-color: ghostwhite">
          <div id="attraction-img-container">
            <img src="@/assets/img/landing-page/1.png" alt="" />
            <img src="@/assets/img/landing-page/2.png" alt="" />
            <img src="@/assets/img/landing-page/3.png" alt="" />
            <img src="@/assets/img/landing-page/4.png" alt="" />
            <img src="@/assets/img/landing-page/5.png" alt="" />
          </div>
        </div>
        <div class="dim" style="background-color: rgb(247, 0, 255)"></div>
        <div class="dim" style="background-color: rgb(27, 24, 179)"></div>
        <div class="dim" style="background-color: black"></div>
      </div>
    </div>
  </div>
  <div class="tomato-line"></div>
</template>

<style scoped>
.sticky-parent {
  height: 700vh;
  z-index: 5;
}

.sticky {
  position: sticky;
  top: 0px;
  max-height: 100vh;
  overflow-x: hidden;
  overflow-y: hidden;
}

.horizontal {
  display: flex;
}

.dim {
  display: block;
  min-width: 100vw;
  height: 100vh;
  position: relative;
}

.tomato-line {
  position: fixed;
  bottom: 3rem;
  width: 100%;
  height: 5px;
  background-color: #ff2f2f;
}

.logo-text-container {
  display: flex;
  margin-top: 10vh;
  margin-left: 12vh;
  min-height: 70vh;
  flex-direction: column;
  justify-content: space-between;
  width: 100vw;
}

.logo-text-container-right {
  display: flex;
  margin-top: 10vh;
  margin-left: 12vh;
  min-height: 70vh;
  flex-direction: column;
  justify-content: space-between;
}

.logo-text {
  font-family: 'Bagel Fat One' !important;
  color: #ff2f2f;
  font-size: 7rem;
  margin-top: -1rem;
  margin-bottom: 1rem;
}

#logo-text-ketchup {
  margin-bottom: -1rem;
}

#logo-text-now {
  color: white;
  -webkit-text-stroke: 1.5px #ff2f2f;
  margin-bottom: 5rem;
}

#logo-text-to-travel {
  color: white;
  -webkit-text-stroke: 1.5px #ff2f2f;
}

#logo-text-add-story {
  margin-bottom: -3rem;
}

.jump-animation {
  animation: jump 2s infinite;
}

.jumping-image {
  width: 35vh;
  position: fixed;
  bottom: 33vh;
  transition: left 0.1s linear; /* Smooth movement */
  z-index: 1000;
  animation: jump 3s infinite;
}

@keyframes jump {
  0%,
  25%,
  50%,
  75%,
  100% {
    transform: translateY(0);
  }
  12.5%,
  37.5%,
  62.5%,
  87.5% {
    transform: translateY(-20px); /* Adjust jump height */
  }
}

#first-page {
  display: flex;
  width: 90vh;
}

#login-btn-box {
  display: flex;
  align-items: center;
  margin-right: 5rem;
}

#attraction-img-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100vh;
}

#attraction-img-container > img {
  display: block;
  width: 28vh;
  height: 60vh;
  object-fit: cover;
}

#scroll-right {
  z-index: 500;
  display: flex;
  align-items: center;
}

#scroll-right > * {
  color: silver;
  animation: flicker 1s linear alternate infinite;
}

@keyframes flicker {
  0% {
    filter: brightness(60%);
  }

  100% {
    filter: brightness(100%);
  }
}
</style>
