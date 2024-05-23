<script setup>
import _ from 'lodash'
import { ref, computed, onMounted, inject } from 'vue'
import { RouterLink } from 'vue-router'
import { HeartFilled, EnvironmentFilled, RightCircleFilled } from '@ant-design/icons-vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import { useRouter } from 'vue-router'
import { useStoryZzimStore } from '@/stores/story-zzim'

const axios = inject('axios')
const router = useRouter()
const storyZzimStore = useStoryZzimStore()

function removeZzim(story) {
  storyZzimStore.toggleZzim(story.uuid).then(() => {
    fetchZzimStories()
  })
}

function goToDetail(story) {
  router.push({ name: 'search', query: { 'story-uuid': story.uuid } })
}

const zzimStories = ref([])

async function fetchZzimStories() {
  zzimStories.value = []
  await storyZzimStore.fetchZzims()
  for (let zzim of storyZzimStore.zzims) {
    axios.get(`/stories/${zzim.storyUuid}`).then((resp) => {
      zzimStories.value.push(resp.data)
    })
  }
}

onMounted(async () => {
  fetchZzimStories()
})
</script>

<template>
  <a-layout-content id="content-container">
    <header>
      <h1>
        <HeartFilled class="heart-icon" />
        <span id="menu-text"> 찜한 스토리 </span>
        <HeartFilled class="heart-icon" />
      </h1>
    </header>
    <div v-if="storyZzimStore.zzims.length > 0" class="zzim-container">
      <div id="no-story-message-box" v-if="storyZzimStore.zzims.length === 0">
        <div id="logo-container">
          <img id="logo-img" src="@/assets/logo.png" alt="" />
        </div>
        <div>아직 찜한 스토리가 없어요 🥺</div>
        <div>
          <RouterLink :to="{ name: 'search' }">이야기의 주인공이 되어 보세요!</RouterLink>
        </div>
      </div>
      <a-card hoverable style="width: 100%" v-for="story in zzimStories" :key="story.uuid">
        <a-row>
          <a-col :span="10" style="width: 8rem; height: 8rem">
            <img :src="story.thumbnailImageUri" alt="" class="story-image" />
          </a-col>
          <a-col :span="14" style="padding-left: 1rem">
            <div>
              <a-card-meta
                :title="story.title"
                :description="
                  story.description.length < 50
                    ? story.description
                    : story.description.slice(0, 47) + '...'
                "
              >
              </a-card-meta>
              <a-rate
                v-model:value="story.averageReviewScore"
                disabled
                style="color: #fadb14; font-size: 1rem; margin: 0.5rem 0"
              />
              <p style="margin: 0; color: gray">
                <EnvironmentFilled style="margin-right: 0.3rem; color: crimson" />{{ story.sido }}
                {{ story.gungu }}
              </p>
            </div>
          </a-col>
        </a-row>
        <div>
          <a-divider class="horizontal-divider" />
          <a-row align="middle" justify="center" class="card-actions">
            <a-col :span="12" class="full-height" @click="onEditSpotModal(element)">
              <div class="action-container">
                <HeartFilled
                  style="color: salmon; font-size: 1rem"
                  key="zzim"
                  @click="() => removeZzim(story)"
                />
              </div>
            </a-col>
            <a-col :span="12" class="full-height" @click="onAddSpotEventModal(element)">
              <div class="action-container" style="border-left: 1px solid lightgray">
                <RightCircleFilled
                  style="color: cornflowerblue; font-size: 1rem"
                  key="detail"
                  @click="() => goToDetail(story)"
                />
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
    </div>
  </a-layout-content>
  <NavigationView />
</template>

<style scoped>
h1 {
  text-align: center;
  padding: 0.5rem;
}
.heart-icon {
  color: orangered;
}

#logo-img {
  width: 10rem;
}
#no-story-message-box {
  font-size: large;
}
.thumbnail {
  background-color: lightgray;
  height: 100%;
  width: 6rem;
  border-radius: 0.5rem;
}

.ant-card {
  border: 1px solid lightgray;
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

:deep(.ant-card-body) {
  padding: 1rem;
}

.horizontal-divider {
  margin: 1rem 0;
  height: 1px;
  background-color: #e8e8e8;
}

.zzim-container {
  overflow: auto;
  height: 80vh;
  padding: 1rem;
  border: 1px solid silver;
  margin: 0.1rem 0.3rem 0.2rem 0.3rem;
  border-radius: 0.5rem;
  overflow: auto;
}

.story-image {
  height: 100%;
  width: 100%;
  border-radius: 0.5rem;
  object-fit: cover;
}

:deep(.ant-rate .ant-rate-star:not(:last-child)) {
  margin-inline-end: 0.2rem;
}

.card-actions {
  display: flex;
  justify-content: space-around;
}

.action-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

/* 스크롤바 컨테이너 스타일 */
::-webkit-scrollbar {
  width: 0.3rem;
}

/* 스크롤바 핸들 스타일 */
::-webkit-scrollbar-thumb {
  background: #cacaca;
  border-radius: 2rem;
}

/* 스크롤바 트랙 스타일 */
::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 1rem;
}
</style>
