<script setup>
import { ref, inject, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { StarFilled, CaretRightFilled } from '@ant-design/icons-vue'
import defaultImage from '@/assets/default-image.jpg'
import HeaderView from '@/views/desktop/includes/HeaderView.vue'
import FooterView from '@/views/desktop/includes/FooterView.vue'
import AddStoryModal from '@/components/desktop/modal/AddStoryModal.vue'

const axios = inject('axios')

const stories = ref([])
const isAddStoryModalOpen = ref(false)

onMounted(async () => {
  fetchMyStories()
})

const fetchMyStories = () => {
  axios.get('/story-bases').then((response) => (stories.value = response.data.reverse()))
}

const onAddSpotModal = () => {
  isAddStoryModalOpen.value = true
}

const onCloseAddStoryModal = () => {
  isAddStoryModalOpen.value = false
}

const onUpdateStories = async () => {
  await fetchMyStories()
  isAddStoryModalOpen.value = false
}
</script>

<template>
  <AddStoryModal
    v-if="isAddStoryModalOpen"
    :modal-open="isAddStoryModalOpen"
    @close-add-story-modal="onCloseAddStoryModal"
    @update-stories="onUpdateStories"
  />
  <HeaderView />
  <a-layout-content class="content-layout">
    <a-row align="middle" justify="space-between" class="title-container">
      <a-col>
        <img src="@/assets/play-tomato.png" alt="" />
      </a-col>
      <a-col class="title-text">
        <h1>내 스토리</h1>
        <div class="typing-wrapper">
          <div class="typing-text">새로운 스토리 작성을 원한다면</div>
        </div>
        <a-button size="large" @click="onAddSpotModal">스토리 만들기</a-button>
      </a-col>
    </a-row>
    <div class="grid-container">
      <div v-for="story in stories" :key="story.uuid" class="card-container">
        <RouterLink
          :to="{
            name: 'creator',
            params: { storyBaseUuid: story.storyBaseUuid, uuid: story.uuid },
          }"
        >
          <a-card hoverable>
            <template #cover>
              <img
                :src="story.imageUri || defaultImage"
                alt=""
                class="story-cover-image"
                @error="$replaceDefaultImage"
              />
            </template>
            <a-row justify="space-between" style="margin-bottom: 1rem">
              <a-col>
                <a-tag :color="story.status === 'WRITING' ? 'blue' : 'red'">{{
                  story.status
                }}</a-tag>
                <a-tag color="green">{{ story.sido }} {{ story.gungu }}</a-tag>
              </a-col>
              <a-col class="icon-box">
                <StarFilled class="star-icon" /><span style="margin-right: 0.5rem">{{
                  story.averageReviewScore
                }}</span>
                <CaretRightFilled class="play-icon" /><span>{{ story.totalPlayCount }}</span>
              </a-col>
            </a-row>

            <a-card-meta :title="story.title" :description="story.description"></a-card-meta>

            <div class="date-container">
              <p>Updated {{ story.modifiedAt.split('T').join(' | ') }}</p>
              <p>Created {{ story.createdAt.split('T').join(' | ') }}</p>
            </div>
          </a-card>
        </RouterLink>
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

.typing-wrapper {
  display: grid;
  place-items: center;
  margin-bottom: 1rem;
}

.typing-text {
  width: 22ch;
  animation: typing 2s steps(20), blink 0.5s step-end infinite alternate;
  white-space: nowrap;
  overflow: hidden;
  border-right: 2px solid;
  font-size: 1.5rem;
}

@keyframes typing {
  from {
    width: 0;
  }
}

@keyframes blink {
  50% {
    border-color: transparent;
  }
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(19rem, 1fr));
  grid-gap: 1.5rem;
  justify-content: center;
}

.card-container {
  display: flex;
  justify-content: center;
}

:deep(.ant-card-image),
:deep(.ant-card-body) {
  width: 20rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.story-cover-image {
  height: 12rem;
  width: 100%;
  object-fit: cover;
}

.icon-box {
  display: flex;
  align-items: center;
}

.star-icon {
  color: #fadb14;
  margin-right: 0.3rem;
}

.play-icon {
  color: tomato;
  font-size: 1rem;
  margin-right: 0.1rem;
}

:deep(.ant-card-meta-description) {
  color: lightslategray;
}

.date-container {
  margin-top: 1rem;
  color: lightgray;
  font-size: 0.8rem;
}
</style>
