<script setup>
import { inject, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  SearchOutlined,
  PlayCircleFilled,
  HistoryOutlined,
  UserOutlined,
  HeartOutlined,
} from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import SearchListModal from './modal/SearchListModal.vue'

const axios = inject('axios')
const router = useRouter()

const updatePlayingStatus = () => {
  return axios
    .get('playings/now')
    .then((response) => {
      if (response.data.status === 'PLAYING') {
        isPlaying.value = true
      } else {
        isPlaying.value = false
      }

      return Promise.resolve(response.data)
    })
    .catch((error) => {
      isPlaying.value = false
      return Promise.reject(error)
    })
}

const isPlaying = ref(false)
function doPlayGame() {
  updatePlayingStatus().finally(() => {
    if (isPlaying.value) {
      router.push({ name: 'play' })
    } else {
      message.error('플레이 중인 스토리가 없어요 🥲')
    }
  })
}

onMounted(async () => {
  updatePlayingStatus()
})
</script>

<template>
  <a-row id="menu-items" justify="space-around">
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'search' }">
        <SearchOutlined />
        <p>검색</p>
      </RouterLink>
    </a-col>
    <a-col class="menu-item">
        <span @click="openZzimListModal">
          <HeartOutlined />
          <p>찜</p>
        </span>
    </a-col>
    <a-col class="menu-item">
      <span @click="doPlayGame">
        <PlayCircleFilled :class="isPlaying ? 'play-icon' : 'disabled-play-icon'" />
        <p>플레이</p>
      </span>
    </a-col>
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'story:cleared-list' }">
        <HistoryOutlined />
        <p>기록</p>
      </RouterLink>
    </a-col>
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'user:my-page' }">
        <UserOutlined />
        <p>MY</p>
      </RouterLink>
    </a-col>
  </a-row>
</template>

<style scoped lang="scss">
#menu-items {
  position: absolute;
  bottom: 0;
  height: $navigationHeight;
  width: 100%;
}

.menu-item {
  display: flex;
  align-items: center;
  text-align: center;
}

.menu-item a {
  color: rgb(53, 53, 53);
}

.menu-item .anticon {
  font-size: 1.2rem;
}

.menu-item p {
  font-size: 0.6rem;
  margin-bottom: 0;
}

.play-icon {
  color: tomato;
}

.disabled-play-icon {
  color: gray;
}
</style>
