<script setup>
import { ref } from 'vue'
import { EnvironmentOutlined } from '@ant-design/icons-vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'

const story = ref({
  uuid: crypto.randomUUID(),
  storyId: crypto.randomUUID(),
  version: 1,
  title: '마도로스 K의 대모험',
  description:
    '시커먼 뻘밭을 맨발로 걷다 무언가에 베어 피가 나지만 검은 바다 밑을 허우적거리다 죽음과 싸우다 뭍으로 돌아왔네. 나를 바다로 밀친 그를 찾아 떠난 자식들의 행방은 알 수 없고 모든 것을 망쳐버린 그 사나이에게 독한 복수를 다짐했네.',
  sido: '인천',
  gungu: '남동구',
  imageUrl:
    'https://a.cdn-hotels.com/gdcs/production59/d1951/52474f11-6fba-4ad5-8b9c-cc0899cf5dab.jpg?impolicy=fcrop&w=800&h=533&q=medium',
  spots: [
    {
      title: '첫 번째 장소',
      description: '이런 이벤트',
    },
    {
      title: '두 번째 장소',
      description: '저런 이벤트',
    },
    {
      title: '세 번째 장소',
      description: '요런 이벤트',
    },
    {
      title: '네 번째 장소',
      description: '그런 이벤트',
    },
    {
      title: '다섯 번째 장소',
      description: '이러한 이벤트',
    },
  ],
})
</script>

<template>
  <a-layout-content id="content-container">
    <div class="story-image">
      <GoBackIcon class="back-icon" />
      <img :src="story.imageUrl" alt="" />
    </div>
    <div class="detail-container">
      <a-row align="middle">
        <a-col :span="24">
          <h1>{{ story.title }}</h1>
          <span>v.{{ story.version }}</span>
        </a-col>
        <a-col>
          <EnvironmentOutlined />
          <span>{{ story.sido }} {{ story.gungu }}</span>
        </a-col>
        <a-col>
          <p>{{ story.description }}</p>
        </a-col>
      </a-row>
      <div>지도</div>
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
</style>
