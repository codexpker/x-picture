<template>
  <div id="homePage">
    <!-- 分类 + 标签 -->
    <a-tabs v-model:activeKey="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">标签：</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        v-model:value="searchParams.searchText"
        placeholder="从海量图片中搜索"
        enter-button="搜索"
        size="large"
        allow-clear
        @search="doSearch"
      />
    </div>
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :pagination="pagination"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item>
          <a-card hoverable style="width: 240px" @click="doClickPicture(picture)">
            <template #cover>
              <img
                :alt="picture.name"
                :src="picture.url"
                style="height: 180px; object-fit: cover"
              />
            </template>
            <a-card-meta :title="picture.name">
              <template #avatar>
                <a-avatar :src="picture.userVO?.userAvatar" />
              </template>
            </a-card-meta>
            <div style="margin-top: 12px">
              <a-flex wrap="wrap" gap="small">
                <a-tag color="green" v-if="picture.category">
                  {{ picture.category }}
                </a-tag>
                <a-tag v-for="tag in picture.tags" :key="tag" color="blue">
                  {{ tag }}
                </a-tag>
              </a-flex>
            </div>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
// 定义数据
import { computed, onMounted, reactive, ref } from 'vue'
import { listPictureTagCategory, listPictureVoByPage } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const dataList = ref<API.PictureVO>([])
const total = ref(0)
const loading = ref(false)
// 定义被选中的分类(默认全部)
const selectedCategory = ref<string>('all')
// 定义分类列表
const categoryList = ref<string[]>([])
//定义标签列表
const tagList = ref<string[]>([])
//选中标签列表
const selectedTagList = ref<string[]>([])

// 获取标签和分页列表
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategory()
  if (res.data.code === 0 && res.data.data) {
    categoryList.value = res.data.data.categoryList ?? []
    tagList.value = res.data.data.tagList ?? []
  } else {
    message.error('加载分类标签失败' + res.data.message)
  }
}
const router =  useRouter()
// 跳转到标签页
const doClickPicture = (picture) => {
  router.push({
    path: `/picture/${picture.id}`
  })
}

//搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    ...searchParams,
    tags: [],
  }
  if(selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  // 选中的标签为[true, false, false, true]形式，要转换
  selectedTagList.value.forEach((useTag, index) => {
    if(useTag){
      params.tags.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPage(params)
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
  loading.value = false
}

// 分页参数
// 设置分页器，利用computed动态计算
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 12,
    total: total.value,
    onChange: (page, pageSize) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})

// 点击搜索按钮，触发搜索事件
const doSearch = () => {
  //重置页码
  searchParams.current = 1
  // 获取数据
  fetchData()
}

onMounted(() => {
  getTagCategoryOptions()
  fetchData()
})
</script>

<style scoped>
#homePage .search-bar {
  max-width: 480px;
  margin: 0 auto;
  margin-bottom: 16px;
}
#homePage .tag-bar{
  margin-bottom:  16px;
}
</style>
