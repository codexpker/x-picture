import AccessEnum from '@/access/accessEnum.ts'
import Access_Enum from '@/access/accessEnum.ts'

/**
 * 检查权限（判断当前用户是否具有某个权限）
 * @param loginUser 当前登录用户
 * @param needAccess  需要有的权限
 * @return boolean 有无权限
 */
const checkAccess = (loginUser: API.LoginUserVO, needAccess = AccessEnum.NOT_LOGIN) => {
  // 获取当前登录用户具有的权限（如果没有登录用户则为未登录）
  const loginUserAccess = loginUser?.userRole ?? AccessEnum.NOT_LOGIN;
  if(needAccess === AccessEnum.NOT_LOGIN){
    return true;
  }
  // 如果当前用户要登录才能访问
  if(needAccess === AccessEnum.USER){
    if(loginUserAccess === Access_Enum.NOT_LOGIN){
      return false;
    }
  }
  // 如果当前用户为管理员才能访问
  if(needAccess === AccessEnum.ADMIN) {
    // 如果不是管理员，表示无权限
    if(loginUserAccess !== Access_Enum.ADMIN){
      return false;
    }
  }
  return true;
}

export default checkAccess;
