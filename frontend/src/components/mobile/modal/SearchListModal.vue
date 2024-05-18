<script setup>
import { ref } from 'vue'
import { StarFilled } from '@ant-design/icons-vue'

const props = defineProps({
  modalOpen: Boolean,
})

const isOpen = ref(props.modalOpen)
</script>

<!-- 디자인 수정 필요 -->
<template>
  <div>
    <a-modal
      v-model:open="isOpen"
      width="100%"
      cancelText="닫기"
      @cancel="$emit('closeSearchListModal')"
      centered
    >
      <h2>스토리 목록</h2>
      <div style="overflow: auto; height: 70vh">
        <a-card>
          <a-card-grid style="width: 100%; height: 9rem">
            <a-row>
              <a-col :span="10">
                <div class="thumnail"></div>
              </a-col>
              <a-col :span="14">
                <p>스토리제목</p>
                <a-rate :value="2" disabled class="star-grade" />
                <p>서울시 강남구</p>
                <p>creator</p>
              </a-col>
            </a-row>
          </a-card-grid>
        </a-card>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
p {
  margin: 0;
}

.thumnail {
  background-color: lightgray;
  height: 6rem;
  width: 6rem;
  border-radius: 0.5rem;
}

.star-grade {
  font-size: 0.9rem;
}

::v-deep .ant-rate .ant-rate-star:not(:last-child) {
  margin-inline-end: 0.2rem;
}
</style>
