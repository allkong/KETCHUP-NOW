<script setup>
import _ from 'lodash'
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'

const loginForm = ref({
  loginId: '',
  password: '',
})

const isProcessingLogin = ref(false)

function doLogin() {
  const { loginId, password } = loginForm.value
  if (_.isEmpty(loginId) || _.isEmpty(password)) {
    message.error('ID와 비밀번호 모두 입력해 주세요!')
    return
  }

  isProcessingLogin.value = true
  setTimeout(() => {
    // 로그인 처리
    isProcessingLogin.value = false
    message.success('로그인 성공!')
  }, 1000)
}
</script>

<template>
  <a-layout-content>
    <GoBackIcon />
    <a-form :label-col="{ col: 8 }" @submit.prevent="doLogin">
      <div id="logo-container">
        <img id="logo-img" src="@/assets/logo.png" />
      </div>
      <a-form-item label="ID" :rules="[{ required: true, message: 'ID를 입력해 주세요.' }]">
        <a-input v-model:value="loginForm.loginId" />
      </a-form-item>
      <a-form-item label="PW" :rules="[{ required: true, message: '비밀번호를 입력해 주세요.' }]">
        <a-input-password v-model:value="loginForm.password" />
      </a-form-item>
      <a-form-item>
        <a-button id="login-btn" html-type="submit" shape="round" :disabled="isProcessingLogin"
          >Login</a-button
        >
      </a-form-item>
      <div id="register-link-container">
        <RouterLink>Or Register now!</RouterLink>
      </div>
    </a-form>
  </a-layout-content>
</template>

<style scoped lang="scss">
.ant-layout-content {
  padding: 1rem;
  min-height: 100vh;
}
#logo-container {
  text-align: center;
}
#logo-img {
  width: 30%;
}
h1 {
  text-align: center;
}
.ant-form {
  margin: 50% 1rem;
  padding: 2rem;
  padding-top: 0.5rem;
  background: $whiteBackgroundColor;
  border: 1px solid silver;
  border-radius: 2%;
}
.ant-form-item {
  margin-bottom: 1rem;
}
#login-btn {
  margin-top: 0.5rem;
  width: 100%;
  height: 2.5rem;
}
#register-link-container {
  text-align: center;
}
#register-link-container > a {
  color: silver;
  text-decoration: none;
}
</style>
