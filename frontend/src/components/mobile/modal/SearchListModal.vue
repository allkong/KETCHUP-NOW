<script setup>
import { ref } from 'vue'
import { StarFilled } from '@ant-design/icons-vue'

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

<!-- 디자인 수정 필요 -->
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
                <p>{{ story.title }}</p>
                <a-rate :value="2" disabled class="star-grade" />
                <p>{{ story.sido }} {{ story.gungu ? ' ' + story.gungu : '' }}</p>
                <p>{{ story.authorNickname }}</p>
              </a-col>
            </a-row>
          </a-card-grid>
        </a-card>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
p {
  margin: 0;
}

.thumbnail {
  background-color: lightgray;
  height: 6rem;
  width: 6rem;
  border-radius: 0.5rem;
}

.star-grade {
  font-size: 0.9rem;
}

:deep(.ant-rate .ant-rate-star:not(:last-child)) {
  margin-inline-end: 0.2rem;
}
</style>
