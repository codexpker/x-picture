<template>
  <a-config-provider :locale="appLocale">
    <div id="app">
      <BasicLayout />
    </div>
  </a-config-provider>
</template>

<script setup lang="ts">
import BasicLayout from '@/layout/BasicLayout.vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN' //引入ant-design-vue的中文语言包
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn' //引入dayjs的中文语言包
//设置dayjs默认语言为中文
dayjs.locale('zh-cn')
//使用Composition API和setup函数定义响应式数据
const appLocale = zhCN
</script>

<style scoped></style>
