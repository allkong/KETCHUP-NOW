<script setup>
import { computed, inject, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { EnvironmentOutlined, EnvironmentFilled } from '@ant-design/icons-vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'
import MapPreviewComponent from '@/components/mobile/MapPreviewComponent.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import DefaultImage from '@/assets/default-image.jpg'

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
  for (let key of ['version', 'title', 'description', 'sido', 'gungu', 'imageUri']) {
    story.value[key] = fetchedStory[key]
  }

  // 해당 스토리의 하위 스팟
  story.value.spots = (await axios.get(`/stories/${route.params.storyUuid}/spots`)).data
  story.value.spots.sort((s1, s2) => s1.orderIndex - s2.orderIndex)

  // 플레이 기록
  const _logs = (await axios.get(`/playings/${route.params.uuid}/logs`)).data
  story.value.logs = []
  for (let spot of story.value.spots) {
    for (let log of _logs) {
      if (spot.uuid === log.spotUuid) {
        story.value.logs.push(log)
      }
    }
  }

  story.value.spots.forEach((spot) => {
    spot.jsonEventContent = JSON.parse(spot.jsonEventContent)
  })
})

console.log(story.value)
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
        </a-col>
        <a-col style="margin-top: 0.6rem">
          <EnvironmentFilled style="color: crimson; margin-right: 0.3rem" />
          <span>{{ story.sido }} {{ story.gungu }}</span>
        </a-col>
      </a-row>
      <a-row>
        <p>{{ story.description }}</p>
      </a-row>
      <a-divider style="margin: 0.5rem 0 1.5rem 0"></a-divider>
      <div>
        <h3>지도 보기</h3>
        <div class="map-content"><MapPreviewComponent :story="story" /></div>
      </div>
      <a-divider></a-divider>
      <div>
        <h3>타임라인</h3>
        <a-timeline style="margin-top: 2rem">
          <a-timeline-item v-for="idx in story.logs.length" :key="idx">
            <a-collapse accordion>
              <a-collapse-panel key="1" :header="`스토리 : ${story.spots[idx - 1].title}`">
                <div class="spot-cover-image">
                  <a-image
                    :src="story.spots[idx - 1].imageUri || DefaultImage"
                    @error="$replaceDefaultImage"
                  ></a-image>
                </div>
                <div>
                  {{ story.spots[idx - 1].description }}
                </div>
              </a-collapse-panel>
              <a-collapse-panel
                key="2"
                :header="`클리어 : ${story.logs[idx - 1].createdAt.split('T').join(' | ')}`"
              >
                <div v-if="story.spots[idx - 1].eventType === 'QUIZ'">
                  <!-- <p>{{ story.spots[idx - 1].jsonEventContent }}</p> -->

                  <sub class="sub-text">주관식 QUIZ</sub>
                  <div>
                    <b>Q. {{ story.spots[idx - 1].jsonEventContent.title }}</b>
                    <p>{{ story.spots[idx - 1].jsonEventContent.content }}</p>
                  </div>
                  <div>
                    <a-input :value="story.spots[idx - 1].jsonEventContent.answer" disabled />
                  </div>
                </div>
                <div v-else>미션이 없는 스팟입니다. 😅</div>
                <!-- 객관식 퀴즈 -->
                <!-- <div v-if="idx === 1">
                  <sub>이벤트 유형 : 객관식 퀴즈</sub>
                  <div><b>Q. 다음 중 세종대의 편찬물이 아닌 것은?</b></div>
                  <a-radio-group value="3">
                    <a-radio value="1" disabled="true">훈민정음</a-radio>
                    <br />
                    <a-radio value="2" disabled="true">칠정산내외편</a-radio>
                    <br />
                    <a-radio value="3" disabled="true" autofocus>제왕운기</a-radio>
                    <br />
                    <a-radio value="4" disabled="true">농사직설</a-radio>
                  </a-radio-group>
                </div> -->
                <!-- 주관식 퀴즈 -->
                <!-- <div v-if="idx === 2">
                  <sub>이벤트 유형 : 주관식 퀴즈</sub>
                  <div>
                    <b
                      >Q. 교태전 뒷편에 있는 경복궁 내부의 인공산으로, 경회루의 연못을 팔 때 나온
                      흙으로 쌓아 만든 이 산의 이름은?</b
                    >
                  </div>
                  <div>
                    <a-input disabled="true" value="아미산" />
                  </div>
                </div> -->
                <!-- 나레이션 -->
                <!-- <div v-if="idx === 3">
                  <sub>이벤트 유형 : 나레이션</sub>
                  <div>
                    {{ narrationScriptExample }}
                  </div>
                </div> -->
              </a-collapse-panel>
            </a-collapse>
          </a-timeline-item>
        </a-timeline>
      </div>
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

.sub-text {
  color: lightslategray;
}

:deep(.ant-timeline .ant-timeline-item-head-blue) {
  border-color: tomato;
}

:deep(.ant-image) {
  width: 100%;
  height: 100%;
  border-radius: 1rem;
}

:deep(.ant-image-img) {
  width: 100%;
  height: 100%;
  border-radius: 1rem;
  object-fit: cover;
}

:deep(.ant-image-mask) {
  width: 8rem;
  height: 8rem;
  border-radius: 1rem;
}

.spot-cover-image {
  width: 8rem;
  height: 8rem;
  border-radius: 1rem;
  margin-bottom: 1rem;
}
</style>
