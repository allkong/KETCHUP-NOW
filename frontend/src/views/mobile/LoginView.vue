<script setup>
import _ from 'lodash'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import TextLogo from '@/components/mobile/TextLogo.vue'
import { useUserStore } from '@/stores/user-store'

const router = useRouter()
const userStore = useUserStore()

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
  userStore
    .login(loginForm.value)
    .then(() => {
      message.success('로그인 성공!')
      router.push({ name: 'search' })
    })
    .catch((error) => {
      message.error('ID와 비밀번호를 다시 확인해 주세요.')
    })
    .finally(() => {
      isProcessingLogin.value = false
    })
}
</script>

<template>
  <a-layout-content>
    <a-form :label-col="{ col: 8 }" @submit.prevent="doLogin">
      <div id="logo-container">
        <img id="logo-img" src="@/assets/logo.png" />
        <TextLogo align="left" />
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
        <RouterLink :to="{ name: 'auth:sign-up' }">Or Register now!</RouterLink>
      </div>
    </a-form>
  </a-layout-content>
</template>

<style scoped lang="scss">
.ant-layout-content {
  padding: 1rem;
  background-color: $whiteBackgroundColor;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
#logo-container {
  display: flex;
  justify-content: center;
  text-align: center;
  margin-top: 1rem;
  margin-bottom: 1rem;
}
#logo-img {
  width: 30%;
}
h1 {
  text-align: center;
}
.ant-form {
  margin: 0% 1rem;
  padding: 2rem;
  padding-top: 0.5rem;
  background: $whiteBackgroundColor;
  border: 1px solid silver;
  border-radius: 2%;
  text-align: center;
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
