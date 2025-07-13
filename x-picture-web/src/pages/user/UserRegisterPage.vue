<template>
  <div id="userRegisterPage">
    <h2 class="title">雨云图库 - 用户注册</h2>
    <div class="desc">企业级智能协同云图库</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号"/>
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, max: 16, message: '密码长度在8-16位之间' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码"/>
      </a-form-item>

      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请确认密码' },
          { min: 8, max: 16, message: '密码长度在8-16位之间' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" />
      </a-form-item>

      <div class="tips">
        已有账号？
        <RouterLink to="/user/register">去登录</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { userRegister } from '@/api/userController.ts'
import { message } from 'ant-design-vue'

import { useRouter } from 'vue-router'

const router = useRouter()
/**
 * 提交表单
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  // 校验两次密码是否一致
  if (values.userPassword !== values.checkPassword) {
    message.error('两次密码不一致')
    return
  }
  const res = await userRegister(values)
  if (res.data.code === 0 && res.data.data) {
    // 注册成功
    message.success('注册成功')
    //跳转至登录页
    router.push({
      path: '/user/login',
      replace: true,
    })
  }else{
    message.error('注册失败' + res.data.message)
  }
}

/**
 * 绑定表单信息
 */
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})
</script>

<style scoped>
#userRegisterPage {
  max-width: 360px;
  margin: 0 auto;
}

#userRegisterPage .title {
  text-align: center;
  margin-bottom: 16px;
  margin-top: 100px;
}

#userRegisterPage .desc {
  text-align: center;
  margin-bottom: 16px;
  color: #bbb;
}

#userRegisterPage .tips {
  text-align: center;
  color: #bbb;
  font-size: 12px;
  margin-bottom: 16px;
}
</style>
