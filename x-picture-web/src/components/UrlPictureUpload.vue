<template>
  <div class="url-picture-upload">
    <a-input-group compact style="margin: 16px">
      <a-input
        v-model:value="fileUrl"
        style="width: calc(100% - 120px)"
        placeholder="请输入图片URL"
      />
      <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px"
        >提交
      </a-button>
    </a-input-group>
    <div class="img-wrapper">
      <a-image v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { uploadPictureByUrl } from '@/api/pictureController.ts'

// 由调用组件的页面管理属性的值
interface Props {
  picture?: API.PictureVO
  OnSuccess: (newPicture: API.PictureVO) => void
  spaceId?: number
}

const props = defineProps<Props>()

const loading = ref<boolean>(false)
const fileUrl = ref<string>()

/**
 * 上传
 */
const handleUpload = async () => {
  loading.value = true
  const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
  try {
    params.spaceId = props.spaceId
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrl(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      // 将上传成功的图片信息传递给父组件
      props.OnSuccess?.(res.data.data)
    }else{
      message.error('图片上传失败' + res.data.data.message)
    }
  } catch (error) {
    console.log(error)
    message.error('图片上传失败')
  } finally {
    loading.value = false
  }
}
</script>
<style scoped>
.url-picture-upload img {
  max-width: 100%;
  max-height: 480px;
  margin: 0 auto;
}

.url-picture-upload .img-wrapper {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
