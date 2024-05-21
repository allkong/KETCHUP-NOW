<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user-store'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const userStore = useUserStore()
const router = useRouter()

const loginForm = ref({
  loginId: '',
  password: '',
})

const isProcessingLogin = ref(false)

function doLogin() {
  isProcessingLogin.value = true
  userStore
    .login(loginForm.value)
    .then(() => {
      message.success('로그인 성공!')
      router.push({ name: 'my-stories' })
    })
    .catch((error) => {
      console.log(error)
      message.error('ID와 비밀번호를 다시 확인해 주세요.')
    })
    .finally(() => {
      isProcessingLogin.value = false
    })
}

const onFailed = (errorInfo) => {
  console.log('Failed:', errorInfo)
}

// 로그인 버튼 활성화
const disabled = computed(() => {
  return !(loginForm.value.loginId && loginForm.value.password)
})
</script>

<template>
  <div id="box">
    <div id="login-box">
      <div id="login-box-left-side">
        <img src="@/assets/logo.png" style="margin-top: 1.5rem" alt="" />
        <div class="catchphrase" style="margin-top: 0.5rem">여행에 이야기를 더하다,</div>
        <div class="catchphrase">Catch-Up, <span style="color: tomato">Now!</span></div>
      </div>
      <div id="login-box-right-side">
        <h1 style="text-align: center">Login</h1>
        <a-form
          :model="loginForm"
          name="normal_login"
          class="login-form"
          @finish="doLogin"
          @finishFailed="onFailed"
        >
          <a-form-item
            label="ID"
            name="loginId"
            :rules="[{ required: true, message: 'ID를 입력해주세요!' }]"
          >
            <a-input v-model:value="loginForm.loginId">
              <template #prefix>
                <UserOutlined class="site-form-item-icon" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            label="PW"
            name="password"
            :rules="[{ required: true, message: '비밀번호를 입력해주세요!' }]"
          >
            <a-input-password v-model:value="loginForm.password">
              <template #prefix>
                <LockOutlined class="site-form-item-icon" />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              id="login-button"
              :disabled="disabled"
              type="primary"
              html-type="submit"
              class="login-form-button"
              style="width: 100%"
            >
              로그인
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
    <a-divider>
      <RouterLink :to="{ name: 'auth:sign-up' }">
        <span id="join-us-text">🍅 Or Join Us! 🍅</span>
      </RouterLink>
    </a-divider>
  </div>
</template>

<style scoped lang="scss">
#box {
  display: flex;
  flex-direction: column;
  justify-content: center;

  border: 1px solid silver;
  padding: 2% 3%;
  margin: 5% auto;
  height: 45vh;
  width: 75vh;
}

#login-box {
  display: flex;
  justify-content: space-evenly;
}

.catchphrase,
.catchphrase * {
  font-family: 'KCC-Hanbit' !important;
}

h1 {
  font-family: 'Bagel Fat One' !important;
  color: tomato;
  font-size: 3rem;
  margin-top: -1rem;
  margin-bottom: 1rem;
}

#login-box-left-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

#login-box-left-side > img {
  height: 10rem;
}

#login-box-right-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

#login-button {
  background-color: tomato;
}

#sign-up-box {
  padding: 0 11%;
}

#join-us-text {
  color: tomato;
}
</style>
