<script setup>
import { ref } from 'vue'

const props = defineProps({
  story: {
    type: Object,
    required: true,
  },
})

const story = ref(props.story)
</script>

<template>
  <a-card class="cleared-story-preview-card">
    <template #cover>
      <img class="story-thumbnail-img" :src="story.thumbnailImageUrl" />
    </template>
    <a-card-meta :title="story.title">
      <template #description>
        <!-- 인천 남동구, 2024-05-05 ~ 2024-05-10 -->
        {{ story.sido ?? '' }}
        {{ story.gungu ? `${story.sido ? ' ' : ''}${story.gungu}` : '' }}
        {{ story.sido || story.gungu ? ' : ' : '' }}
        {{ story.createdAt }} ~ {{ story.clearedAt }}</template
      >
    </a-card-meta>
    <div class="cleared-story-container">
      <a-row>
        <b></b>
        <a-col span="24">{{ story.description }}</a-col>
      </a-row>
    </div>
  </a-card>
</template>

<style scoped lang="scss">
#cleared-story-preview-cards-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.cleared-story-preview-card {
  width: 95%;
  padding: 0.5rem;
  margin-bottom: 1rem;
}
.cleared-story-container {
  margin-top: 1rem;
}
.story-thumbnail-img {
  object-fit: cover;
  height: 12rem;
}
</style>
