<template>
  <div class="login-container">
    <a-form
      :model="form"
      :rules="rules"
      layout="vertical"
      class="login-form"
      ref="formRef"
    >
      <div class="logo-container">
        <img src="../assets/logo-2.png" alt="logo" class="logo" />
      </div>
      <a-form-item label="用户名" field="username">
        <a-input v-model="form.username" placeholder="请输入用户名" />
      </a-form-item>

      <a-form-item label="密码" field="password">
        <a-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
        />
      </a-form-item>
      <div style="display: flex; justify-content: space-between;margin-bottom: 10px;">
        <a-button type="primary" @click="handleLogin" style="width: 100%;" :loading="isLoading">
          登录
        </a-button>
        <a-button type="outline" @click="handleRegister" style="width: 100%;margin-left: 10px;" :loading="isLoading">
          注册
        </a-button>
      </div>
    </a-form>
  </div>
  
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Message } from '@arco-design/web-vue';
import { isMock } from '@/mock/mock';
import router from '@/router/router';
import { loginAPI, registerAPI } from '@/api/user';
import { useUserStore } from '@/stores';

const isLoading = ref(false)
const formRef = ref()
const userStore = useUserStore()
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const validRes = await formRef.value.validate()
  if(validRes !== undefined) {
      return false
  }
  if (isMock) {
    if (form.username === 'admin' && form.password === '123456') {
      Message.success('登录成功！')
      router.push('/device')
    } else {
      Message.error('用户名或密码错误！')
    }
    return
  }
  else{
    try {
      isLoading.value = true
      const res = await loginAPI(form)
      Message.success('登录成功！')
      userStore.setToken(res.data.Authorization)
      router.push('/device')
    } catch (error) {
      console.log(error)
    } finally {
      isLoading.value = false
    }
  }
}
const handleRegister = async () => {
  const validRes = await formRef.value.validate()
  if(validRes !== undefined) {
      return false
  }
  if (isMock) {
    Message.success('注册成功！')
    return
  }
  else{
    try {
      isLoading.value = true
      await registerAPI(form)
      Message.success('注册成功！')
    } catch (error) {
      console.log(error)
    } finally {
      isLoading.value = false
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #1f2b3e;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.logo {
  width: 200px;
  height: 150px;
}

.login-form {
  width: 300px;
  background-color: #f5f4f1;
  padding: 20px 20px 5px 20px;
  border-radius: 10px;
}
</style>
