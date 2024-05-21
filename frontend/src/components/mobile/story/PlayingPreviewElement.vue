<script setup>
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'

const props = defineProps({
  playing: {
    type: Object,
    required: true,
  },
})

const addressInfo = computed(() => {
  let address = props.playing.sido ?? ''
  address += props.playing.gungu ? `${props.playing.sido ? ' ' : ''}${props.playing.gungu}` : ''
  return address
})

function formatDatetime(dt) {
  let [datePart, timePart ] = dt.split('T')
  timePart = timePart.split(':').slice(0, 2).join(':')
  return datePart + ' ' + timePart
}

const periodInfo = computed(() => {
  return formatDatetime(props.playing.startedAt) + ' ~ ' + formatDatetime(props.playing.clearedAt)
})
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
          <div>{{ addressInfo }}</div>
          <div>{{ periodInfo }}</div>
          </template >
      </a-card-meta>
      <div class="cleared-story-container">
          {{ props.playing.description }}
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
