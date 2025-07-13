<template>
  <div id="userLoginPage">
    <h2 class="title">雨云图库 - 用户登录</h2>
    <div class="desc">企业级智能协同云图库</div>
    <a-form
      :model="formState"
      name="basic"
      autocomplete="off"
      @finish="handleSubmit"
    >
      <a-form-item
        name="userAccount"
        :rules="[{ required: true, message: '请输入账号' }]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, max: 16, message: '密码长度在8-16位之间'}
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>

      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
const loginUserStore = useLoginUserStore();
import { useRoute, useRouter } from 'vue-router'
const router = useRouter();
const route = useRoute()
/**
 * 提交表单
 */
const handleSubmit = async (values: API.UserLoginRequest) => {
  const res = await userLogin(values);
  if(res.data.code == 0 && res.data.data){
    // 登录成功，把用户信息存储到全局状态中
    await loginUserStore.fetchLoginUser();
    message.success('登录成功')
    const redirect = route.query.redirect?.toString() || '/'
    // 跳转到目标页
    router.push({
      path: redirect ,
      replace: true,
    })
  }else{
    message.error('登陆失败' + res.data.message)
  }
}

/**
 * 绑定表单信息
 */
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})
</script>

<style scoped>

#userLoginPage {
  max-width: 360px;
  margin: 0 auto;
}
#userLoginPage .title{
  text-align: center;
  margin-bottom: 16px;
  margin-top: 100px;
}

#userLoginPage .desc{
  text-align: center;
  margin-bottom: 16px;
  color: #bbb;
}

#userLoginPage .tips{
  text-align: center;
  color: #bbb;
  font-size: 12px;
  margin-bottom: 16px;
}
</style>
