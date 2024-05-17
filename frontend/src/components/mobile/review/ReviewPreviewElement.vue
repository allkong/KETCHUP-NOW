<script setup>
import { computed, ref } from 'vue'
import { StarFilled, StarOutlined } from '@ant-design/icons-vue'

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
  renderHeader: {
    type: Boolean,
    required: false,
    default: true,
  },
  renderFooter: {
    type: Boolean,
    required: false,
    default: true,
  },
})

const emit = defineEmits(['reviewModifyBtnClick', 'reviewDeleteBtnClick'])

const MAX_DISPLAYING_REVIEW_CONTENT_LENGTH = 100
const concatenatedReviewContent = computed(() =>
  props.review.content.length > MAX_DISPLAYING_REVIEW_CONTENT_LENGTH
    ? props.review.content.slice(0, MAX_DISPLAYING_REVIEW_CONTENT_LENGTH) + ' ...'
    : props.review.content,
)
</script>

<template>
  <a-card
    :title="props.renderHeader ? props.review.storyTitle : null"
    :headStyle="{ backgroundColor: 'silver', color: 'white' }"
  >
    <div class="review-header" @click="showStarPoint" :point="review.point">
      <span class="review-title">{{ review.title }}</span>
      <div class="review-start-container">
        <a-rate :value="props.review.score" disabled />
        <span> ({{ props.review.score }})</span>
      </div>
    </div>
    <div class="review-content-container">
      {{ concatenatedReviewContent }}
    </div>
    <div class="review-btn-container" v-if="renderFooter">
      <a-button class="review-update-btn" @click="$emit('reviewModifyBtnClick', review)">
        수정
      </a-button>
      <a-button class="review-delete-btn" @click="$emit('reviewDeleteBtnClick', review)"
        >삭제</a-button
      >
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
.ant-rate {
  color: tomato;
}
</style>
