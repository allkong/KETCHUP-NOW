<script setup>
import router from '@/router'

const goToHome = () => {
  router.push({ name: 'home' })
}
</script>

<template>
  <a-row align="middle" justify="center" style="height: 100vh">
    <a-col>
      <a-result status="404" title="404" sub-title="찾을 수 없습니다.">
        <template #extra>
          <a-button type="primary" @click="goToHome">홈으로 가기</a-button>
        </template>
      </a-result>
    </a-col>
  </a-row>
</template>

<style scoped></style>
