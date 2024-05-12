<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import MapComponent from '@/components/mobile/MapComponent.vue'
import ReviewList from '@/components/mobile/review/ReviewPreviewElement.vue'

const router = useRouter()

const reviews = ref([
  {
    uuid: crypto.randomUUID(),
    title: '기타에 불 붙혀보셨나요?',
    star: 4.3,
    content:
      '세상 머리가 해맑던 어린 내가 떠올라. 장래 희망을 물으면 10개를 답했던 아이. 대통령, 선생님, 때론 문방구 주인, 때론 아름다운 할머니 된대!',
    createdAt: '2024-05-10',
  },
  {
    uuid: crypto.randomUUID(),
    title: '바퀴벌레 나왔어요',
    star: 2.2,
    content:
      '마마 왜 내 심장은 가짜야? 나는 왜 찢겨도 붉은 피 하나 나지 않는 가짜야? 다들 물어본다고요. 너도 겨울을 아냐고. 마른 가지 같은 손가락이 왜 슬픈 줄 아냐고. 그럼 당연히 알지 왜 몰라, 그 잔가지 위에 업힌 나의 생. 그럼 당연히 알지 왜 몰라, 그 잔가지 위에 업힌 나의 생.',
    createdAt: '2024-05-10',
  },
])

const props = defineProps({
  modalOpen: Boolean,
})

const isOpen = ref(props.modalOpen)
const activeKey = ref('1')

const handleOk = (e) => {
  console.log(e)
  router.push({ name: 'play' })
}
</script>

<template>
  <div>
    <a-modal
      v-model:open="isOpen"
      title="Story"
      centered
      okText="PLAY!"
      @cancel="$emit('closeModal')"
      @ok="handleOk"
    >
      <div class="thumnail"></div>
      <div>
        <h2 :style="{ display: 'inline' }">싸피산책로</h2>
      </div>
      <span>서울시 강남구</span>

      <a-tabs v-model:activeKey="activeKey" class="tabs-container">
        <a-tab-pane key="1" tab="설명">
          <p>김싸피</p>
          <div class="tab-textarea">
            이 스토리는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고
            지금은 당신에게로 옮겨진 이 스토리는 4일 안에 당신 곁을 떠나야 합니다. 이 스토리를
            포함해서 7통을 행운이 필요한 사람에게 보내 주셔야 합니다. 복사를 해도 좋습니다. 혹
            미신이라 하실지 모르지만 사실입니다.
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="지도">
          <div class="tab-map">
            <MapComponent />
          </div>
        </a-tab-pane>
        <a-tab-pane key="3" tab="리뷰">
          <div class="tab-review">
            <ReviewList
              :review="review"
              :render-footer="false"
              v-for="review in reviews"
              :key="review.uuid"
            />
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<style scoped>
h2 {
  margin-bottom: 0.5rem;
}

.thumnail {
  background-color: #d9d9d9;
  height: 12rem;
  border-radius: 0.5rem;
}

.tabs-container {
  margin-top: 1rem;
}

.tab-textarea {
  max-height: 12rem;
  overflow: auto;
}

.tab-map {
  width: 100%;
  height: 15rem;
  border-radius: 0.5rem;
  overflow: hidden;
  margin: 1rem 0 1rem 0;
}

.tab-review {
  max-height: 17rem;
  overflow: auto;
}
</style>
