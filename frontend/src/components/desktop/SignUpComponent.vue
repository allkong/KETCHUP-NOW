<script setup>
import { message } from 'ant-design-vue'
import { inject, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const axios = inject('axios')
const router = useRouter()

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

async function isDuplicatedField(key, value) {
  return (
    await axios.get('/users/duplication-check', {
      params: {
        key,
        value,
      },
    })
  ).data.isDuplicated
}

const rules = {
  loginId: [
    {
      required: true,
      validator: async (_rule, value) => {
        if (value.length < 5) {
          return Promise.reject('5글자 이상 입력해 주세요!')
        }

        const isDuplicated = await isDuplicatedField('loginId', value)
        if (isDuplicated) {
          return Promise.reject('이미 사용 중인 ID에요.')
        }

        return Promise.resolve()
      },
    },
  ],
  nickname: [
    {
      required: true,
      validator: async (_rule, value) => {
        if (value.length < 3) {
          return Promise.reject('3글자 이상 입력해 주세요!')
        }

        const isDuplicated = await isDuplicatedField('nickname', value)
        if (isDuplicated) {
          return Promise.reject('이미 사용 중인 닉네임이에요.')
        }

        return Promise.resolve()
      },
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
    span: 20,
  },
}
const handleFinish = async (values) => {
  const isDuplicatedLoginId = await isDuplicatedField('loginId', values.loginId)
  if (isDuplicatedLoginId) {
    message.error('이미 사용 중인 ID에요.')
    return
  }

  const isDuplicatedNickname = await isDuplicatedField('nickname', values.nickname)
  if (isDuplicatedNickname) {
    message.error('이미 사용 중인 닉네임이에요.')
    return
  }

  axios
    .post('/users/sign-up', {
      loginId: values.loginId,
      nickname: values.nickname,
      password: values.password,
    })
    .then((resp) => {
      message.success('회원 가입 성공!')
      router.push({ name: 'auth:login' })
    })
    .errors((error) => {
      console.log(error)
      message.error('회원 가입 실패 😥')
    })
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
  <div id="box">
    <h1>
      <img id="logo-img" src="@/assets/logo.png" alt="" />
      Sign Up
      <img id="logo-img" src="@/assets/logo.png" alt="" />
    </h1>
    <div id="form-box">
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
          <a-input v-model:value="formState.password" type="password" autocomplete="off" />
        </a-form-item>
        <a-form-item has-feedback label="Confirm" name="checkPass">
          <a-input v-model:value="formState.checkPass" type="password" autocomplete="off" />
        </a-form-item>
        <a-form-item :wrapper-col="{ span: 24 }">
          <a-button type="primary" danger html-type="submit">Submit</a-button>
          <a-button style="margin-left: 10px" @click="resetForm" danger>Reset</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<style scoped>
#box {
  margin: 10% auto;
  padding: 1% 5% 0% 5%;
  width: 40rem;
  border: 1px solid silver;
}

h1 {
  display: flex;
  text-align: center;
  justify-content: center;
  align-items: center;
}

#form-box {
  text-align: center;
  margin: 0 auto;
}

#logo-img {
  width: 2rem;
  margin: 0 0.5rem;
}
</style>
