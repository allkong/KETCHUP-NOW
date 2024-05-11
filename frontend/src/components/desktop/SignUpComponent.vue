<script setup>
import { reactive, ref } from 'vue'
const formRef = ref()
const formState = reactive({
  loginId: '',
  password: '',
  checkPass: '',
  nickname: '',
})

const validatePass = async (_rule, value) => {
  if (value === '') {
    return Promise.reject('비밀번호를 입력하세요')
  } else {
    if (formState.checkPass !== '') {
      formRef.value.validateFields('checkPass')
    }
    return Promise.resolve()
  }
}
const validatePass2 = async (_rule, value) => {
  if (value === '') {
    return Promise.reject('비밀번호 확인을 진행하세요')
  } else if (value !== formState.password) {
    return Promise.reject('비밀번호가 일치하지 않습니다')
  } else {
    return Promise.resolve()
  }
}
const rules = {
  loginId: [
    {
      required: true,
      message: 'ID를 입력하세요',
    },
  ],
  nickname: [
    {
      required: true,
      message: '닉네임을 입력하세요',
    },
  ],
  password: [
    {
      required: true,
      validator: validatePass,
      trigger: 'change',
    },
  ],
  checkPass: [
    {
      validator: validatePass2,
      trigger: 'change',
    },
  ],
}
const layout = {
  labelCol: {
    span: 4,
  },
  wrapperCol: {
    span: 14,
  },
}
const handleFinish = (values) => {
  console.log(values, formState)
}
const handleFinishFailed = (errors) => {
  console.log(errors)
}
const resetForm = () => {
  formRef.value.resetFields()
}
const handleValidate = (...args) => {
  console.log(args)
}
</script>

<template>
  <a-form
    ref="formRef"
    name="custom-validation"
    :model="formState"
    :rules="rules"
    v-bind="layout"
    @finish="handleFinish"
    @validate="handleValidate"
    @finishFailed="handleFinishFailed"
  >
    <a-form-item label="ID" name="loginId">
      <a-input v-model:value="formState.loginId" type="text" />
    </a-form-item>
    <a-form-item label="nickname" name="nickname">
      <a-input v-model:value="formState.nickname" type="text" />
    </a-form-item>
    <a-form-item has-feedback label="Password" name="password">
      <a-input
        v-model:value="formState.password"
        type="password"
        autocomplete="off"
      />
    </a-form-item>
    <a-form-item has-feedback label="Confirm" name="checkPass">
      <a-input
        v-model:value="formState.checkPass"
        type="password"
        autocomplete="off"
      />
    </a-form-item>
    <a-form-item :wrapper-col="{ span: 14, offset: 4 }">
      <a-button type="primary" danger html-type="submit">Submit</a-button>
      <a-button style="margin-left: 10px" @click="resetForm" danger
        >Reset</a-button
      >
    </a-form-item>
  </a-form>
</template>

<style scoped></style>
