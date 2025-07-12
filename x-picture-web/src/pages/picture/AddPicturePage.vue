<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">{{ route.query.id ? '修改图片' : '创建图片' }}</h2>
    <!-- 图片上传组件 -->
    <PictureUpload :picture="picture" :onSuccess="onSuccess" />
    <!-- 图片信息表单 -->
    <a-form
      v-if="picture"
      name="basic"
      layout="vertical"
      :model="pictureForm"
      @finish="handleSubmit"
    >
      <a-form-item name="name" label="名称">
        <a-input v-model:value="pictureForm.name" placeholder="输入图片名称" allow-clear />
      </a-form-item>
      <a-form-item name="introduction" label="简介">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="输入图片简介"
          :auto-size="{ minRows: 2, maxRows: 5 }"
          allow-clear
        >
        </a-textarea>
        <a-form-item name="category" label="分类">
          <a-auto-complete
            v-model:value="pictureForm.category"
            placeholder="输入分类"
            :options="categoryOptions"
            allow-clear
          >
          </a-auto-complete>
        </a-form-item>
        <a-form-item name="tags" label="标签">
          <a-select
            v-model:value="pictureForm.tags"
            mode="tags"
            :options="tagOptions"
            placeholder="请输入标签"
            allow-clear
          ></a-select>
        </a-form-item>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%"
          >{{ route.query.id ? '修改' : '创建' }}
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import { editPicture, getPictureVoById, listPictureTagCategory } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'

const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditRequest>({})
const router = useRouter()

/**
 * 上传成功后，更新图片
 * @param newPicture
 */
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

/**
 * 提交表单
 */
const handleSubmit = async (values: API.PictureEditRequest) => {
  const pictureId = picture.value?.id
  if (!pictureId) {
    return
  }
  const res = await editPicture({
    ...values,
    id: pictureId,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success(pictureId ? '修改成功' : '创建成功')
    // 跳转到图片详情页面
    router.push({ path: `/picture/${pictureId}` })
  } else {
    message.error(pictureId ? '修改失败' : '创建失败' + res.data.message)
  }
}

// 定义分类列表和标签列表
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

const route = useRoute()

// 获取老数据
const getOldPicture = async () => {
  //从地址中读取出id
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoById({ id: id })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getTagCategoryOptions()
  getOldPicture()
})
</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
