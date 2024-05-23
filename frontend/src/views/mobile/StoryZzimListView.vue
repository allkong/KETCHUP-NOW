<script setup>
import _ from 'lodash'
import { ref, computed, onMounted, inject } from 'vue'
import {RouterLink} from 'vue-router'
import { HeartFilled, CloseOutlined, HeartOutlined, CaretRightOutlined } from '@ant-design/icons-vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import { useRouter } from 'vue-router'
import { useStoryZzimStore } from '@/stores/story-zzim';

const axios = inject('axios')
const router = useRouter()
const storyZzimStore = useStoryZzimStore()


function removeZzim(story) {
    storyZzimStore.toggleZzim(story.uuid)
    .then(() => {
        fetchZzimStories()
    })
}

function goToDetail(story) {
    router.push({name: 'search', query: {'story-uuid': story.uuid}})
}

const zzimStories = ref([])

async function fetchZzimStories() {
    zzimStories.value = []
    await storyZzimStore.fetchZzims()
    for(let zzim of storyZzimStore.zzims) {
        axios.get(`/stories/${zzim.storyUuid}`)
        .then(resp => {
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
        <article id="cleared-story-preview-cards-container">
            <div id="no-story-message-box" v-if="storyZzimStore.zzims.length === 0">
                <div id="logo-container">
                    <img id="logo-img" src="@/assets/logo.png" alt="">
                </div>
                <div>아직 찜한 스토리가 없어요 🥺</div>
                <div><RouterLink :to="{name: 'search'}">이야기의 주인공이 되어 보세요!</RouterLink></div>
            </div>
            <div v-if="storyZzimStore.zzims.length > 0" style="overflow: auto; height: 90vh">
              <a-card hoverable style="width: 300px"
              v-for="story in zzimStories"
              :key="story.uuid">
                <template #extra>
                    <!-- <sub>{{ story.sido }} {{ story.gungu }}</sub> -->
                    <a-rate v-model:value="story.averageReviewScore" disabled style="color: tomato" />
                </template>
                <template #cover>
                  <img
                    alt="example"
                    :src="story.thumbnailImageUri"
                  />
                </template>
                <template #actions>
                  <close-outlined key="zzim" @click="() => removeZzim(story)" />
                  <caret-right-outlined key="detail" @click="() => goToDetail(story)" />
                </template>
                <a-card-meta :title="story.title" :description="story.description.length < 50 ? story.description : story.description.slice(0, 47) + '...'">
                </a-card-meta>
              </a-card>
            </div>
          </article>
      </a-layout-content>
      <NavigationView />
    
</template>

<style scoped>
h1 {
    text-align: center;
    padding: 0.5rem;
}
.heart-icon {
    color: tomato;
}
#cleared-story-preview-cards-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
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
.star-grade {
    font-size: 0.9rem;
    color: tomato;
}
.ant-card {
    border: 1px solid silver;
    margin-bottom: 1rem;
}
.ant-card-grid {
    padding: 1rem;
}
</style>