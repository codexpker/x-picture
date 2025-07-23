<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">{{ route.query.id ? '修改空间' : '创建空间' }}</h2>
    <!-- 空间信息表单 -->
    <a-form name="basic" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="spaceName" label="空间名称">
        <a-input v-model:value="formData.spaceName" placeholder="输入空间名称" allow-clear />
      </a-form-item>
      <a-form-item name="spaceLevel" label="空间级别">
        <a-select
          v-model:value="formData.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="请输入空间级别"
          style="min-width: 200px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading"
          >{{ route.query.id ? '修改' : '创建' }}
        </a-button>
      </a-form-item>
    </a-form>
    <a-card title="空间级别介绍">
      <a-typography-paragraph>
        * 目前仅支持开通普通版，如需升级空间，请联系管理员
        <span>937296961@qq.com</span>
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList" :key="spaceLevel">
        {{ spaceLevel.text }}: 大小 {{ formatSize(spaceLevel.maxSize) }}, 数量
        {{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  addSpace,
  editSpace,
  getSpaceVoById,
  listSpaceLevel,
  updateSpace,
} from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { SPACE_LEVEL_ENUM, SPACE_LEVEL_OPTIONS } from '@/constants/space.ts'
import { formatSize } from '@/utils'

const oldSpace = ref<API.SpaceVO>()
const formData = reactive<API.SpaceUpdateRequest | API.SpaceAddRequest>({
  spaceName: '',
  spaceLevel: SPACE_LEVEL_ENUM.COMMON,
})
const loading = ref(false)
const router = useRouter()

// 空间级别信息
const spaceLevelList = ref<API.SpaceLevel[]>([])

// 获取空间级别
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevel()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data ?? []
  } else {
    message.error('加载空间级别失败，' + res.data.message)
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  loading.value = true
  const spaceId = oldSpace.value?.id
  let res
  if (spaceId) {
    // 更新
    res = await updateSpace({
      id: spaceId,
      ...formData,
    })
  } else {
    // 创建
    res = await addSpace({
      ...formData,
    })
  }

  if (res.data.code === 0 && res.data.data) {
    message.success(spaceId ? '修改成功' : '创建成功')
    // 跳转到空间详情页面
    router.push({ path: `/space/${spaceId ?? res.data.data}` })
  } else {
    message.error(spaceId ? '修改失败' : '创建失败' + res.data.message)
  }
  loading.value = false
}

const route = useRoute()

// 获取老数据
const getOldSpace = async () => {
  //从地址中读取出id
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoById({ id: id })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      oldSpace.value = data
      formData.spaceName = data.spaceName
      formData.spaceLevel = data.spaceLevel
    }
  }
}

onMounted(() => {
  fetchSpaceLevelList()
})

onMounted(() => {
  getOldSpace()
})
</script>

<style scoped>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
