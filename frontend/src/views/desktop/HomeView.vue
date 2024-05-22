<script setup>
import { onMounted, ref } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import { useLocationStore } from '@/stores/location'
import HeaderView from '@/views/desktop/includes/HeaderView.vue'
const { VITE_APP_MODE } = import.meta.env

const route = useRoute()

let sticky = ref();
let stickyParent = ref();

onMounted(async () => {
    // Adding scroll event listener
    document.addEventListener('scroll', horizontalScroll);

    //Selecting Elements
    sticky.value = document.querySelector('.sticky');
    stickyParent.value = document.querySelector('.sticky-parent');
    let scrollWidth = sticky.value.scrollWidth;
    let verticalScrollHeight = stickyParent.value.getBoundingClientRect().height - sticky.value.getBoundingClientRect().height;

    //Scroll function 
    function horizontalScroll() {
        //Checking whether the sticky element has entered into view or not
        let stickyPosition = sticky.value.getBoundingClientRect().top;
        if (stickyPosition > 1) {
            return;
        } else {
            let scrolled = stickyParent.value.getBoundingClientRect().top; //how much is scrolled?
            sticky.value.scrollLeft = (scrollWidth / verticalScrollHeight) * (-scrolled) * 0.85;
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
  <HeaderView />
  <div class="sticky-parent">
    <div class="sticky">
      <div class="horizontal">
        <div id="first-page" class="dim" style="background-color: ghostwhite;">
          <div class="logo-text-container">
            <div class="logo-text" id="logo-text-ketchup">KETCHUP,</div>
            <div>
              <img src="@/assets/logo.png" alt="Jumping Image" class="jumping-image" ref="jumpingImage">
            </div>
            <div class="logo-text" id="logo-text-now">NOW!</div>
          </div>
          <div id="scroll-right">
            <div>SCROLL DOWN</div>
          </div>
        </div>
        <div class="dim" style="background-color: ghostwhite;">
          <div class="logo-text-container-right">
            <div class="logo-text" id="logo-text-to-travel">여행에</div>
            <div class="logo-text" id="logo-text-add-story">이야기를 더하다!</div>
          </div>
        </div>
        <div class="dim" style="background-color: ghostwhite;">
          <div id="attraction-img-container">
            <img src="@/assets/img/landing-page/1.png" alt="">
            <img src="@/assets/img/landing-page/2.png" alt="">
            <img src="@/assets/img/landing-page/3.png" alt="">
            <img src="@/assets/img/landing-page/4.png" alt="">
            <img src="@/assets/img/landing-page/5.png" alt="">
          </div>
        </div>
        <div class="dim" style="background-color: rgb(247, 0, 255);"></div>
        <div class="dim" style="background-color: rgb(27, 24, 179);"></div>
        <div class="dim" style="background-color:black"></div>
      </div>
    </div>
  </div>
  <div class="tomato-line"></div>
</template>

<style scoped></style>
