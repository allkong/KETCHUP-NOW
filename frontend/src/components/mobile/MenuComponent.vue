<script setup>
import { ref } from 'vue'
import {
  SearchOutlined,
  PlayCircleFilled,
  HistoryOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

const menuList = ref([
  { name: '검색', routeName: 'search' },
  { name: '플레이', routeName: 'play' },
  { name: '스토리', routeName: 'story:cleared-list' },
  { name: 'MY', routeName: 'user:my-page' },
])
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
      <RouterLink :to="{ name: 'play' }">
        <PlayCircleFilled class="play-icon" />
        <p>플레이</p>
      </RouterLink>
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
</style>
