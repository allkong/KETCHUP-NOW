<script setup>
import { ref, inject } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'
const axios = inject('axios')

const searchByRegion = () => {
  console.log('찾아보자')
  axios
    .get('/attractions/areas', {
      params: {
        'area-name': '서울',
      },
    })
    .then((response) => console.log(response))
}
</script>

<template>
  <a-button class="map-button region-button" @click="searchByRegion">
    <SearchOutlined />
    지역으로 찾기
  </a-button>
  <!-- <a-cascader
    class="map-button region-button"
    v-model:value="value"
    :options="options"
    placeholder="지역 찾기"
    @change="handleRegionChange"
  /> -->
</template>

<style scoped>
.region-button {
  right: 1rem;
}
</style>
