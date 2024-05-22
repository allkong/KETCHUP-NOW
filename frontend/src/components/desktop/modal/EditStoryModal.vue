<script setup>
import { ref, inject, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const axios = inject('axios')

const emit = defineEmits(['closeEditStoryModal', 'updateStory'])
const props = defineProps({
  modalOpen: Boolean,
  story: Object,
})

const isOpen = ref(props.modalOpen)
const editedStory = ref({
  status: '',
  title: '',
  description: '',
  sido: '',
  gungu: '',
})

const onSaveStorytoEditedStory = (story) => {
  editedStory.value.status = story.status
  editedStory.value.title = story.title
  editedStory.value.description = story.description
  editedStory.value.sido = story.sido
  editedStory.value.gungu = story.gungu
}

watchEffect(() => {
  if (isOpen.value) {
    onSaveStorytoEditedStory(props.story)
  }
})

const onEditStory = () => {
  console.log(editedStory.value)

  axios
    .put(`/stories/${route.params.uuid}`, editedStory.value)
    .then((response) => emit('updateStory'))
    .catch((error) => console.error(error))
}
</script>

<template>
  <a-modal
    v-model:open="isOpen"
    width="25rem"
    centered
    cancelText="취소"
    okText="변경"
    @cancel="$emit('closeEditStoryModal')"
    @ok="onEditStory"
  >
    <a-row align="middle" justify="center">
      <a-col>
        <img src="@/assets/icon/open-book.png" class="book-icon" />
      </a-col>
      <a-col>
        <h2>스토리 수정</h2>
      </a-col>
    </a-row>
    <div class="input-container">
      <a-row justify="center" class="input-form">
        <a-col class="input-label">이름</a-col>
        <a-col>
          <a-input v-model:value="editedStory.title" class="input-box" />
        </a-col>
      </a-row>
      <a-row justify="center" class="input-form">
        <a-col class="input-label">설명</a-col>
        <a-col>
          <a-textarea v-model:value="editedStory.description" class="input-box" />
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
</style>
