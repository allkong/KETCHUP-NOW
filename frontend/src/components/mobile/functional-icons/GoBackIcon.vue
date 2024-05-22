<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()
function goBack() {
  router.back()
}
</script>

<template>
  <ArrowLeftOutlined id="go-back-icon" @click="goBack" />
</template>

<style scoped>
#go-back-icon {
  margin-top: 0.5rem;
  margin-left: 0.5rem;
  font-size: large;
  z-index: 10;
}
</style>
