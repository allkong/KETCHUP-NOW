<script setup>
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { computed, ref } from 'vue'

const MAX_PROMPT_LENGTH = 50

const additionalPrompt = ref('')
const isAdditionalPromptExceedsMaxLength = computed(() => {
  const exceeds = additionalPrompt.value.length > MAX_PROMPT_LENGTH
  return exceeds
})

const isGenerating = ref(false)
const isGenerated = ref(false)

function requestAIGeneration() {
  isGenerating.value = true
  setTimeout(() => {
    isGenerating.value = false

    // 성공
    isGenerated.value = true
  }, 3000)
}
</script>

<template>
  <div id="box">
    <h1>
      AI 스토리 작가
      <a-tooltip placement="right" color="silver">
        <template #title>
          <div>🤖</div>
          <div>선택한 스팟의 제목과 내용을 참고하여</div>
          <div>스토리의 내용을 작성해 줍니다</div>
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
        placeholder="추가적인 요청 사항을 입력해 보세요! (50자 이상 입력 불가)"
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
        >생성</a-button
      >
      <a-button id="apply-button" type="primary"> 반영 </a-button>
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
</style>
