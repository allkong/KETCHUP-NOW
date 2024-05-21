<script setup>
import { computed, inject, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { EnvironmentOutlined } from '@ant-design/icons-vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'
import MapPreviewComponent from '@/components/mobile/MapPreviewComponent.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'

const route = useRoute()
const axios = inject('axios')

const story = ref({
  uuid: route.params.storyUuid,
  version: 1,
  title: '',
  description: '',
  sido: '',
  gungu: '',
  imageUri: '',
  spots: [],
  logs: [],
})

onMounted(async () => {
  // 플레이한 스토리 정보
  const fetchedStory = (await axios.get(`/stories/${route.params.storyUuid}`)).data
  for(let key of ['version', 'title', 'description', 'sido', 'gungu', 'imageUri']) {
    story.value[key] = fetchedStory[key]
  }

  // 해당 스토리의 하위 스팟
  story.value.spots = (await axios.get(`/stories/${route.params.storyUuid}/spots`)).data
  story.value.spots.sort((s1, s2) => s1.orderIndex - s2.orderIndex)

  // 플레이 기록
  const _logs = (await axios.get(`/playings/${route.params.uuid}/logs`)).data
  story.value.logs = []
  for(let spot of story.value.spots) {
    for(let log of _logs) {
      if (spot.uuid === log.spotUuid) {
        story.value.logs.push(log)
      }
    }
  }
})

const narrationScriptExample = '경복궁의 정전으로, 국가적인 대례를 거행하던 장소이다. 다포식 건축으로는 현존하는 것 중 국내 최대 규모이며, 2단의 석축기단 위에 정면 5칸, 측면 5칸의 2층 전각으로 구성되어 있는데, 천장에 있는 금박 입힌 2마리의 목조 용 조각의 발톱이 7개인 것이 특징이다. 1395년(태조 4년) 건설되었다가 1592년(선조 25년) 임진왜란으로 소실된 것을 1867년(고종 4년) 중건했다. 일제가 조선총독부를 지을 때도 철거당하지 않았으며 1985년 1월 8일 국보 제223호로 지정되었다.'
</script>

<template>
  <a-layout-content id="content-container">
    <div class="story-image">
      <GoBackIcon class="back-icon" />
      <img :src="story.imageUri" alt="" />
    </div>
    <div class="detail-container">
      <a-row align="middle">
        <a-col :span="24">
          <h1>{{ story.title }}</h1>
          <span> v.{{ story.version }}</span>
        </a-col>
        <a-col>
          <EnvironmentOutlined />
          <span>{{ story.sido }} {{ story.gungu }}</span>
        </a-col>
      </a-row>
      <a-row>
        <p>{{ story.description }}</p>
      </a-row>
      <div class="map-content"><MapPreviewComponent :story="story" /></div>
      <a-timeline>
        <a-timeline-item v-for="idx in story.logs.length" :key="idx">
          <a-collapse accordion>
            <a-collapse-panel key="1" :header="`스토리 : ${story.spots[idx-1].title}`">
              {{ story.spots[idx-1].description }}
            </a-collapse-panel>
            <a-collapse-panel key="2" :header="`클리어 : ${story.logs[idx-1].createdAt.split('T').join(' | ')}`">
              <!-- 객관식 퀴즈 -->
              <div v-if="idx === 1">
                <sub>이벤트 유형 : 객관식 퀴즈</sub>
                <div><b>Q. 다음 중 세종대의 편찬물이 아닌 것은?</b></div>
                <a-radio-group value="3">
                  <a-radio value="1" disabled="true">훈민정음</a-radio>
                  <br/>
                  <a-radio value="2" disabled="true">칠정산내외편</a-radio>
                  <br/>
                  <a-radio value="3" disabled="true" autofocus>제왕운기</a-radio>
                  <br/>
                  <a-radio value="4" disabled="true">농사직설</a-radio>
                </a-radio-group>
              </div>
              <!-- 주관식 퀴즈 -->
              <div v-if="idx === 2">
                <sub>이벤트 유형 : 주관식 퀴즈</sub>
                <div><b>Q. 교태전 뒷편에 있는 경복궁 내부의 인공산으로, 경회루의 연못을 팔 때 나온 흙으로 쌓아 만든 이 산의 이름은?</b></div>
                <div>
                  <a-input disabled="true" value="아미산" />
                </div>
              </div>
              <!-- 나레이션 -->
              <div v-if="idx === 3">
                <sub>이벤트 유형 : 나레이션</sub>
                <div>
                  {{ narrationScriptExample }}
                </div>
              </div>
            </a-collapse-panel>
          </a-collapse>
        </a-timeline-item>
      </a-timeline>
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

.map-content {
  width: 100%;
  height: 15rem;
  border-radius: 0.5rem;
  overflow: hidden;
  margin: 1rem 0 1rem 0;
}
</style>
