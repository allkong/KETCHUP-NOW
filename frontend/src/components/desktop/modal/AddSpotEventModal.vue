<script setup>
import { ref, inject, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const axios = inject('axios')

const emit = defineEmits(['closeAddSpotEventModal', 'updateSpotEvent'])
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
  eventType: 'QUIZ',
  jsonEventContent: JSON.parse(props.spot.jsonEventContent),
})

const quizEvent = ref({
  title: '',
  content: '',
  hint: '',
  answer: '',
})

const onSaveSpotToEvent = (spot) => {
  console.log(editedSpot.value)
  if (spot.eventType === 'QUIZ') {
    const eventContent = spot.jsonEventContent || ''
    if (eventContent !== null || eventConent === '') {
      quizEvent.value.title = eventContent.title || ''
      quizEvent.value.content = eventContent.content || ''
      quizEvent.value.hint = eventContent.hint || ''
      quizEvent.value.answer = eventContent.answer || ''
    }
  }
}

watchEffect(() => {
  if (isOpen.value) {
    onSaveSpotToEvent(editedSpot.value)
  }
})

const onAddSpotEvent = () => {
  if (editedSpot.value.eventType === 'QUIZ') {
    editedSpot.value.jsonEventContent = quizEvent.value
  }
  console.log(editedSpot.value)
  editedSpot.value.jsonEventContent = JSON.stringify(editedSpot.value.jsonEventContent)
  axios
    .put(`/stories/${route.params.uuid}/spots/${props.spot.uuid}`, editedSpot.value)
    .then((response) => emit('updateSpotEvent'))
    .catch((error) => console.error(error))
}
</script>

<template>
  <a-modal
    v-model:open="isOpen"
    width="25rem"
    centered
    cancelText="취소"
    okText="등록"
    @cancel="$emit('closeAddSpotEventModal')"
    @ok="onAddSpotEvent"
  >
    <a-row align="middle" justify="center">
      <a-col>
        <img src="@/assets/icon/flag.png" class="flag-icon" />
      </a-col>
      <a-col>
        <h2>스팟 이벤트</h2>
      </a-col>
    </a-row>
    <div class="input-container">
      <a-row justify="center" class="input-form">
        <a-col class="input-label">타입</a-col>
        <a-select v-model:value="editedSpot.eventType" class="input-box">
          <a-select-option value="QUIZ">퀴즈</a-select-option>
        </a-select>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">제목</a-col>
        <a-col>
          <a-input
            v-model:value="quizEvent.title"
            placeholder="퀴즈 제목을 입력하세요."
            class="input-box"
          />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">내용</a-col>
        <a-col>
          <a-textarea
            v-model:value="quizEvent.content"
            placeholder="퀴즈 내용을 입력하세요."
            class="input-box"
          />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">힌트</a-col>
        <a-col>
          <a-input
            v-model:value="quizEvent.hint"
            placeholder="퀴즈 힌트를 입력하세요."
            class="input-box"
          />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">정답</a-col>
        <a-col>
          <a-input
            v-model:value="quizEvent.answer"
            placeholder="퀴즈 정답을 입력하세요."
            class="input-box"
          />
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
</style>
