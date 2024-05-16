<script setup>
import { ref, inject, onMounted } from 'vue'
import { CloseOutlined, SearchOutlined } from '@ant-design/icons-vue'

const axios = inject('axios')

const options = ref([])
const value = ref([])

onMounted(() => {
  getParentAreas()
})

const getParentAreas = () => {
  axios
    .get('/attractions/areas')
    .then((response) => response.data.data)
    .then((areas) => {
      options.value = areas.map((area) => ({
        value: area.code,
        label: area.name,
        isLeaf: false,
      }))
    })
    .catch((error) => console.error(error))
}

const getChildrenAreas = (selectedOptions) => {
  const targetOption = selectedOptions[selectedOptions.length - 1]
  targetOption.loading = true

  axios
    .get(`/attractions/areas/${targetOption.value}`)
    .then((response) => response.data.data)
    .then((areas) => {
      targetOption.loading = false
      targetOption.children = areas.map((area) => ({
        value: area,
        label: area,
        isLeaf: true,
      }))
    })
    .catch((error) => console.error(error))
}

const searchByRegion = () => {
  console.log('조회')
}
</script>

<template>
  <!-- <a-button class="map-button region-button" @click="searchByRegion">
    <SearchOutlined />
    지역으로 찾기
  </a-button> -->
  <a-cascader
    class="map-button region-button"
    v-model:value="value"
    :options="options"
    :load-data="getChildrenAreas"
    placeholder="지역으로 찾기"
    @change="searchByRegion"
    change-on-select
  />
</template>

<style scoped>
.region-button {
  left: 1rem;
}
</style>
