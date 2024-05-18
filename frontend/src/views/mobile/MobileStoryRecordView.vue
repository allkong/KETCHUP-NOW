<script setup>
import { inject, ref } from 'vue'
import { useRoute } from 'vue-router'
import router from '@/router'
import { EnvironmentOutlined } from '@ant-design/icons-vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'
import MapPreviewComponent from '@/components/mobile/MapPreviewComponent.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'

const route = useRoute()
const axios = inject('axios')

const story = ref({
  uuid: route.params.uuid,
  storyUuid: route.params.storyUuid,
  version: 1,
  title: '',
  description: '',
  sido: '',
  gungu: '',
  imageUri: '',
  spots: [],
})

axios.get(`/stories/${route.params.storyUuid}`).then((resp) => {
  const storyData = resp.data
  story.value.title = storyData.title
  story.value.description = storyData.description
  story.value.version = storyData.version
  story.value.sido = storyData.sido
  story.value.gungu = storyData.gungu
  story.value.imageUri = storyData.imageUri
})
</script>

<template>
  <a-layout-content id="content-container">
    <div class="story-image">
      <GoBackIcon class="back-icon" />
      <img :src="story.imageUri" alt="" />
    </div>
    <div class="detail-container">
      <a-row align="middle">
        <a-col :span="24">
          <h1>{{ story.title }}</h1>
          <span> v.{{ story.version }}</span>
        </a-col>
        <a-col>
          <EnvironmentOutlined />
          <span>{{ story.sido }} {{ story.gungu }}</span>
        </a-col>
      </a-row>
      <a-row>
        <p>{{ story.description }}</p>
      </a-row>
      <div class="map-content"><MapPreviewComponent :storyUuid="story.uuid" /></div>
      <a-steps progress-dot direction="vertical" :current="5" :items="story.spots"></a-steps>
    </div>
  </a-layout-content>
  <NavigationView />
</template>

<style scoped lang="scss">
h1 {
  display: inline;
}

.back-icon {
  position: fixed;
  background-color: rgb(255, 255, 255, 0.5);
  border-radius: 1rem;
  padding: 0.3rem;
}

.ant-steps .ant-steps-item-wait .ant-steps-item-icon {
  background-color: tomato;
}

.story-image {
  height: 30vh;
  overflow: hidden;
}

.story-image img {
  height: 100%;
  width: 100%;
  object-fit: cover;
  object-position: center;
}

.detail-container {
  margin: 1rem;
}

.map-content {
  width: 100%;
  height: 15rem;
  border-radius: 0.5rem;
  overflow: hidden;
  margin: 1rem 0 1rem 0;
}
</style>
