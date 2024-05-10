<script setup>
import _ from 'lodash'
import { ref, computed, defineProps, defineEmits } from 'vue'
import { PASSWORD_VERIFICATION_STATUS } from './variables'

const props = defineProps({
  passwordForm: {
    type: Object,
    required: true,
    validator: (value) => {
      const keys = Object.keys(value)
      return _.includes(keys, 'password') && _.includes(keys, 'passwordCheck')
    },
  },
})

const emits = defineEmits(['passwordFormChange'])

const passwordForm = ref(props.passwordForm)

const passwordVerificationStatus = computed(() => {
  // 아직 입력하지 않은 경우
  if (_.isEmpty(passwordForm.value.password)) {
    return PASSWORD_VERIFICATION_STATUS.NOT_YET
  }
  // 입력은 했으나, 확인란과 다른 경우
  else if (passwordForm.value.password !== passwordForm.value.passwordCheck) {
    return PASSWORD_VERIFICATION_STATUS.DIFFERENT
  }
  // 너무 짧은 경우
  else if (passwordForm.value.password.length < 10) {
    return PASSWORD_VERIFICATION_STATUS.TOO_SHORT
  }

  // 이외의 경우 정상
  return PASSWORD_VERIFICATION_STATUS.VERIFIED
})
</script>

<template>
  <a-row id="top-row">
    <a-col :span="24">
      <a-input
        type="password"
        v-model:value="passwordForm.password"
        placeholder="비밀번호를 입력해 주세요."
        @keyup="$emit('passwordFormChange', passwordForm, passwordVerificationStatus)"
      >
      </a-input>
    </a-col>
  </a-row>
  <a-row>
    <a-col :span="24">
      <a-input
        type="password"
        v-model:value="passwordForm.passwordCheck"
        placeholder="비밀번호를 다시 입력해 주세요."
        @keyup="$emit('passwordFormChange', passwordForm, passwordVerificationStatus)"
      >
      </a-input>
    </a-col>
  </a-row>
</template>

<style scoped>
#top-row {
  margin-bottom: 0.2rem;
}
</style>
