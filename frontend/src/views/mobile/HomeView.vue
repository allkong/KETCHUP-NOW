<script setup>
import { GithubOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const goToSignUp = () => {
  router.push({ name: 'auth:sign-up' })
}

const goToLogin = () => {
  router.push({ name: 'auth:login' })
}
</script>

<template>
  <div class="image">
    <div class="button-container">
      <a-button type="primary" size="large" danger @click="goToSignUp" style="margin-bottom: 9rem"
        >바로 시작하기</a-button
      >
    </div>
  </div>
  <div class="end">
    <img class="logo" src="@/assets/logo.png" alt="" />
    <div>
      <a-button type="primary" danger @click="goToSignUp">회원가입</a-button>
      <a-button danger @click="goToLogin" style="margin-left: 1rem">로그인</a-button>
    </div>
  </div>
  <a-layout-footer>
    <div>
      Developed by
      <a href="https://github.com/allkong">Dabin Jeong <GithubOutlined /></a>
      &
      <a href="https://github.com/pcjs156">Yubin Park <GithubOutlined /></a>
    </div>
    <div>Copyright ©SSAFY All Rights Reserved.</div>
  </a-layout-footer>
</template>

<style scoped>
.image {
  width: 100vw;
  height: 380vh;
  background-image: url('@/assets/home/mobile/home.png');
  background-size: cover;
}

.button-container {
  height: 100vh;
  display: flex;
  align-items: end;
  justify-content: center;
}

.end {
  width: 100vw;
  height: 35vh;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  background-color: #ffe2db;
}

.logo {
  width: 9rem;
  margin-bottom: 1rem;
}

.ant-layout-footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: rgb(105, 105, 105);
}

.ant-layout-footer a {
  color: rgb(105, 105, 105);
}
</style>
