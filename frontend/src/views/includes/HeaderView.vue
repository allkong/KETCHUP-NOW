<script setup>
import MenuComponent from '@/components/MenuComponent.vue'
</script>

<template>
    <a-layout-header class="header">
      <!-- <div class="logo">
        <img src="@/assets/logo.png" alt="" :style="{ height: '50px' }">
      </div> -->
      <MenuComponent/>
    </a-layout-header>
</template>

<style scoped>
.header {
  background-color: #ffffff;
}

</style>