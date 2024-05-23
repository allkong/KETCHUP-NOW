<script setup>
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { EnvironmentFilled, ClockCircleFilled } from '@ant-design/icons-vue'

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
  let [datePart, timePart] = dt.split('T')
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
          {{ props.playing.description }}
          <a-divider></a-divider>
          <a-row>
            <a-col>
              <div>
                <EnvironmentFilled style="margin: 0.3rem; color: crimson" />{{ addressInfo }}
              </div>
            </a-col>
            <a-col>
              <div>
                <ClockCircleFilled style="margin: 0.3rem; color: cornflowerblue" />{{ periodInfo }}
              </div>
            </a-col>
          </a-row>
        </template>
      </a-card-meta>
      <div class="cleared-story-container"></div>
    </a-card>
  </RouterLink>
</template>

<style scoped lang="scss">
.cleared-story-preview-card {
  width: 100%;
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
:deep(.ant-card .ant-card-body) {
  padding: 1rem;
}
.ant-divider-horizontal {
  margin: 1rem;
}
a {
  width: 90%;
}
</style>
