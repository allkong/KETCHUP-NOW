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
      return _.includes(keys, 'newPassword') && _.includes(keys, 'newPasswordCheck')
    },
  },
})

const emits = defineEmits(['passwordFormChange'])

const passwordForm = ref(props.passwordForm)

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
</script>

<template>
  <a-row>
    <a-col :span="24">
      <a-input
        type="password"
        v-model:value="passwordForm.newPassword"
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
        v-model:value="passwordForm.newPasswordCheck"
        placeholder="비밀번호를 다시 입력해 주세요."
        @keyup="$emit('passwordFormChange', passwordForm, passwordVerificationStatus)"
      >
      </a-input>
    </a-col>
  </a-row>
</template>

<style scoped></style>
