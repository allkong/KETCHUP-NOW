<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  SearchOutlined,
  PlayCircleFilled,
  HistoryOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const router = useRouter()

const isPlaying = ref(false)
function doPlayGame() {
  if (isPlaying.value) {
    router.push({ name: 'play' })
  } else {
    message.error('플레이 중인 스토리가 없어요 🥲')
  }
}
</script>

<template>
  <!-- <a-menu id="menu-items">
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'search' }"><span>검색</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'play' }"><span>플레이</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'search' }"><span>검색</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'story:cleared-list' }"><span>추천</span></RouterLink>
    </a-menu-item>
    <a-menu-item class="menu-item">
      <RouterLink :to="{ name: 'user:my-page' }"><span>MY</span></RouterLink>
    </a-menu-item>
  </a-menu> -->
  <a-row id="menu-items" justify="space-around">
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'search' }">
        <SearchOutlined />
        <p>검색</p>
      </RouterLink>
    </a-col>
    <a-col class="menu-item">
      <RouterLink :to="{ name: 'search' }">
        <SearchOutlined />
        <p>검색</p>
      </RouterLink>
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
