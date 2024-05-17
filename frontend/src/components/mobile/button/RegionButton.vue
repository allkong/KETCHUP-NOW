<script setup>
import { ref, inject, onMounted } from 'vue'
import { CloseOutlined, SearchOutlined } from '@ant-design/icons-vue'

const axios = inject('axios')

const emit = defineEmits(['areaFilterUpdated'])

const options = ref([])
const selectedRegionValue = ref([])

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

function getSidoFromCode(sidoCode) {
  return options.value.filter((opt) => opt.value === sidoCode)[0].label
}

const onDropdownVisibleChange = (isOpened) => {
  if (!isOpened) {
    // 닫혔는데 군/구까지 선택되지 않았다면
    if (selectedRegionValue.value.length == 1) {
      // 옵션 초기화
      selectedRegionValue.value = []
      return
    }
    // 군/구까지 제대로 선택되었으면
    else {
      // 부모로 선택된 시/도, 군/구를 알림
      const [sidoCode, gungu] = selectedRegionValue.value
      const sido = getSidoFromCode(sidoCode)
      emit('areaFilterUpdated', sido, gungu)
    }
  }
}
</script>

<template>
  <!-- <a-button class="map-button region-button" @click="searchByRegion">
    <SearchOutlined />
    지역으로 찾기
  </a-button> -->
  <a-cascader
    class="map-button region-button"
    v-model:value="selectedRegionValue"
    :options="options"
    :load-data="getChildrenAreas"
    placeholder="지역으로 찾기"
    @dropdownVisibleChange="onDropdownVisibleChange"
    change-on-select
  />
</template>

<style scoped>
.region-button {
  left: 1rem;
}
</style>
