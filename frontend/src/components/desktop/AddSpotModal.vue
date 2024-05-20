<script setup>
import { ref } from 'vue'

const props = defineProps({
  modalOpen: Boolean,
  place: Object,
})
console.log(props.place)
const isOpen = ref(props.modalOpen)

const spot = ref({
  previousSpotUuid: null,
  latitude: 0,
  longitude: 0,
  title: '',
  description: '',
  imageFile: '',
})

const onSavePlaceToSpot = (place) => {
  if (place.placeType === 'keyword') {
    spot.value.latitude = place.y
    spot.value.longitude = place.x
    spot.value.title = place.place_name
  } else if (place.placeType === 'attraction') {
    spot.value.latitude = place.latitude
    spot.value.longitude = place.longitude
    spot.value.title = place.title
  }
}

watch(
  () => props.modalOpen,
  (newValue) => {
    isOpen.value = newValue
  },
)

const onAddSpot = (e) => {
  console.log(e)
}
</script>

<!-- previousSpotUuid, latitude, longitude, title, description -->
<template>
  <a-modal v-model:open="isOpen" centered @cancel="$emit('closeAddSpotModal')" @ok="onAddSpot">
    <a-row align="middle" justify="center">
      <a-col :span="4">
        <img src="@/assets/icon/flag.png" class="flag-icon" />
      </a-col>
      <a-col :span="10">
        <h2>스팟 등록</h2>
      </a-col>
    </a-row>
    <a-row>
      <a-col :span="5" class="input-label">이름</a-col>
      <a-col :span="19">
        <!-- <a-input :value="" /> -->
      </a-col>
    </a-row>
  </a-modal>
</template>

<style scoped>
h2 {
  margin: 0;
}

.flag-icon {
  width: 2rem;
  height: 2rem;
}

.modal-button {
  width: 4rem;
  display: flex;
  justify-content: center;
}
</style>
