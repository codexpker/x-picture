import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import Access_Enum from '@/access/accessEnum.ts'
import checkAccess from '@/access/checkAccess.ts'

// 进入页面前进行权限校验
router.beforeEach(async (to, from, next) => {
  // 获取当前登录用户
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  // 如果之前没有尝试获取过登录用户信息，才自动登录
  if (!loginUser || !loginUser.userRole) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
  }
  //权限校验
  const needAccess = (to.meta?.access as string) ?? Access_Enum.NOT_LOGIN
  // 要跳转的页面必须要登录
  if (needAccess !== Access_Enum.NOT_LOGIN) {
    //如果没登录，跳转到登录页面
    if (!loginUser || !loginUser.userRole || loginUser.userRole === Access_Enum.NOT_LOGIN) {
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
    // 如果登录了，判断权限是否足够，如果不足，跳转到无权限页面
    if (!checkAccess(loginUser, needAccess)) {
      next('/noAuth')
      return
    }
  }
  next()
})
