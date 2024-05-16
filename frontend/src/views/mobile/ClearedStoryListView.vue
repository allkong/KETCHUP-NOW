<script setup>
import _ from 'lodash'
import { ref, computed } from 'vue'
import { TrophyFilled, CaretUpFilled, CaretDownFilled } from '@ant-design/icons-vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import StoryPreviewElement from '@/components/mobile/story/StoryPreviewElement.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const _clearedStories = ref([
  {
    uuid: crypto.randomUUID(),
    storyId: crypto.randomUUID(),
    version: 1,
    title: '마도로스 K의 대모험',
    description:
      '시커먼 뻘밭을 맨발로 걷다 무언가에 베어 피가 나지만 검은 바다 밑을 허우적거리다 죽음과 싸우다 뭍으로 돌아왔네. 나를 바다로 밀친 그를 찾아 떠난 자식들의 행방은 알 수 없고 모든 것을 망쳐버린 그 사나이에게 독한 복수를 다짐했네.',
    playCount: 134,
    clearCount: 20,
    sido: '인천',
    gungu: '남동구',
    imageUrl:
      'https://a.cdn-hotels.com/gdcs/production59/d1951/52474f11-6fba-4ad5-8b9c-cc0899cf5dab.jpg?impolicy=fcrop&w=800&h=533&q=medium',
    thumbnailImageUrl:
      'https://a.cdn-hotels.com/gdcs/production59/d1951/52474f11-6fba-4ad5-8b9c-cc0899cf5dab.jpg?impolicy=fcrop&w=800&h=533&q=medium',
    createdAt: '2024-05-05',
    clearedAt: '2024-05-10',
  },
  {
    uuid: crypto.randomUUID(),
    storyId: crypto.randomUUID(),
    version: 2,
    title: '청계천 8가',
    description: '아아, 짧았던 내 젊음도 헛된 꿈이 아니었으리.',
    playCount: 20,
    clearCount: 2,
    sido: '서울',
    gungu: '종로구',
    imageUrl: 'https://upload.wikimedia.org/wikipedia/commons/b/b8/Seoul_Cheonggyecheon_night.jpg',
    thumbnailImageUrl:
      'https://upload.wikimedia.org/wikipedia/commons/b/b8/Seoul_Cheonggyecheon_night.jpg',
    createdAt: '2024-05-05',
    clearedAt: '2024-05-13',
  },
])

const clearedAtOrderingDirection = ref('DESC')

const clearedStories = computed(() => {
  const ascSorted = _.sortBy(_clearedStories.value, 'clearedAt')
  return clearedAtOrderingDirection.value === 'ASC' ? ascSorted : ascSorted.reverse()
})

const goToStoryRecord = () => {
  router.push({ name: 'story:cleared-record' })
}
</script>

<template>
  <a-layout-content id="content-container">
    <header>
      <h1>
        <TrophyFilled class="trophy-icon" />
        <span id="menu-text">스토리 클리어 기록</span>
        <TrophyFilled class="trophy-icon" />
      </h1>
    </header>
    <article id="main-article">
      <div id="top-submenu-container">
        <div id="simple-statistics-container">클리어 횟수 : {{ clearedStories.length }}회</div>
        <div id="ordering-container">
          {{ clearedAtOrderingDirection === 'DESC' ? '최신순' : '오래된 순' }}
          <a-radio-group v-model:value="clearedAtOrderingDirection" size="small">
            <a-radio-button value="DESC"><CaretUpFilled /></a-radio-button>
            <a-radio-button value="ASC"><CaretDownFilled /></a-radio-button>
          </a-radio-group>
        </div>
      </div>
      <article id="cleared-story-preview-cards-container" @click="goToStoryRecord">
        <StoryPreviewElement :story="story" v-for="story in clearedStories" :key="story.uuid" />
      </article>
    </article>
  </a-layout-content>
  <NavigationView />
</template>

<style scoped lang="scss">
header {
  display: flex;
  justify-content: center;
  text-align: center;
}
.trophy-icon {
  color: gold;
  -webkit-text-stroke: black;
}
h1 {
  display: block;
  padding: 0.5rem;
}
#main-article {
  border: 1px solid silver;
  margin: 0.1rem 0.3rem 0.2rem 0.3rem;
  border-radius: 0.44cm;
}
#menu-text {
  padding: 0.5rem;
}
#top-submenu-container {
  display: flex;
  justify-content: space-between;
  padding: 1rem 1.5rem;
}
#simple-statistics-container {
  display: flex;
  align-items: center;
}
#ordering-container {
  text-align: right;
}
.ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled) {
  color: tomato;
  border-color: tomato;
}
.ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled):hover::before,
.ant-radio-button-wrapper-checked:not(.ant-radio-button-wrapper-disabled):hover::after {
  background-color: tomato;
}
#cleared-story-preview-cards-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}
</style>
