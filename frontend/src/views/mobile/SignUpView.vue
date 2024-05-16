<script setup>
import _ from 'lodash'
import { computed, inject, ref } from 'vue'
import { useRouter } from 'vue-router'
import TextLogo from '@/components/mobile/TextLogo.vue'
import PasswordCheckingInput from '@/components/mobile/auth/PasswordCheckingInput.vue'
import { PASSWORD_VERIFICATION_STATUS } from '@/components/mobile/auth/variables'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user-store'

const axios = inject('axios')

const router = useRouter()
const userStore = useUserStore()

const signUpForm = ref({
  loginId: '',
  nickname: '',
})

const loginValidationMessage = ref('')
const isLoginIdValidated = computed(() => {
  const { loginId } = signUpForm.value
  if (loginId.length < 5) {
    loginValidationMessage.value = '너무 짧아요! (5글자 이상)'
    return false
  }

  return true
})

const nicknameValidationMessage = ref('')
const isNicknameValidated = computed(() => {
  const { nickname } = signUpForm.value
  if (nickname.length < 3) {
    nicknameValidationMessage.value = '너무 짧아요! (3글자 이상)'
    return false
  }

  return true
})

const passwordForm = ref({
  password: '',
  passwordCheck: '',
})

const passwordVerificationStatus = ref(PASSWORD_VERIFICATION_STATUS.NOT_YET)
function onPasswordFormChange(updatedPasswordForm, updatedPasswordVerificationStatus) {
  passwordForm.value = updatedPasswordForm
  passwordVerificationStatus.value = updatedPasswordVerificationStatus
}

const isDuplicatedLoginId = ref(false)
const isDuplicatedNickname = ref(false)
async function checkFieldDuplication(key, value) {
  await axios
    .get('/users/duplication-check', {
      params: {
        key,
        value,
      },
    })
    .then((resp) => {
      if (key === 'loginId') {
        isDuplicatedLoginId.value = resp.data.isDuplicated
      } else if (key === 'nickname') {
        isDuplicatedNickname.value = resp.data.isDuplicated
      }
    })
}

const isProcessingSignUp = ref(false)
const signUpBtn = ref(null)

async function doSignUp() {
  if (
    isLoginIdValidated.value &&
    passwordVerificationStatus.value !== PASSWORD_VERIFICATION_STATUS.VERIFIED &&
    isNicknameValidated.value
  ) {
    message.error('입력을 확인해주세요.')
    return
  }

  await checkFieldDuplication('loginId', signUpForm.value.loginId)
  await checkFieldDuplication('nickname', signUpForm.value.nickname)

  if (isDuplicatedLoginId.value) {
    message.error('이미 사용중인 ID입니다 ㅠㅠ')
    return
  } else if (isDuplicatedNickname.value) {
    message.error('이미 사용중인 닉네임입니다 ㅠㅠ')
    return
  }

  isProcessingSignUp.value = true
  userStore
    .signUp({
      password: passwordForm.value.password,
      ...signUpForm.value,
    })
    .then(() => {
      message.success('회원 가입 완료!')
      router.push({ name: 'auth:login' })
    })
    .catch((error) => {
      message.error('회원 가입에 실패했어요 ㅠㅠ')
    })
    .finally(() => {
      isProcessingSignUp.value = false
    })
}
</script>

<template>
  <a-layout-content>
    <TextLogo />
    <a-form :label-col="{ col: 8 }" @submit.prevent="doSignUp" autocomplete="off">
      <h1>Sign Up</h1>
      <a-form-item label="ID" :rules="[{ required: true, message: 'ID를 입력해 주세요.' }]">
        <a-input
          v-model:value="signUpForm.loginId"
          @keyup="() => checkFieldDuplication('loginId', signUpForm.loginId)"
        />
        <div class="verification-failed-msg" v-show="!isLoginIdValidated">
          {{ loginValidationMessage }}
        </div>
        <div class="verification-failed-msg" v-show="isDuplicatedLoginId">
          이미 사용 중인 ID입니다.
        </div>
      </a-form-item>
      <a-form-item label="PW" :rules="[{ required: true, message: '비밀번호를 입력해 주세요.' }]">
        <PasswordCheckingInput
          :password-form="passwordForm"
          @password-form-change="onPasswordFormChange"
        />
        <span class="verification-failed-msg">
          <div v-if="passwordVerificationStatus === PASSWORD_VERIFICATION_STATUS.DIFFERENT">
            비밀번호가 확인란과 달라요.
          </div>
          <div></div>
        </span>
      </a-form-item>
      <a-form-item
        label="Nickname"
        :rules="[{ required: true, message: '닉네임을 입력해 주세요.' }]"
      >
        <a-input
          v-model:value="signUpForm.nickname"
          @keyup="() => checkFieldDuplication('nickname', signUpForm.nickname)"
        />
        <div class="verification-failed-msg" v-show="!isNicknameValidated">
          {{ nicknameValidationMessage }}
        </div>
        <div class="verification-failed-msg" v-show="isDuplicatedNickname">
          이미 사용 중인 닉네임입니다.
        </div>
      </a-form-item>
      <a-form-item>
        <a-button
          id="sign-up-btn"
          ref="signUpBtn"
          html-type="submit"
          shape="round"
          :disabled="isProcessingSignUp"
        >
          <span v-show="!isProcessingSignUp">Let's Ketchup!</span>
          <img v-show="isProcessingSignUp" id="ketchup-img" src="@/assets/logo.png" alt="" />
        </a-button>
      </a-form-item>
    </a-form>
  </a-layout-content>
</template>

<style scoped lang="scss">
.ant-layout-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: $whiteBackgroundColor;
  height: 100vh;
  padding: 1rem;
}
#back-icon-container {
  text-align: left;
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
  padding: 2rem;
  padding-top: 0.5rem;
  background: $whiteBackgroundColor;
  border: 1px solid silver;
  border-radius: 2%;
}
.ant-form-item {
  margin-bottom: 1rem;
}
.verification-failed-msg {
  color: silver;
}
#sign-up-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 0.5rem;
  width: 100%;
  height: 2.5rem;
  font-weight: bold;
  background-color: tomato;
  border: tomato;
  color: white;
}
#sign-up-btn > img {
  height: 2rem;
}
</style>
