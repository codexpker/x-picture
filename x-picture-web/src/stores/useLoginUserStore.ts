import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser } from '@/api/userController.ts'
import Access_Enum from '@/access/accessEnum.ts'

/**
 * 存储登录用户信息状态
 */

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  /**
   * 获取登录用户
   */
  async function fetchLoginUser() {
    const res = await getLoginUser()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    } else {
      loginUser.value = { userRole: Access_Enum.NOT_LOGIN }
    }
  }

  /**
   * 设置登录用户
   */

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})
