<script setup>
import { ref, computed, inject } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UploadOutlined, PaperClipOutlined } from '@ant-design/icons-vue'
import RegionButton from '@/components/button/RegionButton.vue'

const route = useRoute()
const axios = inject('axios')

const emit = defineEmits(['closeAddStoryModal', 'updateStories'])
const props = defineProps({
  modalOpen: Boolean,
})

const ALLOWED_FILE_TYPES = ['jpeg', 'png', 'gif', 'jpg']
const isOpen = ref(props.modalOpen)
const story = ref({
  imageFile: {},
  createDto: {
    title: '',
    description: '',
    sido: '',
    gungu: '',
  },
})

const onSaveRegion = (...args) => {
  story.value.createDto.sido = args[0]
  story.value.createDto.gungu = args[1]
}

const onFileChange = (e) => {
  // 파일이 선택되지 않은 상태로 파일 선택 창이 닫히면 입력 비우기
  if (!e.target.files.length) {
    story.value.imageFile = {}
    return
  }

  // 파일 확장자로 이미지인지 확인
  const uploadFile = e.target.files[0]
  const ext = uploadFile.name.split('.').pop()
  if (!ALLOWED_FILE_TYPES.find((allowedExt) => allowedExt === ext)) {
    message.error('이미지 파일만 가능합니다.')
    story.value.imageFile = {}
    return
  }

  story.value.imageFile = uploadFile
}

const onAddStory = () => {
  const formData = new FormData()
  formData.append('imageFile', story.value.imageFile)
  formData.append(
    'createDto',
    new Blob([JSON.stringify(story.value.createDto)], { type: 'application/json' }),
  )

  axios
    .post('/stories', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    .then((response) => {
      emit('updateStories')
    })
    .catch((error) => console.error(error))
}
</script>

<template>
  <a-modal
    v-model:open="isOpen"
    width="25rem"
    centered
    cancelText="취소"
    okText="생성"
    @cancel="$emit('closeAddStoryModal')"
    @ok="onAddStory"
  >
    <a-row align="middle" justify="center">
      <a-col>
        <img src="@/assets/icon/open-book.png" class="book-icon" />
      </a-col>
      <a-col>
        <h2>스토리 생성</h2>
      </a-col>
    </a-row>
    <div class="input-container">
      <a-row justify="center" class="input-form">
        <a-col class="input-label">제목</a-col>
        <a-col>
          <a-input v-model:value="story.createDto.title" class="input-box" />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">설명</a-col>
        <a-col>
          <a-textarea v-model:value="story.createDto.description" class="input-box" />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">지역</a-col>
        <a-col>
          <RegionButton class="input-box" @area-select-event="onSaveRegion" />
        </a-col>
      </a-row>
      <a-row justify="center" class="image-form">
        <a-col>
          <!-- 기본 파일 업로드 UI를 가려서 파일을 안올려도 파일 이름이 화면에 남아 있는 문제 해결 -->
          <input id="upload-image" type="file" hidden @change="onFileChange" />
          <!-- label 태그를 붙였기 때문에 해당 영역을 클릭해도 파일 업로드 창이 뜸 -->
          <label id="upload-img-label" for="upload-image">
            <UploadOutlined style="margin-right: 0.3rem" />
            <span>이미지 업로드</span>
          </label>
          <!-- 파일 이름 표시 -->
          <div v-if="story.imageFile.name" class="image-name">
            <PaperClipOutlined />{{ story.imageFile.name }}
          </div>
          <div v-else class="image-name">이미지 없음</div>
        </a-col>
      </a-row>
    </div>
  </a-modal>
</template>

<style scoped>
h2 {
  margin: 0;
}

.book-icon {
  width: 2rem;
  height: 2rem;
  margin-right: 0.5rem;
}

.modal-button {
  width: 4rem;
  display: flex;
  justify-content: center;
}

.input-container {
  margin: 2rem 0;
}

.input-form {
  margin: 1rem 0;
}

.input-label {
  display: flex;
  align-items: center;
  justify-content: end;
  padding-right: 1rem;
}

.input-box {
  width: 18rem;
}

.image-form {
  margin: 1rem 0;
  height: 3rem;
}

#upload-img-label {
  display: inline-block;
  border: 1px solid lightgray;
  border-radius: 0.3rem;
  padding: 0.3rem 0.8rem;
  cursor: pointer;
}

#upload-img-label:hover {
  border-color: #4096ff;
  color: #4096ff;
}

.image-name {
  margin: 0.5rem;
  text-align: center;
}
</style>
