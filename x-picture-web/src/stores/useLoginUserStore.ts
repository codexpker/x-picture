import { ref } from 'vue'
import { defineStore } from 'pinia'

/**
 * 存储登录用户信息状态
 */

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<any>({
    userName: '未登录',
  })

  /**
   * 获取登录用户
   */
  async function fetchLoginUser() {
    // to do
    setTimeout(() => (loginUser.value = { userName: '测试用户', id: 1 }), 3000)
  }

  /**
   * 设置登录用户
   */

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})
