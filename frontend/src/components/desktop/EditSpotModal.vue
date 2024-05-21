<script setup>
import { ref, inject } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const axios = inject('axios')

const emit = defineEmits(['closeEditSpotModal', 'updateEditedSpot'])
const props = defineProps({
  modalOpen: Boolean,
  spot: Object,
})

const isOpen = ref(props.modalOpen)
const editedSpot = ref({
  previousSpotUuid: props.spot.previousSpotUuid,
  latitude: props.spot.latitude,
  longitude: props.spot.longitude,
  title: props.spot.title,
  description: props.spot.description,
  imageFile: props.spot.imageFile,
  eventType: props.spot.eventType,
  jsonEventContent: props.spot.jsonEventContent,
})

const onEditSpot = () => {
  axios
    .put(
      `/stories/${route.params.uuid}/spots/${editedSpot.value.previousSpotUuid}`,
      editedSpot.value,
    )
    .then((response) => emit('updateEditedSpot'))
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
    @cancel="$emit('closeEditSpotModal')"
    @ok="onEditSpot"
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
          <a-input v-model:value="editedSpot.title" class="input-box" />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">설명</a-col>
        <a-col>
          <a-textarea v-model:value="editedSpot.description" class="input-box" />
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
