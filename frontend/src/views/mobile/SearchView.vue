<script setup>
import HeaderView from '@/views/mobile/includes/HeaderView.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import SearchMapComponent from '@/components/mobile/SearchMapComponent.vue'
</script>

<template>
  <a-layout-content id="map-size">
    <SearchMapComponent />
  </a-layout-content>
  <NavigationView />
</template>

<style scoped lang="scss"></style>
