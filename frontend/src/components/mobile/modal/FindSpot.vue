<script setup>
import { ref } from 'vue'

const props = defineProps({
  modalOpen: Boolean,
})

const isOpen = ref(props.modalOpen)

const handleOk = (e) => {
  console.log(e)
}
</script>

<template>
  <div>
    <a-modal v-model:open="isOpen" width="70%" centered>
      <a-row align="middle" justify="center">
        <a-col :span="4">
          <img src="@/assets/icon/flag.png" class="flag-icon" />
        </a-col>
        <a-col :span="10">
          <h2>스팟 발견</h2>
        </a-col>
      </a-row>
      <a-row justify="center">
        <a-col>
          <p>미션을 수행하시겠습니까?</p>
        </a-col>
      </a-row>
      <template #footer>
        <a-row justify="center">
          <a-button key="submit" type="primary" @click="handleOk" class="modal-button" danger
            >네!</a-button
          >
          <a-button key="back" @click="$emit('closeModal')" class="modal-button">아니요</a-button>
        </a-row>
      </template>
    </a-modal>
  </div>
</template>

<style scoped>
h2 {
  margin: 0;
}

.flag-icon {
  width: 2rem;
  height: 2rem;
}

.modal-button {
  width: 4rem;
  display: flex;
  justify-content: center;
}
</style>
