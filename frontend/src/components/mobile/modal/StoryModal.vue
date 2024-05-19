<script setup>
import { computed, inject, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  HeartOutlined,
  HeartFilled,
  EnvironmentOutlined,
  StarFilled,
  CaretRightFilled,
} from '@ant-design/icons-vue'
import MapPreviewComponent from '@/components/mobile/MapPreviewComponent.vue'
import ReviewPreviewElement from '@/components/mobile/review/ReviewPreviewElement.vue'
import HttpStatus from '@/api/http-status'

const axios = inject('axios')

const router = useRouter()

const props = defineProps({
  modalOpen: Boolean,
  story: {
    type: Object,
    required: true,
  },
})

const isOpen = ref(props.modalOpen)
const reviews = ref([])
const isZzim = ref(false)
const activeKey = ref('1')

const handleOk = (e) => {
  router.push({ name: 'play' })
}

const storyFullAddress = computed(() => {
  if (!props.story.sido) {
    return '주소 미입력'
  } else if (!props.story.gungu) {
    return props.story.gungu
  } else {
    return `${props.story.sido} ${props.story.gungu}`
  }
})

onMounted(() => {
  fetchReviews(props.story.uuid)
  fetchZzims()
})

watch(props.story, () => {
  fetchReviews(props.story.uuid)
})

function fetchReviews(storyUuid) {
  axios.get(`/stories/${storyUuid}/reviews`).then((resp) => {
    reviews.value = resp.data
  })
}

function fetchZzims() {
  axios.get(`/users/me/zzims`).then((resp) => {
    const myZzims = resp.data
    isZzim.value = myZzims.filter((zzim) => zzim.storyUuid === props.story.uuid).length > 0
  })
}

async function toggleZzim() {
  axios.post(`/stories/${props.story.uuid}/zzims`).then((resp) => {
    // 찜 처리됨
    if (resp.status === HttpStatus.OK) {
      isZzim.value = true
    }
    // 찜 해제 처리됨
    else if (resp.status === HttpStatus.NO_CONTENT) {
      isZzim.value = false
    }
  })
}

const fixedAverageReviewScore = computed(() => {
  return props.story.averageReviewScore.toFixed(1)
})
</script>

<template>
  <div>
    <a-modal
      v-model:open="isOpen"
      centered
      cancelText="닫기"
      okText="PLAY!"
      @cancel="$emit('closeStoryModal')"
      @ok="handleOk"
    >
      <img class="thumbnail" :src="props.story.imageUri" @error="$replaceDefaultImage" alt="" />
      <a-row align="middle" justify="space-between">
        <a-col>
          <h2>{{ props.story.title }}</h2>
          <!-- <span>v1</span> -->
        </a-col>
        <a-col>
          <span @click="toggleZzim">
            <HeartOutlined v-if="!isZzim" />
            <HeartFilled v-if="isZzim" />
          </span>
        </a-col>
      </a-row>
      <a-row align="middle" justify="space-between">
        <a-col>
          <EnvironmentOutlined />
          <span>{{ storyFullAddress }}</span>
        </a-col>
        <a-col>
          <StarFilled /><span>{{ fixedAverageReviewScore }}</span> <CaretRightFilled /><span>{{
            props.story.totalPlayCount
          }}</span>
        </a-col>
      </a-row>

      <a-tabs v-model:activeKey="activeKey" class="tabs-container">
        <a-tab-pane key="1" tab="설명">
          <u id="author-nickname">Created by {{ props.story.authorNickname }}</u>
          <div class="tab-textarea">
            {{ props.story.description }}
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="지도">
          <div class="tab-map">
            <MapPreviewComponent :story="props.story" />
          </div>
        </a-tab-pane>
        <a-tab-pane key="3" tab="리뷰">
          <div class="tab-review" v-if="reviews.length > 0">
            <ReviewPreviewElement
              :review="review"
              :render-footer="false"
              v-for="review in reviews"
              :key="review.uuid"
            />
          </div>
          <div class="tab-review" v-if="reviews.length === 0">
            <div>아직 리뷰가 없어요.</div>
            <div>이야기의 첫 주인공이 되어 볼까요?</div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<style scoped>
.thumbnail {
  background-color: #d9d9d9;
  height: 12rem;
  width: 100%;
  border-radius: 0.5rem;
}

.anticon-heart {
  font-size: 1.5rem;
}

.anticon-caret-right {
  font-size: 1rem;
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

#author-nickname {
  text-underline-position: under;
}
</style>
