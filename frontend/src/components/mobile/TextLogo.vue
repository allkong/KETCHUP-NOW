<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const props = defineProps({
  align: {
    type: String,
    required: false,
    default: 'center',
  },
})

console.log(props.align)

function goToHome() {
  router.push({ name: 'home' })
}
</script>

<template>
  <div id="logo-text" :style="{ 'text-align': props.align }" @click="goToHome">
    <div id="logo-text-ketchup">KETCHUP</div>
    <div id="logo-text-now">NOW</div>
  </div>
</template>

<style scoped lang="scss">
#logo-text {
  font-family: 'Bagel Fat One' !important;
  color: $primaryColor;
  font-size: xx-large;
  margin-left: 1rem;
  text-align: center;
}
#logo-text > #logo-text-ketchup {
  margin-bottom: -1rem;
}
#logo-text > #logo-text-now {
  color: white;
  -webkit-text-stroke: 1.5px $primaryColor;
}
</style>
