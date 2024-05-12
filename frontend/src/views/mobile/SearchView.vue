<script setup>
import HeaderView from '@/views/mobile/includes/HeaderView.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import SearchMapComponent from '@/components/mobile/SearchMapComponent.vue'
import { ref, onMounted } from 'vue'

const navbar = ref()
const navbarHeight = ref()

onMounted(() => {
  navbarHeight.value = navbar.value.$el.clientHeight
})
</script>

<template>
  <!-- <HeaderView /> -->
  <SearchMapComponent id="search-map-component" :navbar-height="navbarHeight" />
  <NavigationView ref="navbar" />
</template>

<style scoped lang="scss">
#search-map-component {
  height: 100vh - $footerHeight;
}
</style>
