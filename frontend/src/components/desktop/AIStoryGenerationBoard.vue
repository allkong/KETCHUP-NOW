<script setup>
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
import { BadgeRibbon } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { computed, inject, ref } from 'vue'

const { VITE_AI_API_URL } = import.meta.env

const axios = inject('axios')

const props = defineProps({
  spots: {
    type: Object,
    required: true,
  },
  story: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['refreshSpots'])

const MAX_PROMPT_LENGTH = 100
const additionalPrompt = ref('')
const isAdditionalPromptExceedsMaxLength = computed(() => {
  const exceeds = additionalPrompt.value.length > MAX_PROMPT_LENGTH
  return exceeds
})

const aiGenerationResult = ref([])

const isGenerating = ref(false)
const isGenerated = ref(false)
const isGenerationError = ref(false)

async function requestAIGeneration() {
  isGenerating.value = true
  isGenerated.value = false
  isGenerationError.value = false

  axios
    .post(VITE_AI_API_URL, {
      select: props.spots.length,
      additionalMessage: additionalPrompt.value,
      spots: props.spots.map((s) => {
        return {
          uuid: s.uuid,
          title: s.title,
          description: s.description,
          latitude: s.latitude,
          longitude: s.longitude,
        }
      }),
    })
    .then((resp) => {
      // 결과 받아 써주고
      aiGenerationResult.value = resp.data
      // 기존의 스팟 정보를 덮어씌워줌
      for (let oldIdx = 0; oldIdx < props.spots.length; oldIdx++) {
        const originalSpotInfo = props.spots[oldIdx]

        for (let newIdx = 0; newIdx < aiGenerationResult.value.length; newIdx++) {
          const aiGeneratedSpotInfo = aiGenerationResult.value[newIdx]
          if (originalSpotInfo.uuid === aiGeneratedSpotInfo.uuid) {
            aiGenerationResult.value[newIdx] = {
              ...originalSpotInfo,
              ...aiGenerationResult.value[newIdx],
              oldIdx,
              newIdx,
              oldPreviousOrderIndex: oldIdx === 0 ? null : props.spots[oldIdx-1].uuid,
              newPreviousOrderIndex: newIdx === 0 ? null : aiGenerationResult.value[newIdx-1].uuid
            }
          }
        }
      }

      // 생성 완료 표시
      isGenerated.value = true
    })
    .catch((error) => {
      console.log(error)
      isGenerationError.value = true
      message.error('앗, 다른 요청 사항을 입력해 보세요 😢')
    })
    .finally(() => {
      isGenerating.value = false
    })
}

async function applySuggestedFlow() {
  if (!isGenerated) {
    message.error('아직 생성되지 않았어요.')
    return
  }

  for (let idx = 0; idx < aiGenerationResult.value.length; idx++) {
    const suggestion = aiGenerationResult.value[idx]
    // 새 스토리
    const newDescription = suggestion.story
    // 새 삽입 인덱스
    const oldIdx = suggestion.oldIdx

    // 새 삽입 위치에 있는 스팟의 uuid
    const previousSpotUuid = suggestion.newPreviousOrderIndex

    const updateBody = {
      ...props.spots[oldIdx],
      previousSpotUuid,
      description: newDescription,
    }
    await axios.put(`/stories/${props.story.uuid}/spots/${suggestion.uuid}`, updateBody)
  }
  message.success('적용 완료!')
  emit('refreshSpots')
}
</script>

<template>
  <div id="box">
    <h1>
      AI 스토리 작가
      <a-tooltip placement="right" color="silver">
        <template #title>
          <div>🤖</div>
          <div>선택한 스팟의 제목과 내용을 참고하여 스토리의 내용을 작성해 줍니다.</div>
        </template>
        <a-badge>
          <template #count>
            <QuestionCircleOutlined />
          </template>
        </a-badge>
      </a-tooltip>
    </h1>
    <div>
      <a-textarea
        id="additional-prompt-input"
        v-model:value="additionalPrompt"
        placeholder="추가적인 요청 사항을 입력해 보세요! (100자 이상 입력 불가)"
        :status="isAdditionalPromptExceedsMaxLength ? 'error' : ''"
        :showCount="true"
        :rows="4"
        :autosize="{ minRows: 4, maxRows: 4 }"
      />
      <a-button
        id="generate-button"
        type="primary"
        :loading="isGenerating"
        @click="requestAIGeneration"
        :disabled="isAdditionalPromptExceedsMaxLength"
        >생성하기</a-button
      >
      <a-button v-show="isGenerated" id="apply-button" type="primary" @click="applySuggestedFlow"
        >적용하기</a-button
      >
    </div>
    <div class="menu-container" v-show="isGenerated">
      <div class="list-body">
        <a-list>
          <a-list-item v-for="suggestion of aiGenerationResult" :key="suggestion.uuid">
            <BadgeRibbon
              :text="
                suggestion.oldIdx === suggestion.newIdx
                  ? suggestion.oldIdx + 1
                  : `${suggestion.oldIdx + 1} -> ${suggestion.newIdx + 1}`
              "
              :color="suggestion.oldIdx === suggestion.newIdx ? 'green' : 'tomato'"
            >
              <a-card :title="suggestion.title" class="ai-suggestion-story-card">
                <div>{{ suggestion.story }}</div>
              </a-card>
            </BadgeRibbon>
          </a-list-item>
        </a-list>
      </div>
    </div>
  </div>
</template>

<style scoped>
#box {
  display: flex;
  flex-direction: column;
}

h1 {
  text-align: center;
  font-size: 1.4rem;
  margin: 1rem 0;
}

h1 .anticon-question-circle {
  color: silver;
}

#additional-prompt-input {
  height: 10rem;
}

#generate-button {
  background-color: tomato;
  width: 100%;
  margin-top: 0.5rem;
}

#apply-button {
  background-color: skyblue;
  width: 100%;
  margin-top: 0.5rem;
}

.menu-container {
  max-height: 35rem;
  min-height: 35rem;
  overflow-y: auto;
  margin-top: 1rem;
}

.ai-suggestion-story-card {
  margin-bottom: 10px;
}

.ai-suggestion-story-card {
  width: 100%;
}

.ant-ribbon-wrapper {
  width: 100%;
}

.order-change-info {
  font-size: small;
}

.menu-container {
  overflow: auto;
  height: 90%;
}

/* 스크롤바 컨테이너 스타일 */
.menu-container::-webkit-scrollbar {
  width: 0.3rem;
}

/* 스크롤바 핸들 스타일 */
.menu-container::-webkit-scrollbar-thumb {
  background: #cacaca;
  border-radius: 10px;
}

/* 스크롤바 트랙 스타일 */
.menu-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 1rem;
}

:deep(.ant-list-item) {
  border-block-end: none;
}
</style>
