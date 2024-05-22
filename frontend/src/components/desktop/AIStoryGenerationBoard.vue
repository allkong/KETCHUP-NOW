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
              oldIdx: oldIdx + 1,
              newIdx: newIdx + 1,
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

  console.log('before:', props.spots)

  for (let idx = 0; idx < aiGenerationResult.value.length; idx++) {
    const suggestion = aiGenerationResult.value[idx]
    // 새 스토리
    const newDescription = suggestion.story
    // 새 삽입 인덱스
    const newIdx = suggestion.newIdx
    const oldIdx = suggestion.oldIdx

    // 새 삽입 위치에 있는 스팟의 uuid
    const previousSpotUuid = props.spots[newIdx - 1].uuid

    const updateBody = {
      ...props.spots[oldIdx - 1],
      previousSpotUuid,
      description: newDescription,
    }

    console.log('for', suggestion.uuid, ':', updateBody)

    console.log('update : ', oldIdx, '~', newIdx)
    await axios.put(`/stories/${props.story.uuid}/spots/${suggestion.uuid}`, updateBody)
    // props.spots = (await axios.get(`/stories/${props.story.uuid}/spots`)).data
    emit('refreshSpots')
  }

  console.log('after:', props.spots)
}
</script>

<template>
  <div id="box">
    <h1>
      AI 스토리 작가
      <a-tooltip placement="right" color="silver">
        <template #title>
          <div>🤖</div>
          <div>선택한 스팟의 제목과 내용을 참고하여 스토리의 내용을 작성해 줍니다</div>
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
        >생성</a-button
      >
      <a-button v-show="isGenerated" id="apply-button" type="primary" @click="applySuggestedFlow"
        >적용하기</a-button
      >
    </div>
    <div class="menu-container" v-show="isGenerated">
      <div class="list-body">
        <a-list bordered>
          <a-list-item v-for="suggestion of aiGenerationResult" :key="suggestion.uuid">
            <BadgeRibbon
              :text="
                suggestion.oldIdx === suggestion.newIdx
                  ? suggestion.oldIdx
                  : `${suggestion.oldIdx} -> ${suggestion.newIdx}`
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

  padding: 2%;
}

h1 {
  text-align: center;
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
</style>
