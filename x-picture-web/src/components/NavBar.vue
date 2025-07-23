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
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="menus"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="!loginUserStore.loginUser.id">
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
          <div v-else>
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item>
                    <router-link to="/my_space">
                      <UserOutlined />
                      我的空间
                    </router-link>
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { LogoutOutlined, HomeOutlined, UserOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'

import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { logout } from '@/api/userController.ts'

import checkAccess from '@/access/checkAccess.ts'

const router = useRouter()
const loginUserStore = useLoginUserStore()
// 获取所有路由
const routes = router.getRoutes()

// 把路由项转换成菜单项
const routeItemToMenu = (item: any) => {
  const isHome = item.path === '/'
  return {
    key: item.path,
    label: item.name,
    title: item.name,
    icon: isHome ? h(item.meta?.icon ?? HomeOutlined) : undefined, //仅在主页路径时显示icon
  }
}

// 过滤菜单项
const items = computed(() => {
  const filtered = routes.filter((item) => {
    if (item.meta?.hideInMenu) return false
    return checkAccess(loginUserStore.loginUser, item.meta?.access as string)
  })

  // 找到主页
  const homeIndex = filtered.findIndex((item) => item.path === '/')
  if (homeIndex > 0) {
    // 如果主页不在第一位，则调整到第一位
    const [homeItem] = filtered.splice(homeIndex, 1)
    filtered.unshift(homeItem)
  }

  return filtered.map(routeItemToMenu)
})

const menus = ref<MenuProps['items']>(items)

/**
 * 用户注销
 */
const doLogout = async () => {
  const res = await logout()
  if (res.data.code === 0) {
    // 清除登录状态
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    // 跳转登录页
    await router.push({ path: '/user/login' })
  } else {
    message.error('退出登录失败' + res.data.message)
  }
}

/**
 * 实现路由跳转
 */

const doMenuClick = ({ key }: { key: string }) => {
  console.log(key)
  router.push({
    path: key,
  })
}

// 当前选择菜单
const current = ref<string[]>()
// 监听路由变化,更新当前选中菜单
router.afterEach((to) => {
  current.value = [to.path]
})
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
