<template>
  <div id="pictureDetailPage">
    <a-row :gutter="[16, 16]">
      <a-col :sm="24" :md="16" :xl="18">
        <!-- 图片展示区 -->
        <a-card title="图片预览">
          <a-image :src="picture.url" style="max-height: 600px; object-fit: contain" />
        </a-card>
      </a-col>
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :src="picture.userVO?.userAvatar" />
                <div>{{ picture.userVO?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">{{ picture.name ?? '未命名' }}</a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              <a-tag color="green">
                {{ picture.category ?? '默认' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in picture.tags" :key="tag" color="blue">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
          </a-descriptions>
          <!-- 操作按钮 -->
          <a-space>
            <a-button type="primary" @click="doDownload">
              免费下载
              <template #icon>
                <DownloadOutlined/>
              </template>
            </a-button>
            <a-button v-if="canEdit" @click="doEdit" type="default">
              编辑
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-button danger v-if="canEdit" @click="doDelete">
              删除
              <template #icon>
                <DeleteOutlined />
              </template>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
// 定义属性
import { deletePicture, getPictureVoById } from '@/api/pictureController.ts'
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { downloadImage, formatSize } from '@/utils'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import accessEnum from '@/access/accessEnum.ts'
import { useRouter } from 'vue-router'
import { EditOutlined, DeleteOutlined, DownloadOutlined } from '@ant-design/icons-vue'

const props = defineProps<{ id: string | number }>()
const picture = ref<API.PictureVO>({})
//获取图片详情
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoById({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (error: any) {
    message.error('获取图片详情失败，' + error.message)
  }
}
const loginUserStore = useLoginUserStore()
//判断用户是否有编辑权限
const canEdit = computed(() => {
  const loginUser = loginUserStore.loginUser
  //没有登录，不可编辑
  if (!loginUser.id) {
    return false
  }
  const user = picture.value.userVO || {}
  // 仅本人或管理员可编辑
  return loginUser.userRole === accessEnum.ADMIN || loginUser.id === user.id
})

const router = useRouter()

//编辑
const doEdit = () => {
  console.log(picture.value.id)
  router.push({
    path: '/add_picture',
    query: {
      id: picture.value.id,
    },
  })
}

//删除
const doDelete = async () => {
  const id = picture.value.id
  if(!id){
    return
  }
  const res = await deletePicture({
    id: id
  })
  if(res.data.code === 0) {
    message.success('删除成功')
    await router.push({ path: '/' })
  }else{
    message.error('删除失败')
  }
}


// 下载
const doDownload = () => {
  downloadImage(picture.value.url)
}
onMounted(() => {
  fetchPictureDetail()
})
</script>

<style scoped></style>
