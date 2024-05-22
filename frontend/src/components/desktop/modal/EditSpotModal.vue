<script setup>
import { ref, inject, watchEffect } from 'vue'
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
  previousSpotUuid: '',
  latitude: 0,
  longitude: 0,
  title: '',
  description: '',
  eventType: '',
  jsonEventContent: {},
})

const onSaveSpotToEditedSpot = (spot) => {
  editedSpot.value.previousSpotUuid = spot.previousSpotUuid
  editedSpot.value.latitude = spot.latitude
  editedSpot.value.longitude = spot.longitude
  editedSpot.value.title = spot.title
  editedSpot.value.description = spot.description
  editedSpot.value.imageFile = spot.imageFile
  editedSpot.value.eventType = spot.eventType
  editedSpot.value.jsonEventContent = spot.jsonEventContent
}

watchEffect(() => {
  if (isOpen.value) {
    onSaveSpotToEditedSpot(props.spot)
  }
})

const onEditSpot = () => {
  if (editedSpot.value.jsonEventContent === null) {
    editedSpot.value.jsonEventContent = null
  } else {
    editedSpot.value.jsonEventContent = JSON.stringify(editedSpot.value.jsonEventContent)
  }
  axios
    .put(`/stories/${route.params.uuid}/spots/${props.spot.uuid}`, editedSpot.value)
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
    okText="변경"
    @cancel="$emit('closeEditSpotModal')"
    @ok="onEditSpot"
  >
    <a-row align="middle" justify="center">
      <a-col>
        <img src="@/assets/icon/flag.png" class="flag-icon" />
      </a-col>
      <a-col>
        <h2>스팟 정보</h2>
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
          <a-textarea v-model:value="editedSpot.description" class="textarea-box" />
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

.textarea-box {
  width: 18rem;
  height: 6rem;
}

/* 스크롤바 컨테이너 스타일 */
.textarea-box::-webkit-scrollbar {
  width: 0.4rem;
}

/* 스크롤바 핸들 스타일 */
.textarea-box::-webkit-scrollbar-thumb {
  background: #cacaca;
  border-radius: 10px;
}

/* 스크롤바 트랙 스타일 */
.textarea-box::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 1rem;
}
</style>
