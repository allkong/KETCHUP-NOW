<script setup>
import { computed, inject, onMounted, ref, watch, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import {
  HeartOutlined,
  HeartFilled,
  EnvironmentFilled,
  StarFilled,
  CaretRightFilled,
  UserOutlined,
} from '@ant-design/icons-vue'
import MapPreviewComponent from '@/components/mobile/MapPreviewComponent.vue'
import ReviewPreviewElement from '@/components/mobile/review/ReviewPreviewElement.vue'
import HttpStatus from '@/api/http-status'
import { useStoryZzimStore } from '@/stores/story-zzim'
import { message } from 'ant-design-vue'

const axios = inject('axios')

const router = useRouter()
const storyZzimStore = useStoryZzimStore()

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
  axios
    .post(`/stories/${props.story.uuid}/play`)
    .then(() => {
      message.success('플레이를 시작합니다!')
      router.push({ name: 'play' })
    })
    .catch((error) => {
      if (
        error.response.status === HttpStatus.CONFLICT &&
        error.response.data.detailCode === 'E0002'
      ) {
        message.error('한 번에 하나의 스토리만 플레이 할 수 있습니다.')
      }
    })
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
  storyZzimStore.fetchZzims().then((zzims) => {
    isZzim.value = zzims.filter((zzim) => zzim.storyUuid === props.story.uuid).length > 0
  })
})

watchEffect(() => {
  fetchReviews(props.story.uuid)
  isZzim.value =
    storyZzimStore.zzims.filter((zzim) => zzim.storyUuid === props.story.uuid).length > 0
})

function fetchReviews(storyUuid) {
  axios.get(`/stories/${storyUuid}/reviews`).then((resp) => {
    reviews.value = resp.data
  })
}

const fixedAverageReviewScore = computed(() => {
  return props.story.averageReviewScore.toFixed(1)
})

async function doToggleZzim() {
  isZzim.value = await storyZzimStore.toggleZzim(props.story.uuid)
}
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
        </a-col>
        <a-col>
          <span @click="doToggleZzim">
            <HeartOutlined v-if="!isZzim" style="color: tomato" />
            <HeartFilled v-if="isZzim" style="color: tomato" />
          </span>
        </a-col>
      </a-row>
      <a-row align="middle" justify="space-between">
        <a-col>
          <EnvironmentFilled style="margin-right: 0.3rem; color: crimson" />
          <span>{{ storyFullAddress }}</span>
        </a-col>
        <a-col>
          <StarFilled style="margin-right: 0.3rem; color: #fadb14" /><span
            style="margin-right: 0.8rem"
            >{{ fixedAverageReviewScore }}</span
          >
          <CaretRightFilled style="margin-right: 0.1rem; color: cornflowerblue" /><span>{{
            props.story.totalPlayCount
          }}</span>
        </a-col>
      </a-row>
      <a-tabs v-model:activeKey="activeKey" class="tabs-container">
        <a-tab-pane key="1" tab="설명">
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
      <a-divider></a-divider>
      <UserOutlined style="margin-right: 0.3rem" />
      <span>Created By </span>
      <b style="color: black">{{ props.story.authorNickname }}</b>
    </a-modal>
  </div>
</template>

<style scoped>
.thumbnail {
  background-color: #d9d9d9;
  height: 12rem;
  width: 100%;
  border-radius: 0.5rem;
  object-fit: cover;
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
  min-height: 3rem;
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
