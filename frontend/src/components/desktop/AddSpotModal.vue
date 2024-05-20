<script setup>
import { ref } from 'vue'

const props = defineProps({
  modalOpen: Boolean,
  marker: Object,
})
console.log(props.marker)
const isOpen = ref(props.modalOpen)

const onAddSpot = (e) => {
  console.log(e)
}
</script>

<template>
  <div>
    <a-modal v-model:open="isOpen" centered @cancel="$emit('closeAddSpotModal')" @ok="onAddSpot">
      <a-row align="middle" justify="center">
        <a-col :span="4">
          <img src="@/assets/icon/flag.png" class="flag-icon" />
        </a-col>
        <a-col :span="10">
          <h2>스팟 등록</h2>
        </a-col>
      </a-row>
      <a-row justify="center">
        <a-col>
          <p>스팟 등록 폼</p>
        </a-col>
      </a-row>
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
