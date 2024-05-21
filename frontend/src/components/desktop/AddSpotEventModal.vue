<script setup>
import { ref, inject, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const axios = inject('axios')

const props = defineProps({
  modalOpen: Boolean,
  spot: Object,
})

const isOpen = ref(props.modalOpen)
console.log(props.spot)

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

const quizEvent = ref({
  title: '',
  content: '',
  hint: '',
  answer: '',
})

console.log(editedSpot.value)
</script>

<template>
  <a-modal v-model:open="isOpen" width="25rem" centered cancelText="취소" okText="등록">
    <a-row align="middle" justify="center">
      <a-col>
        <img src="@/assets/icon/flag.png" class="flag-icon" />
      </a-col>
      <a-col>
        <h2>스팟 수정</h2>
      </a-col>
    </a-row>
    <div></div>
  </a-modal>
</template>

<style scoped>
.flag-icon {
  width: 2rem;
  height: 2rem;
  margin-right: 0.5rem;
}
</style>
