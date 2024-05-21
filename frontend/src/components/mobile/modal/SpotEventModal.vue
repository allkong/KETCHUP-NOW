<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import DefaultImage from '@/assets/default-image.jpg'

const emit = defineEmits(['closeSpotEventModal', 'spotEventClear'])
const props = defineProps({
  modalOpen: Boolean,
  spot: Object,
})

const eventContent = JSON.parse(props.spot.jsonEventContent)
const inputAnswer = ref('')

const isOpen = ref(props.modalOpen)
const hasEvent = ref(props.spot.jsonEventContent !== null)
const activeKey = ref('1')

const onCheckAnswer = () => {
  // 만약 퀴즈가 없다면 그냥 완료 처리
  if (!hasEvent.value) {
    emit('spotEventClear')
    return
  }

  if (inputAnswer.value === eventContent.answer) {
    message.success('정답입니다! 👏')
    emit('spotEventClear')
  } else {
    message.error('오답입니다. 😢')
  }
}
</script>

<template>
  <div>
    <a-modal
      v-model:open="isOpen"
      centered
      cancelText="닫기"
      okText="완료"
      @cancel="$emit('closeSpotEventModal')"
      @ok="onCheckAnswer"
    >
      <img
        class="thumbnail"
        :src="props.spot.imageUri || DefaultImage"
        @error="$replaceDefaultImage"
        alt=""
      />
      <h2>{{ props.spot.title }}</h2>
      <span>{{ props.spot.description }}</span>
      <a-tabs v-if="hasEvent" v-model:activeKey="activeKey" class="tabs-container">
        <a-tab-pane key="1" tab="미션">
          <h3>Q. {{ eventContent.title }}</h3>
          <div class="tab-textarea">
            {{ eventContent.content }}
          </div>
          <a-input
            v-model:value="inputAnswer"
            placeholder="답을 입력하세요."
            class="answer-input"
          ></a-input>
        </a-tab-pane>
        <a-tab-pane key="2" tab="힌트">
          <p>{{ eventContent.hint }}</p>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<style scoped>
.thumbnail {
  background-color: #d9d9d9;
  height: 12rem;
  width: 100%;
  border-radius: 0.5rem;
  object-fit: cover;
}

.anticon-heart {
  font-size: 1.5rem;
}

.anticon-caret-right {
  font-size: 1rem;
}

.tabs-container {
  margin-top: 1rem;
}

.tab-textarea {
  max-height: 12rem;
  overflow: auto;
}

.answer-input {
  width: 60%;
  margin: 1rem 0;
}
</style>
