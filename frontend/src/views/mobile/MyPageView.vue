<script setup>
import _ from 'lodash'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { CopyrightCircleOutlined, GithubOutlined } from '@ant-design/icons-vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import GoBackIcon from '@/components/mobile/functional-icons/GoBackIcon.vue'

const router = useRouter()

const passwordForm = ref({
  newPassword: '',
  newPasswordCheck: '',
})

const isPasswordUpdating = ref(false)

const PASSWORD_VERIFICATION_STATUS = {
  NOT_YET: 'NOT_YET',
  DIFFERENT: 'DIFFERENT',
  TOO_SHORT: 'TOO_SHORT',
}
Object.freeze(PASSWORD_VERIFICATION_STATUS)

const passwordVerificationStatus = computed(() => {
  // 아직 입력하지 않은 경우
  if (_.isEmpty(passwordForm.value.newPassword)) {
    return PASSWORD_VERIFICATION_STATUS.NOT_YET
  }
  // 입력은 했으나, 확인란과 다른 경우
  else if (passwordForm.value.newPassword !== passwordForm.value.newPasswordCheck) {
    return PASSWORD_VERIFICATION_STATUS.DIFFERENT
  }
  // 너무 짧은 경우
  else if (passwordForm.value.newPassword.length < 10) {
    return PASSWORD_VERIFICATION_STATUS.TOO_SHORT
  }
})

const passwordChangeBtnClass = computed(() => {
  switch (passwordVerificationStatus.value) {
    case PASSWORD_VERIFICATION_STATUS.NOT_YET:
      return 'disabled-password-change-btn'
    case PASSWORD_VERIFICATION_STATUS.DIFFERENT:
    case PASSWORD_VERIFICATION_STATUS.TOO_SHORT:
      return 'new-password-validation-failed-btn'
    // 검증 성공
    default:
      return 'new-password-validation-success-btn'
  }
})

function doPasswordChange() {
  switch (passwordVerificationStatus.value) {
    case PASSWORD_VERIFICATION_STATUS.NOT_YET:
      message.warning('새 비밀번호를 입력해 주세요.')
      return
    case PASSWORD_VERIFICATION_STATUS.DIFFERENT:
      message.error('비밀번호가 일치하지 않습니다.')
      return
    case PASSWORD_VERIFICATION_STATUS.TOO_SHORT:
      message.error('비밀번호는 10글자 이상 입력해 주세요.')
      return
  }
  // 검증 성공
  isPasswordUpdating.value = true
  message.success('수정중...')
  setTimeout(() => {
    // TODO : 수정 실패 시나리오도 필요함
    isPasswordUpdating.value = false
    passwordForm.value.newPassword = ''
    passwordForm.value.newPasswordCheck = ''

    message.success('수정 완료!')
  }, 3000)
}
</script>

<template>
  <a-layout-content id="content-container">
    <GoBackIcon />
    <div id="greeting-container">
      <img id="logo-img" src="@/assets/logo.png" alt="logo-img" />
      <h1>여행자 <span id="heading-nickname">팔척장신</span>님!</h1>
      <img id="logo-img" class="flip-horizontally" src="@/assets/logo.png" alt="logo-img" />
    </div>
    <a-card title="회원 정보">
      <a-layout-content id="auth-info-container">
        <a-row>
          <a-col :span="5" class="input-label">ID</a-col>
          <a-col :span="19">
            <a-input value="pcjs156" readonly />
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="5" class="input-label">닉네임</a-col>
          <a-col :span="19">
            <a-input value="팔척장신" readonly />
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="5" class="input-label">가입일</a-col>
          <a-col :span="19">
            <a-input value="2024-05-10" readonly />
          </a-col>
        </a-row>
        <a-collapse :bordered="false">
          <a-collapse-panel header="비밀번호 수정">
            <a-row>
              <a-col :span="24">
                <a-input
                  type="password"
                  v-model:value="passwordForm.newPassword"
                  placeholder="새 비밀번호를 입력해 주세요."
                >
                </a-input>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="24">
                <a-input
                  type="password"
                  v-model:value="passwordForm.newPasswordCheck"
                  placeholder="새 비밀번호를 다시 입력해 주세요."
                >
                </a-input>
              </a-col>
            </a-row>
            <a-button
              :class="passwordChangeBtnClass"
              @:click="doPasswordChange"
              :disabled="isPasswordUpdating"
              >수정</a-button
            >
          </a-collapse-panel>
        </a-collapse>
      </a-layout-content>
    </a-card>
    <a-card title="기타 정보">
      <a-button id="my-review-page-btn" type="primary">작성한 리뷰 관리</a-button>
    </a-card>
    <a-divider />
    <div id="project-info-container">
      <div>
        Developed by Dabin Jeong <a href="https://github.com/allkong"><GithubOutlined /></a> & Yubin
        Park <a href="https://github.com/pcjs156"><GithubOutlined /></a>
      </div>
      <div>Copyright ©SAMSUNG All Rights Reserved.</div>
    </div>
  </a-layout-content>
  <NavigationView />
</template>

<style scoped lang="scss">
#content-container {
  background: $whiteBackgroundColor;
  padding: 1rem;
}
#logo-img {
  width: 2rem;
  height: 2rem;
  margin: 0.5rem;
}
.flip-horizontally {
  transform: scaleX(-1);
}
#greeting-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 0.5rem;
}
#heading-nickname {
  color: tomato;
}
.ant-divider {
  margin-top: 0;
  margin-bottom: 0;
}
.ant-card {
  margin: 1rem;
  margin-top: 0;
}
.ant-card-body {
  padding: 0 1rem;
}
.input-label {
  display: flex;
  align-items: center;
  justify-content: end;
  padding-right: 1rem;
}
#auth-info-container > .ant-row {
  margin: 1rem 0;
}
.ant-collapse {
  background-color: $whiteBackgroundColor;
}
.ant-collapse .ant-input {
  margin-bottom: 0.5rem;
}
.ant-collapse-header {
  padding: 0 auto;
  background-color: red;
}
.ant-collapse-header-text {
  text-align: left;
}
.ant-btn:hover {
  border-color: white;
}
.ant-collapse .ant-btn {
  width: 100%;
  height: 2rem;
}
.disabled-password-change-btn,
.disabled-password-change-btn:hover {
  color: silver;
  border-color: silver;
}
.new-password-validation-failed-btn,
.new-password-validation-failed-btn:hover {
  color: tomato;
  border-color: tomato;
}
.new-password-validation-success-btn,
.new-password-validation-success-btn:hover {
  color: green;
  border-color: green;
}
#my-review-page-btn {
  width: 90%;
  height: 3rem;
  margin: 0 5%;
  background-color: tomato;
}
#project-info-container {
  margin: 1rem;
  text-align: center;
  color: silver;
}
</style>
