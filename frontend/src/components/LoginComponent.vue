<script setup>
    import { ref, computed } from 'vue'
    import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
    import { useUserStore } from '@/stores/user-store'

    const userStore = useUserStore()

    const userInfo = ref({
        loginId: '',
        password: ''
    })

    const login = async () => {
        await userStore.login(userInfo.value)
    }

    const onFinishFailed = errorInfo => {
        console.log('Failed:', errorInfo);
    };

    // 로그인 버튼 활성화
    const disabled = computed(() => {
        return !(userInfo.value.loginId && userInfo.value.password);
    });
</script>

<template>
    <a-form
      :model="userInfo"
      name="normal_login"
      class="login-form"
      @finish="login"
      @finishFailed="onFinishFailed"
    >
      <a-form-item
        label="ID"
        name="loginId"
        :rules="[{ required: true, message: 'Please input your ID!' }]"
      >
        <a-input v-model:value="userInfo.loginId">
          <template #prefix>
            <UserOutlined class="site-form-item-icon" />
          </template>
        </a-input>
      </a-form-item>
  
      <a-form-item
        label="Password"
        name="password"
        :rules="[{ required: true, message: 'Please input your password!' }]"
      >
        <a-input-password v-model:value="userInfo.password">
          <template #prefix>
            <LockOutlined class="site-form-item-icon" />
          </template>
        </a-input-password>
      </a-form-item>

      <a-form-item>
        <a class="login-form-forgot" href="">Forgot password</a>
      </a-form-item>
  
      <a-form-item>
        <a-button :disabled="disabled" type="primary" html-type="submit" class="login-form-button">
          로그인
        </a-button>
        <a href="">register now!</a>
      </a-form-item>
    </a-form>
  </template>

<sytle scoped>
</sytle>