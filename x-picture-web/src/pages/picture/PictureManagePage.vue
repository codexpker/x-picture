<template>
  <div id="pictureManagePage">
    <!-- 创建图片 -->
    <a-flex justify="space-between">
      <h2>图片管理</h2>
      <a-button type="primary" href="/add_picture" target="_blank">创建图片</a-button>
    </a-flex>
    <!--  搜索表单   -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch" style="margin-bottom: 16px">
      <a-form-item label="关键词" name="searchParams">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="从简介或用户名中搜索"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="类型" name="category">
        <a-input v-model:value="searchParams.category" placeholder="请输入类型" allow-clear />
      </a-form-item>
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="请输入标签"
          style="min-width: 200px"
          allow-clear
        >
        </a-select>
      </a-form-item>
      <a-form-item label="审核状态" name="reviewStatus">
        <a-select
          v-model:value="searchParams.reviewStatus"
          :options="PIC_REVIEW_STATUS_OPTIONS"
          placeholder="请输入审核状态"
          style="min-width: 200px"
          allow-clear
        >
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <!--  表格  -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      @change="doTableChange"
      :pagination="pagination"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="120"></a-image>
        </template>
        <!-- 分类 -->
        <template v-if="column.dataIndex === 'category'">
          <a-space>
            <a-tag v-if="column.dataIndex === 'category' && record.category" color="green">
              {{ record.category }}
            </a-tag>
          </a-space>
        </template>
        <!-- 标签 -->
        <template v-if="column.dataIndex === 'tags'">
          <div
            style="
              white-space: normal; /* 允许换行 */
              word-wrap: break-word; /* 允许单词内换行 */
              max-width: 200px; /* 限制最大宽度（可选） */
            "
          >
            <a-space wrap>
              <!-- 关键：使用 wrap 模式让标签自动换行 -->
              <a-tag
                v-for="tag in JSON.parse(record.tags || '[]')"
                :key="tag"
                color="blue"
                style="margin-bottom: 4px"
              >
                {{ tag }}
              </a-tag>
            </a-space>
          </div>
        </template>
        <!-- 图片信息 -->
        <template v-if="column.dataIndex === 'picInfo'">
          <div>格式： {{ record.picFormat }}</div>
          <div>宽度： {{ record.picWidth }}</div>
          <div>高度： {{ record.picHeight }}</div>
          <div>宽高比： {{ record.picScale }}</div>
          <div>大小： {{ (record.picSize / 1024).toFixed(2) }} KB</div>
        </template>
        <!-- 审核信息 -->
        <template v-if="column.dataIndex === 'reviewMessage'">
          <div>审核状态：
            <a-tag color="green" v-if="PIC_REVIEW_STATUS_ENUM.PASS === record.reviewStatus">
              {{ PIC_REVIEW_STATUS_MAP[record.reviewStatus] }}
            </a-tag>
            <a-tag color="yellow" v-if="PIC_REVIEW_STATUS_ENUM.REVIEWING === record.reviewStatus">
              {{ PIC_REVIEW_STATUS_MAP[record.reviewStatus] }}
            </a-tag>
            <a-tag color="red" v-if="PIC_REVIEW_STATUS_ENUM.REJECT === record.reviewStatus">
              {{ PIC_REVIEW_STATUS_MAP[record.reviewStatus] }}
            </a-tag>
          </div>
          <div>审核信息： {{ record.reviewMessage }}</div>
          <div>审核人： {{ record.reviewerId }}</div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"
              type="primary"
              ghost
              @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.PASS)"
              >通过
            </a-button>
            <a-button
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT"
              danger
              @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.REJECT)"
              >拒绝
            </a-button>
            <a-button type="primary" ghost :href="`/add_picture?id=${record.id}`" target="_blank"
              >编辑
            </a-button>
            <a-button danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { deletePicture, doPictureReview, listPictureByPage } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { ref } from 'vue'
import dayjs from 'dayjs'
import { PIC_REVIEW_STATUS_ENUM, PIC_REVIEW_STATUS_MAP, PIC_REVIEW_STATUS_OPTIONS } from '@/constants/picture.ts'
// 需要展示的表格列
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '图片',
    dataIndex: 'url',
  },
  {
    title: '名称',
    dataIndex: 'name',
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'category',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo',
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '审核信息',
    dataIndex: 'reviewMessage',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 定义数据
// ref一般用于普通的数据，变量等，一般是监控整体更新
const dataList = ref<API.Picture>([])
// 数据总目
const total = ref(0)
// 搜索条件
// reactive一般用于对对象内的字段监控的查询
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 3,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  //searchParams是响应式变量，传值的时候展开再传
  const res = await listPictureByPage({
    ...searchParams,
  })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
}

// 分页
// 设置分页器，利用computed动态计算
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    // 允许用户切换页码
    showSizeChanger: true,
    showTotal: (total) => `共${total}条`,
  }
})

// 表格变化后，重新获取数据
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 表单提交事件
const doSearch = () => {
  //每一次搜索前，重置页码
  searchParams.current = 1
  // 获取数据
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deletePicture({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    //刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 处理审核
const handleReview = async (record: API.Picture, reviewStatus: number) => {
  const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员审核通过' : '管理员审核拒绝'
  const res = await doPictureReview({
    id: record.id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success('审核操作成功')
    // 更新数据
    fetchData()
  } else {
    message.error('审核操作失败')
  }
}

// 页面加载时执行一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped></style>
