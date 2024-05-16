<script setup>
import PlayMapComponent from '@/components/mobile/PlayMapComponent.vue'
import HeaderView from '@/views/mobile/includes/HeaderView.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
</script>

<template>
  <!-- <HeaderView /> -->
  <a-layout-content id="map-size">
    <PlayMapComponent />
  </a-layout-content>
  <NavigationView />
</template>

<style scoped></style>
