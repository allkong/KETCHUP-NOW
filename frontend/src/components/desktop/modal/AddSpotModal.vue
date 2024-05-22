<script setup>
import { ref, inject, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const axios = inject('axios')

const props = defineProps({
  modalOpen: Boolean,
  place: Object,
  lastSpotUuid: String,
})

const isOpen = ref(props.modalOpen)
const spot = ref({
  previousSpotUuid: props.lastSpotUuid,
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

watchEffect(() => {
  if (isOpen.value) {
    onSavePlaceToSpot(props.place)
  }
})

const emit = defineEmits(['closeAddSpotModal', 'updateSpots'])
const onAddSpot = () => {
  axios
    .post(`/stories/${route.params.uuid}/spots`, spot.value)
    .then((response) => emit('updateSpots'))
    .catch((error) => console.error(error))
}
</script>

<!-- previousSpotUuid, latitude, longitude, title, description -->
<template>
  <a-modal
    v-model:open="isOpen"
    width="25rem"
    centered
    cancelText="취소"
    okText="등록"
    @cancel="$emit('closeAddSpotModal')"
    @ok="onAddSpot"
  >
    <a-row align="middle" justify="center">
      <a-col>
        <img src="@/assets/icon/flag.png" class="flag-icon" />
      </a-col>
      <a-col>
        <h2>스팟 등록</h2>
      </a-col>
    </a-row>
    <div class="input-container">
      <a-row justify="center" class="input-form">
        <a-col class="input-label">이름</a-col>
        <a-col>
          <a-input v-model:value="spot.title" class="input-box" />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">설명</a-col>
        <a-col>
          <a-textarea v-model:value="spot.description" class="input-box" />
        </a-col>
      </a-row>
    </div>
  </a-modal>
</template>

<style scoped>
h2 {
  margin: 0;
}

.flag-icon {
  width: 2rem;
  height: 2rem;
  margin-right: 0.5rem;
}

.modal-button {
  width: 4rem;
  display: flex;
  justify-content: center;
}

.input-container {
  margin: 2rem 0;
}

.input-form {
  margin: 1rem 0;
}

.input-label {
  display: flex;
  align-items: center;
  justify-content: end;
  padding-right: 1rem;
}

.input-box {
  width: 18rem;
}
</style>
