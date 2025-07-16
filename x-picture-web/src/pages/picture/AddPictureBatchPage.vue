<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px">批量创建图片</h2>
    <!-- 图片信息表单 -->
    <a-form name="basic" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item
        name="searchText"
        label="关键词"
        :rules="[{ required: true, message: '请输入关键词' }]"
      >
        <a-input v-model:value="formData.searchText" placeholder="输入关键词" allow-clear />
      </a-form-item>
      <a-form-item name="count" label="抓取数量">
        <a-input-number
          v-model:value="formData.count"
          placeholder="请输入抓取数量"
          :min="1"
          :max="30"
          style="min-width: 180px"
          allow-clear
        >
        </a-input-number>
      </a-form-item>
      <a-form-item name="namePrefix" label="名称前缀">
        <a-auto-complete
          v-model:value="formData.namePrefix"
          placeholder="请输入名称前缀，会自动补充序号"
          allow-clear
        >
        </a-auto-complete>
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-auto-complete
          v-model:value="formData.category"
          placeholder="输入分类"
          :options="categoryOptions"
          allow-clear
        >
        </a-auto-complete>
      </a-form-item>
      <a-form-item name="tags" label="标签">
        <a-select
          v-model:value="formData.tags"
          mode="tags"
          :options="tagOptions"
          placeholder="请输入标签"
          allow-clear
        ></a-select>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          执行任务
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { listPictureTagCategory, uploadPictureByBatch } from '@/api/pictureController.ts'

const formData = reactive<API.PictureUploadByBatchRequest>({})
const loading = ref<boolean>(false)
const router = useRouter()

/**
 * 提交表单
 */
const handleSubmit = async () => {
  loading.value = true
  const res = await uploadPictureByBatch({
    ...formData,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success(`创建图片成功，共${res.data.data}条`)
    // 跳转到首页
    router.push({ path: '/' })
  } else {
    message.error('创建图片失败', res.data.message)
  }
  loading.value = false
}


const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

/**
 * 获取标签和选项列表
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategory()
  if (res.data.code === 0 && res.data.data) {
    // 转化成下拉选项组件接受的格式
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('加载选项失败,' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})
</script>

<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
