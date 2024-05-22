<script setup>
import { ref, inject, onMounted } from 'vue'

const axios = inject('axios')

const emit = defineEmits(['areaSelectEvent'])

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
  console.log(selectedRegionValue.value)
  if (!isOpened) {
    // 닫혔는데 시/도를 선택하지 않았거나 시/도는 선택했지만 군/구까지 선택되지 않았다면
    if (selectedRegionValue.value.length <= 1) {
      // 옵션 초기화
      selectedRegionValue.value = []
      return
    }
    // 군/구까지 제대로 선택되었으면
    else {
      // 부모로 선택된 시/도, 군/구를 알림
      const [sidoCode, gungu] = selectedRegionValue.value
      const sido = getSidoFromCode(sidoCode)
      emit('areaSelectEvent', sido, gungu)
    }
  }
}
</script>

<template>
  <a-cascader
    v-model:value="selectedRegionValue"
    :options="options"
    :load-data="getChildrenAreas"
    placeholder="지역 선택"
    @dropdownVisibleChange="onDropdownVisibleChange"
    change-on-select
  />
</template>

<style scoped></style>
