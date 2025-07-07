<template>
  <div id="BasicLayout">
    <a-layout>
      <a-layout-header class="header">
        <NavBar />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">Copyright© by xpker</a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'

</script>

<style scoped>
#BasicLayout .footer {
  position: fixed;
  bottom: 0;
  text-align: center;
  left: 0;
  right: 0;
  padding: 16px;
  background: #efefef;
}

#BasicLayout .content {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 20px;
}

#BasicLayout .header {
  background: white;
  color: unset;
  margin-bottom: 16px;
  padding-inline: 20px;
}
</style>
