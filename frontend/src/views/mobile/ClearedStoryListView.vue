<script setup>
import _ from 'lodash'
import { ref, computed, onMounted, inject } from 'vue'
import { TrophyFilled, CaretUpFilled, CaretDownFilled } from '@ant-design/icons-vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import PlayingPreviewElement from '@/components/mobile/story/PlayingPreviewElement.vue'
import { useRouter } from 'vue-router'

const axios = inject('axios')

const router = useRouter()

const _clearedPlayings = ref([])

const clearedAtOrderingDirection = ref('DESC')

const clearedPlayings = computed(() => {
  const ascSorted = _.sortBy(_clearedPlayings.value, 'clearedAt').filter(
    (playing) => playing.status === 'CLEARED',
  )
  return clearedAtOrderingDirection.value === 'ASC' ? ascSorted : ascSorted.reverse()
})

onMounted(async () => {
  axios.get('/playings').then((resp) => {
    _clearedPlayings.value = resp.data
    _clearedPlayings.value.forEach((playing, idx) => {
      axios.get(`/stories/${playing.storyUuid}`).then((resp) => {
        const story = resp.data
        _clearedPlayings.value[idx] = {
          ...story,
          ..._clearedPlayings.value[idx],
          storyCreatedAt: story.createdAt,
          playingStartedAt: _clearedPlayings.value[idx].createdAt,
        }
      })
    })
  })
})
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
        <div id="simple-statistics-container">클리어 횟수 : {{ clearedPlayings.length }}회</div>
        <div id="ordering-container">
          {{ clearedAtOrderingDirection === 'DESC' ? '최신순' : '오래된 순' }}
          <a-radio-group v-model:value="clearedAtOrderingDirection" size="small">
            <a-radio-button value="DESC"><CaretUpFilled /></a-radio-button>
            <a-radio-button value="ASC"><CaretDownFilled /></a-radio-button>
          </a-radio-group>
        </div>
      </div>
      <article id="cleared-story-preview-cards-container">
        <PlayingPreviewElement
          :playing="playing"
          v-for="playing in clearedPlayings"
          :key="playing.uuid"
        />
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
  border-radius: 0.5rem;
  height: 80vh;
  overflow: auto;
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
