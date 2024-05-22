<script setup>
import { ref, inject, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import { UploadOutlined, PaperClipOutlined } from '@ant-design/icons-vue'

const route = useRoute()
const axios = inject('axios')

const emit = defineEmits(['closeAddSpotModal', 'updateSpots'])
const props = defineProps({
  modalOpen: Boolean,
  place: Object,
  lastSpotUuid: String,
})

const ALLOWED_FILE_TYPES = ['jpeg', 'png', 'gif', 'jpg']
const isOpen = ref(props.modalOpen)
const spot = ref({
  imageFile: {},
  createDto: {
    previousSpotUuid: props.lastSpotUuid,
    latitude: 0,
    longitude: 0,
    title: '',
    description: '',
  },
})

const onSavePlaceToSpot = (place) => {
  if (place.placeType === 'keyword') {
    spot.value.createDto.latitude = place.y
    spot.value.createDto.longitude = place.x
    spot.value.createDto.title = place.place_name
  } else if (place.placeType === 'attraction') {
    spot.value.createDto.latitude = place.latitude
    spot.value.createDto.longitude = place.longitude
    spot.value.createDto.title = place.title
  }
}

watchEffect(() => {
  if (isOpen.value) {
    onSavePlaceToSpot(props.place)
  }
})

const onFileChange = (e) => {
  // 파일이 선택되지 않은 상태로 파일 선택 창이 닫히면 입력 비우기
  if (!e.target.files.length) {
    spot.value.imageFile = {}
    return
  }

  // 파일 확장자로 이미지인지 확인
  const uploadFile = e.target.files[0]
  const ext = uploadFile.name.split('.').pop()
  if (!ALLOWED_FILE_TYPES.find((allowedExt) => allowedExt === ext)) {
    message.error('이미지 파일만 가능합니다.')
    spot.value.imageFile = {}
    return
  }

  spot.value.imageFile = uploadFile
}

const onAddSpot = () => {
  const formData = new FormData()
  formData.append('imageFile', spot.value.imageFile)
  formData.append(
    'createDto',
    new Blob([JSON.stringify(spot.value.createDto)], { type: 'application/json' }),
  )
  console.log(spot.value.createDto)
  axios
    .post(`/stories/${route.params.uuid}/spots`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    .then((response) => emit('updateSpots'))
    .catch((error) => console.error(error))
}
</script>

<template>
  <a-modal
    v-model:open="isOpen"
    width="25rem"
    centered
    cancelText="취소"
    okText="등록"
    @cancel="$emit('closeAddSpotModal')"
    @ok="onAddSpot"
  >
    <a-row align="middle" justify="center">
      <a-col>
        <img src="@/assets/icon/flag.png" class="flag-icon" />
      </a-col>
      <a-col>
        <h2>스팟 등록</h2>
      </a-col>
    </a-row>
    <div class="input-container">
      <a-row justify="center" class="input-form">
        <a-col class="input-label">이름</a-col>
        <a-col>
          <a-input v-model:value="spot.createDto.title" class="input-box" />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">설명</a-col>
        <a-col>
          <a-textarea v-model:value="spot.createDto.description" class="input-box" />
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
          <div v-if="spot.imageFile.name" class="image-name">
            <PaperClipOutlined />{{ spot.imageFile.name }}
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

.flag-icon {
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
