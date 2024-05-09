<template>
  <a-layout-content id="content-container">
    <GoBackIcon></GoBackIcon>
    <h1>나의 리뷰</h1>
    <div id="review-card-container">
      <ReviewPreviewElement :review="review" v-for="review in reviews" :key="review.uuid" />
    </div>
  </a-layout-content>
  <NavigationView />
</template>

<script setup>
import { ref } from 'vue'

import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import ReviewPreviewElement from '@/components/mobile/review/ReviewPreviewElement.vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'

const reviews = ref([
  {
    uuid: crypto.randomUUID(),
    storyTitle: '김광석 거리 산책',
    title: '기타에 불 붙혀보셨나요?',
    star: 4.3,
    content:
      '세상 머리가 해맑던 어린 내가 떠올라. 장래 희망을 물으면 10개를 답했던 아이. 대통령, 선생님, 때론 문방구 주인, 때론 아름다운 할머니 된대!',
    createdAt: '2024-05-10',
  },
  {
    uuid: crypto.randomUUID(),
    storyTitle: '광진구 박유빈 가택 탐험',
    title: '바퀴벌레 나왔어요',
    star: 2.2,
    content:
      '마마 왜 내 심장은 가짜야? 나는 왜 찢겨도 붉은 피 하나 나지 않는 가짜야? 다들 물어본다고요. 너도 겨울을 아냐고. 마른 가지 같은 손가락이 왜 슬픈 줄 아냐고. 그럼 당연히 알지 왜 몰라, 그 잔가지 위에 업힌 나의 생. 그럼 당연히 알지 왜 몰라, 그 잔가지 위에 업힌 나의 생.',
    createdAt: '2024-05-10',
  },
])
</script>

<style scoped lang="scss">
#content-container {
  background: $whiteBackgroundColor;
  padding: 1rem;
}
h1 {
  text-align: center;
}
</style>
