<script setup>
import { defineProps, ref, computed, reactive } from 'vue'
import { StarFilled, StarOutlined } from '@ant-design/icons-vue'

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
})

const review = reactive(props.review)

const MAX_DISPLAYING_REVIEW_CONTENT_LENGTH = 100
const concatenatedReviewContent = computed(() =>
  review.content.length > MAX_DISPLAYING_REVIEW_CONTENT_LENGTH
    ? review.content.slice(0, MAX_DISPLAYING_REVIEW_CONTENT_LENGTH) + ' ...'
    : review.content,
)
</script>

<template>
  <a-card :title="review.title" :headStyle="{ backgroundColor: 'silver', color: 'white' }">
    <div class="review-header" @click="showStarPoint" :star="review.star">
      <span class="review-title">{{ review.storyTitle }}</span>
      <div class="review-start-container">
        <StarFilled
          class="review-star review-filled-star"
          v-for="starIdx in Math.floor(review.star)"
          :key="review.uuid + '_filled_star_' + starIdx"
        />
        <StarOutlined
          class="review-star review-outlined-star"
          v-for="starIdx in 5 - Math.floor(review.star)"
          :key="review.uuid + '_unfilled_star_' + starIdx"
        />
        <span> ({{ review.star }})</span>
      </div>
    </div>
    <div class="review-content-container">
      {{ concatenatedReviewContent }}
    </div>
    <div class="review-btn-container">
      <a-button class="review-update-btn">수정</a-button>
      <a-button class="review-delete-btn">삭제</a-button>
    </div>
  </a-card>
</template>

<style scoped>
.ant-card {
  margin: 1rem 0;
}
.review-header {
  margin-bottom: 0.5rem;
}
.review-title {
  font-size: large;
  font-weight: bold;
  padding-right: 0.3rem;
}
.review-filled-star {
  color: tomato;
}
.review-outlined-star {
  color: silver;
}
.review-content-container {
  border: 1px solid rgb(230, 230, 230);
  padding: 0.5rem;
  border-radius: 5%;
}
.review-btn-container {
  display: flex;
  justify-content: end;
  padding-top: 0.5rem;
}
.review-btn-container > .ant-btn {
  margin: 0 0.2rem;
}
.review-update-btn,
.review-update-btn:hover {
  color: white;
  border-color: darkcyan;
  background-color: darkcyan;
}
.review-delete-btn,
.review-delete-btn:hover {
  color: white;
  border-color: tomato;
  background-color: tomato;
}
</style>
