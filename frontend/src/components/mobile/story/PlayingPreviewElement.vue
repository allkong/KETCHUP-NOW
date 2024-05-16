<script setup>
import { ref } from 'vue'
import { RouterLink } from 'vue-router'

const props = defineProps({
  playing: {
    type: Object,
    required: true,
  },
})

console.log(props.playing)
</script>

<template>
  <RouterLink
    :to="{
      name: 'story:cleared-record',
      params: { uuid: props.playing.uuid, storyUuid: props.playing.storyUuid },
    }"
  >
    <a-card class="cleared-story-preview-card">
      <template #cover>
        <img class="story-thumbnail-img" :src="props.playing.thumbnailImageUri" />
      </template>
      <a-card-meta :title="props.playing.title">
        <template #description>
          <!-- 인천 남동구, 2024-05-05 ~ 2024-05-10 -->
          {{ props.playing.sido ?? '' }}
          {{ props.playing.gungu ? `${props.playing.sido ? ' ' : ''}${props.playing.gungu}` : '' }}
          {{ props.playing.sido || props.playing.gungu ? ' : ' : '' }}
          {{ props.playing.storyCreatedAt }}
          ~ {{ props.playing.clearedAt }}</template
        >
      </a-card-meta>
      <div class="cleared-story-container">
        <a-row>
          <b></b>
          <a-col span="24">{{ props.playing.description }}</a-col>
        </a-row>
      </div>
    </a-card>
  </RouterLink>
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
.ant-card {
  width: 100%;
  min-width: 100%;
}
</style>
