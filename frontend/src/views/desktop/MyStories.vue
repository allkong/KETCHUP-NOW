<script setup>
import { ref, inject, onMounted } from 'vue'
import { SettingOutlined, EditOutlined, EllipsisOutlined } from '@ant-design/icons-vue'
import defaultImage from '@/assets/default-image.jpg'
import HeaderView from '@/views/desktop/includes/HeaderView.vue'
import FooterView from '@/views/desktop/includes/FooterView.vue'

const axios = inject('axios')

const stories = ref([])

onMounted(async () => {
  fetchMyStories()
})

const fetchMyStories = () => {
  axios.get('/story-bases').then((response) => (stories.value = response.data))
}
</script>

<template>
  <HeaderView />
  <a-layout-content class="content-layout">
    <a-row align="middle" justify="space-between" class="title-container">
      <a-col>
        <img src="@/assets/play-tomato.png" alt="" />
      </a-col>
      <a-col class="title-text">
        <h1>내 스토리</h1>
        <p>새로운 스토리 작성을 원한다면</p>
        <p style="margin: 0.5rem 0">|</p>
        <a-button>스토리 만들기</a-button>
      </a-col>
    </a-row>
    <div class="grid-container">
      <div v-for="story in stories" :key="story.uuid" class="card-container">
        <a-card hoverable>
          <template #cover>
            <img
              :src="story.imageUri || defaultImage"
              alt=""
              class="story-cover-image"
              @error="$replaceDefaultImage"
            />
          </template>
          <a-tag color="red">{{ story.sido }} {{ story.gungu }}</a-tag>

          <a-tag color="yellow">플레이 {{ story.totalPlayCount }}</a-tag>
          <a-tag color="yellow">평점 {{ story.averageReviewScore }}</a-tag>
          <a-card-meta :title="story.title" :description="story.description"></a-card-meta>
          <a-tag color="green">version {{ story.version }}</a-tag>
          <a-tag color="blue">{{ story.status }}</a-tag>
          <p>생성일: {{ story.createdAt }}</p>
          <p>수정일: {{ story.modifiedAt }}</p>
        </a-card>
      </div>
    </div>
  </a-layout-content>
  <FooterView />
</template>

<style scoped lang="scss">
h1,
p {
  margin: 0;
}

.content-layout {
  margin: 0 auto 3rem auto;
  width: 70vw;
  min-height: 100vh - $headerHeight;
}

.title-container {
  background-color: white;
  overflow: hidden;
  margin-bottom: 1rem;
}

.title-container img {
  height: 30vh;
  margin: 2rem 3rem;
}

.title-text {
  text-align: right;
  font-size: 1.5rem;
  margin-right: 3rem;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-gap: 24px;
  justify-content: center;
}

.card-container {
  display: flex;
  justify-content: center;
}

.story-cover-image {
  height: 11rem;
  width: 300px;
  object-fit: cover;
}
</style>
