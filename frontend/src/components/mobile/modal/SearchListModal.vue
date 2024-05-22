<script setup>
import { ref } from 'vue'
import { EnvironmentOutlined } from '@ant-design/icons-vue'

const props = defineProps({
  modalOpen: Boolean,
  stories: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['storySelected'])

const isOpen = ref(props.modalOpen)

function onStoryCardClicked(story) {
  emit('storySelected', story)
  isOpen.value = false
}
</script>

<template>
  <div>
    <a-modal
      v-model:open="isOpen"
      width="100%"
      cancelText="닫기"
      @cancel="$emit('closeSearchListModal')"
      centered
    >
      <h2>스토리 목록</h2>
      <div style="overflow: auto; height: 70vh">
        <a-card
          v-for="story in props.stories"
          :key="story.uuid"
          @click="() => onStoryCardClicked(story)"
        >
          <a-card-grid style="width: 100%; height: 9rem">
            <a-row>
              <a-col :span="10">
                <img
                  class="thumbnail"
                  :src="story.thumbnailImageUri"
                  @error="$replaceDefaultImage"
                  alt=""
                />
              </a-col>
              <a-col :span="14">
                <h4>{{ story.title }}</h4>
                <a-rate :value="story.averageReviewScore.toFixed(1)" disabled class="star-grade" />
                <p style="display: flex; align-items: center">
                  <EnvironmentOutlined style="margin-right: 0.3rem" />
                  {{ story.sido }} {{ story.gungu ? ' ' + story.gungu : '' }}
                </p>
                <p>Created By {{ story.authorNickname }}</p>
              </a-col>
            </a-row>
          </a-card-grid>
        </a-card>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
h4 {
  margin: 0;
  font-size: 1rem;
}

p {
  margin: 0.2rem 0;
  font-size: 0.8rem;
}

.thumbnail {
  background-color: lightgray;
  height: 6rem;
  width: 6rem;
  border-radius: 0.5rem;
  object-fit: cover;
}

.star-grade {
  font-size: 1rem;
}

:deep(.ant-rate .ant-rate-star:not(:last-child)) {
  margin-inline-end: 0.2rem;
}
</style>
