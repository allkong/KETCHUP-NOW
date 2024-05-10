<script setup>
import { ref } from 'vue'
import MapComponent from '@/components/mobile/MapComponent.vue'
const props = defineProps({
  modalOpen: Boolean,
})

const isOpen = ref(props.modalOpen)
const activeKey = ref('1')

const handleOk = (e) => {
  console.log(e)
}
</script>

<template>
  <div>
    <a-modal
      v-model:open="isOpen"
      title="Story"
      centered
      width="25rem"
      @cancel="$emit('closeModal')"
      @ok="handleOk"
    >
      <div class="thumnail"></div>
      <h2>싸피산책로</h2>
      <span>서울시 강남구</span>

      <a-tabs v-model:activeKey="activeKey" class="tabs-container">
        <a-tab-pane key="1" tab="설명">
          <p>김싸피</p>
          <div class="tab-textarea">
            이 스토리는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고
            지금은 당신에게로 옮겨진 이 스토리는 4일 안에 당신 곁을 떠나야 합니다. 이 스토리를
            포함해서 7통을 행운이 필요한 사람에게 보내 주셔야 합니다. 복사를 해도 좋습니다. 혹
            미신이라 하실지 모르지만 사실입니다.
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="지도">
          <div class="tab-map">
            <MapComponent />
          </div>
        </a-tab-pane>
        <a-tab-pane key="3" tab="리뷰">Content of Tab Pane 3</a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<style scoped>
h2 {
  margin-bottom: 0.5rem;
}

.thumnail {
  background-color: #d9d9d9;
  height: 12rem;
  border-radius: 0.5rem;
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
</style>
