<script setup>
import { ref } from 'vue'
import { SearchOutlined, FlagOutlined } from '@ant-design/icons-vue'
import MapComponent from '@/components/desktop/MapComponent.vue'

const leftCollapsed = ref(false)
const rightCollapsed = ref(false)
const selectedKeys = ref(['1'])
const keyword = ref('')

const onRightCollapse = (collapsedValue) => {
  rightCollapsed.value = collapsedValue
}

const search = () => {}
</script>

<template>
  <a-layout-header>
    <!-- <a-row align="middle">
      <a-col>
        <img src="@/assets/logo.png" class="logo-image" />
      </a-col>
    </a-row> -->
  </a-layout-header>
  <a-layout>
    <a-layout-sider
      v-model:collapsed="leftCollapsed"
      collapsible
      width="20rem"
      class="left-sider-shadow"
    >
      <a-menu v-model:selectedKeys="selectedKeys" theme="light" mode="inline">
        <a-menu-item key="1">
          <SearchOutlined />
          <span>키워드</span>
        </a-menu-item>
        <a-menu-item key="2">
          <FlagOutlined />
          <span>관광지</span>
        </a-menu-item>
      </a-menu>
      <a-row justify="center">
        <a-col>
          <a-input v-model="keyword" placeholder="키워드 검색" />
        </a-col>
        <a-col>
          <a-button type="primary" ghost @click="search">검색</a-button>
        </a-col>
      </a-row>
      <a-row>
        <ul id="placesList"></ul>
      </a-row>
    </a-layout-sider>

    <a-layout-content>
      <MapComponent />
    </a-layout-content>

    <a-layout-sider
      breakpoint="lg"
      collapsed-width="0"
      :collapsed="rightCollapsed"
      @collapse="onRightCollapse"
      :class="{ 'right-sider-shadow': !rightCollapsed }"
      collapsible
      reverseArrow
    >
      내 스팟
    </a-layout-sider>
  </a-layout>
</template>

<style scoped>
.logo-image {
  height: 6vh;
}

.a-layout-sider {
  z-index: 10;
}

.ant-layout-header {
  background: lightgray;
}

.ant-layout-sider {
  background: white;
}

.left-sider-shadow {
  box-shadow: 5px 0 5px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.right-sider-shadow {
  box-shadow: -5px 0 5px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
}
</style>
