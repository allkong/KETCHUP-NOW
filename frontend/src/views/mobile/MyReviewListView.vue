<template>
  <a-layout-content id="content-container">
    <GoBackIcon></GoBackIcon>
    <div id="review-card-container">
      <div v-if="reviews.length > 0">
        <h1>나의 리뷰</h1>
        <ReviewPreviewElement
          :review="review"
          v-for="review in reviews"
          :key="review.uuid"
          @review-modify-btn-click="onReviewModifyBtnClicked"
          @review-delete-btn-click="onReviewDeleteBtnClicked"
        />
      </div>
      <div id="no-review-message-container" v-if="reviews.length === 0">
        <h1 id="no-review-message">😢 아직 리뷰가 없어요 😢</h1>
        <div style="">여행자님의 생생한 후기를 들려주세요!</div>
      </div>
    </div>
  </a-layout-content>
  <NavigationView />

  <a-modal v-model:open="isModifyModalOpen" title="리뷰 수정" @ok="onReviewModifyOk">
    <a-rate id="modify-review-score" v-model:value="modifyForm.score" />
    <a-input id="modify-review-title" type="text" v-model:value="modifyForm.title" />
    <a-textarea id="modify-review-text" v-model:value="modifyForm.content" :rows="4" />
  </a-modal>

  <a-modal v-model:open="isDeleteModalOpen" title="리뷰 삭제" @ok="onReviewDeleteOk">
    <div>정말 삭제하시겠습니까?</div>
  </a-modal>
</template>

<script setup>
import { createVNode, inject, onMounted, ref } from 'vue'
import { Modal } from 'ant-design-vue'
import { WarningOutlined, CrownOutlined } from '@ant-design/icons-vue'

import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import ReviewPreviewElement from '@/components/mobile/review/ReviewPreviewElement.vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'
import { message } from 'ant-design-vue'

const axios = inject('axios')

const reviews = ref([])

function fetchMyReviews() {
  axios.get('/reviews/me').then((resp) => {
    reviews.value = resp.data
  })
}

onMounted(async () => {
  fetchMyReviews()
})

const isModifyModalOpen = ref(false)
const modifyForm = ref({
  uuid: '',
  title: '',
  content: '',
  score: 0,
})
function onReviewModifyBtnClicked(...args) {
  const currentValue = args[0]
  modifyForm.value.uuid = currentValue.uuid
  modifyForm.value.title = currentValue.title
  modifyForm.value.content = currentValue.content
  modifyForm.value.score = currentValue.score

  isModifyModalOpen.value = true
}

function onReviewModifyOk() {
  if (modifyForm.value.title.length === 0 || modifyForm.value.content.length === 0) {
    message.warn('제목과 내용을 모두 적어주세요.')
    return
  }

  axios
    .put(`/reviews/${modifyForm.value.uuid}`, {
      ...modifyForm.value,
    })
    .then((resp) => {
      const updatedReview = resp.data
      for (let reviewIdx = 0; reviewIdx < reviews.value.length; reviewIdx++) {
        if (reviews.value[reviewIdx].uuid === updatedReview.uuid) {
          reviews.value[reviewIdx] = updatedReview
          break
        }
      }
      message.success('수정 완료!')
    })
    .catch((error) => {
      console.log(error)
      message.error('에러가 발생했습니다.')
    })
    .finally(() => {
      isModifyModalOpen.value = false
    })
}

const deleteTargetUuid = ref('')
function onReviewDeleteBtnClicked(...args) {
  const currentValue = args[0]
  deleteTargetUuid.value = currentValue.uuid

  Modal.confirm({
    title: () => '리뷰 삭제',
    icon: () => createVNode(WarningOutlined),
    content: () => '정말 삭제하시겠습니까?',
    onOk: () => {
      axios
        .delete(`/reviews/${deleteTargetUuid.value}`)
        .then(() => {
          reviews.value = reviews.value.filter((review) => review.uuid !== deleteTargetUuid.value)
          message.success('삭제 완료!')
        })
        .catch((error) => {
          console.log(error)
          message.error('에러가 발생했습니다.')
        })
        .finally(() => {})
    },
    okType: 'danger',
    okText: '삭제',
    cancelText: () => '취소',
    onCancel: () => {},
  })
}
</script>

<style scoped lang="scss">
#content-container {
  background: $whiteBackgroundColor;
  padding: 1rem;
}
h1 {
  text-align: center;
}
#modify-review-score {
  color: tomato;
  margin-bottom: 0.7rem;
}
#modify-review-title {
  margin-bottom: 0.9rem;
}
#no-review-message-container {
  text-align: center;
}
#no-review-message {
  color: tomato;
  font-weight: bold;
}
</style>
