<template>
  <div id="NavBar">
    <a-row :wrap="false">
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="title-bar">
            <img class="logo" src="@/assets/logo.png" alt="logo" />
            <div class="title">雨云图库</div>
          </div>
        </RouterLink>
      </a-col>
      <a-col flex="auto">
        <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" @click="MenuClick" />
      </a-col>
      <a-col flex="100px">
        <div v-if="!loginUserStore.loginUser.id" class="user-login-status" href="/user/login">
          <a-button type="primary">登录</a-button>
        </div>
        <div v-else>
          {{loginUserStore.loginUser.userName ?? '无名'}}
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { MailOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'

import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const loginUserStore = useLoginUserStore();


/**
 * 实现路由跳转
 */
const router = useRouter()
const MenuClick = ({ key }: { key: string }) => {
  console.log(key);
  router.push({
    path: key,
  })
}

// 当前选择菜单
const current = ref<string[]>()
// 监听路由变化,更新当前选中菜单
router.afterEach((to) => {
  current.value = [to.path];
})

const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(MailOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
  },
])
</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.logo {
  height: 48px;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}
</style>
