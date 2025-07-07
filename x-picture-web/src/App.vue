<template>
  <div id="app">
    <BasicLayout/>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layout/BasicLayout.vue'
import {health} from '@/api/healthController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const loginUserStore = useLoginUserStore();
loginUserStore.fetchLoginUser();

health().then(res => {
  console.log(res);
})

</script>

<style>
#app{

}
</style>
