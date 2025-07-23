<template>
  <div id="sider" v-if="loginUserStore.loginUser.id">
    <a-layout-sider width="200" v-model:collapsed="collapsed" breakpoint="lg" collapsible>
      <a-menu
        v-model:selectedKeys="current"
        mode="inline"
        :items="menuItems"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { PictureOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选择菜单
const current = ref<string[]>()
// 监听路由变化,更新当前选中菜单
router.afterEach((to) => {
  current.value = [to.path]
})

const collapsed = ref<boolean>(false);

/**
 * 实现路由跳转
 */

const doMenuClick = ({ key }: { key: string }) => {
  console.log(key)
  router.push({
    path: key,
  })
}

const menuItems = [
  {
    key: '/',
    icon: () => h(PictureOutlined),
    label: '公共图库',
    title: '公共图库'
  },
  {
    key: '/my_space',
    label: '我的空间',
    icon: () => h(UserOutlined),
  }
]
</script>


<style scoped>
#sider {
  background: #fff;
  border-right: 0.5px solid #eee;
}

#sider .ant-layout-sider {
  padding-top: 20px;
  background: none;
}

#sider :deep(.ant-layout-sider-trigger){
  background: #fff;
  color: unset;
}
</style>
