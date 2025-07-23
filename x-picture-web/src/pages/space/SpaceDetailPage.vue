<template>
  <div id="spaceDetailPage">
    <!--空间信息-->
    <a-flex justify="space-between">
      <h2>{{ space.spaceName }}(私有空间)</h2>
      <a-space size="middle">
        <a-button type="primary" :href="`/add_picture?spaceId=${id}`" target="_blank">
          + 创建图片
        </a-button>
        <a-tooltip :title="`占用空间${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`">
          <a-progress
            type="circle"
            :percent="((space.totalSize * 100) / space.maxSize).toFixed(1)"
            :size="42"
          />
        </a-tooltip>
      </a-space>
    </a-flex>
    <!-- 图片列表 -->
    <PictureList :dataList="dataList" :loading="loading" :showOp="true" :onReload="fetchData"/>
    <a-pagination
      style="text-align: right"
      :current="searchParams.current"
      :pageSize="searchParams.pageSize"
      :show-total="()=> `图片总数：${total} / ${space.maxCount}`"
      @change="onPageChange"
    />
  </div>
</template>

<script setup lang="ts">
// 定义属性
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { getSpaceVoById } from '@/api/spaceController.ts'
import { listPictureVoByPage } from '@/api/pictureController.ts'
import { formatSize } from '@/utils'
import PictureList from '@/components/PictureList.vue'

const props = defineProps<{ id: string | number }>()
const space = ref<API.SpaceVO>({})

// 数据
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(false)

//搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    spaceId: props.id,
    ...searchParams,
    tags: [],
  }
  const res = await listPictureVoByPage(params)
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
  loading.value = false
}

onMounted(async () => {
  fetchData()
})

// 分页参数
const onPageChange = (page: number, pageSize: number) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  fetchData()
}

//获取空间详情
const fetchSpaceDetail = async () => {
  try {
    const res = await getSpaceVoById({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    } else {
      message.error('获取空间详情失败，' + res.data.message)
    }
  } catch (error: any) {
    message.error('获取空间详情失败，' + error.message)
  }
}

onMounted(() => {
  fetchSpaceDetail()
})
</script>

<style scoped></style>
