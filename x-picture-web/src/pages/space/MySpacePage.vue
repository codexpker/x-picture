<template>
  <div id="mySpacePage">
    <p>正在跳转，请稍后 ~</p>
  </div>
</template>

<script setup lang="ts">
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { listSpaceVoByPage } from '@/api/spaceController.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { onMounted } from 'vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()
// 检查是否有个人空间
const checkUserSpace = async () => {
  const loginUser = loginUserStore.loginUser
  // 用户未登录，跳转到登录页
  if (!loginUser?.id) {
    router.replace('/user/login')
    return
  }
  // 用户已登录，获取该用户已创建的空间
  const res = await listSpaceVoByPage({
    userId: loginUser.id,
    current: 1,
    pageSize: 1,
  })
  // 如果有，则进入第一个空间
  if (res.data.code === 0) {
    if (res.data.data?.records?.length > 0) {
      const space = res.data.data?.records[0]
      router.replace(`/space/${space.id}`)
    } else {
      // 如果没有，则进入创建空间页面
      router.replace('/add_space')
      message.warn('请先创建空间')
    }
  } else {
    message.error('加载我的空间失败，' + res.data.message)
  }
}

onMounted(() => {
  checkUserSpace()
})
</script>

<style scoped></style>
