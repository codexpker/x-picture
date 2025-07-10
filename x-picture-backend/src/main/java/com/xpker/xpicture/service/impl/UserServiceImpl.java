package com.xpker.xpicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xpker.xpicture.constant.SortConstant;
import com.xpker.xpicture.constant.UserConstant;
import com.xpker.xpicture.exception.BusinessException;
import com.xpker.xpicture.exception.ErrorCode;
import com.xpker.xpicture.exception.ThrowUtils;
import com.xpker.xpicture.model.dto.user.UserQueryRequest;
import com.xpker.xpicture.model.dto.user.UserRegisterRequest;
import com.xpker.xpicture.model.entity.User;
import com.xpker.xpicture.model.vo.user.LoginUserVO;
import com.xpker.xpicture.model.vo.user.UserVO;
import com.xpker.xpicture.service.UserService;
import com.xpker.xpicture.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 93729
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-07-07 10:40:27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return
     */
    @Override
    public long UserRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 1. 校验
        ThrowUtils.throwIf(
                StrUtil.hasBlank(userAccount, userPassword, checkPassword),
                new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空")
        );
        ThrowUtils.throwIf(
                userAccount.length() < 4 || userAccount.length() > 16,
                new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度在4-16位之间")
        );
        ThrowUtils.throwIf(
                userPassword.length() < 8 || userPassword.length() > 16,
                new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度在8-16位之间")
        );
        ThrowUtils.throwIf(
                !userPassword.equals(checkPassword),
                new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致")
        );
        // 校验用户是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0, new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在"));
        // 将密码加密处理
        String encodePassword = getEncodePassword(userPassword);
        // 创建实体类
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encodePassword);
        // 设置默认值
        user.setUserName(UserConstant.DEFAULT_USERNAME);
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, new BusinessException(ErrorCode.SYSTEM_ERROR, "用户注册失败"));
        // 返回用户id
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request      请求对象
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(
                StrUtil.hasBlank(userAccount, userPassword),
                new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空")
        );
        ThrowUtils.throwIf(
                userAccount.length() < 4 || userAccount.length() > 16,
                new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度在4-16位之间")
        );
        ThrowUtils.throwIf(
                userPassword.length() < 8 || userPassword.length() > 16,
                new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度在8-16位之间")
        );
        // 根据用户名查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User user = userMapper.selectOne(queryWrapper);
        // 将密码解码和输入的密码进行校验
        ThrowUtils.throwIf(
                (user == null || !BCrypt.checkpw(userPassword, user.getUserPassword())),
                new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误")
        );
        // 登录成功，记录登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        // 返回脱敏后的用户信息
        return this.getLoginUserVO(user);
    }

    /**
     * 获取登录用户信息
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 从存储的里面读出信息
        Object obj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        // 转化为User类型
        User user = (User) obj;
        ThrowUtils.throwIf(user == null || user.getId() == null,
                new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        // 可能会存在缓存问题，重新从数据库中读取一遍
        user = userMapper.selectById(user.getId());
        ThrowUtils.throwIf(user == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        return user;
    }


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public Boolean logout(HttpServletRequest request) {
        // 判断用户是否登录
        User loginUser = this.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        // 移除用户的登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }


    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏后的用户列表
     *
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return null;
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }


    /**
     * 将密码加密
     *
     * @param password 密码
     * @return
     */
    public String getEncodePassword(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * 判断用户是否为管理员
     *
     * @param user 用户
     * @return
     */
    @Override
    public Boolean isAdmin(User user) {
        return user != null && UserConstant.ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 获取脱敏后的用户
     *
     * @param user
     * @return
     */
    public LoginUserVO getLoginUserVO(User user) {
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 根据查询请求拼接条件构造器
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数为空"));
        // 从查询请求中获得参数
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        // 构造条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        /**
         * @param condition 是否应用排序
         * @param isAsc 是否升序
         * @param columns 排序字段，可以为多个
         */
        // QueryWrapper<T> orderBy(boolean condition, boolean isAsc, String ...columns)
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals(SortConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

}




