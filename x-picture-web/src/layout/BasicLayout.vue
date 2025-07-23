<template>
  <div id="basicLayout">
    <a-layout>
      <a-layout-header class="header">
        <NavBar />
      </a-layout-header>
      <a-layout>
        <Sider />
        <a-layout-content class="content">
          <router-view />
        </a-layout-content>
      </a-layout>

      <a-layout-footer class="footer">Copyright© by xpker</a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import Sider from '@/components/Sider.vue'
</script>

<style scoped>
#basicLayout .footer {
  position: fixed;
  bottom: 0;
  text-align: center;
  left: 0;
  right: 0;
  padding: 16px;
  background: #efefef;
}

#basicLayout .content {
  background: linear-gradient(to right, #fefefe, #fff);
  padding: 28px;
  margin-bottom: 30px;
}

#basicLayout :deep(.ant-menu-root){
  border-bottom: none !important;
  border-inline-end: none !important;
}

#basicLayout .header {
  background: white;
  color: unset;
  margin-bottom: 1px;
  padding-inline: 20px;
}

#basicLayout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
