import { createRouter, createWebHistory } from 'vue-router'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import HomePage from '@/pages/HomePage.vue'
import Access_Enum from '@/access/accessEnum.ts'
import NoAuthPage from '@/pages/NoAuthPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginPage,
      meta:{
        hideInMenu: true,
      }
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterPage,
      meta:{
        hideInMenu: true,
      }
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManagePage,
      meta:{
        access: Access_Enum.ADMIN,
      }
    },
    {
      path: '/noAuth',
      name: '无权限',
      component: NoAuthPage,
      meta:{
        hideInMenu: true,
      }
    },
  ],
})

export default router
