package com.xpker.xpicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xpker.xpicture.model.dto.user.UserQueryRequest;
import com.xpker.xpicture.model.dto.user.UserRegisterRequest;
import com.xpker.xpicture.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xpker.xpicture.model.vo.user.LoginUserVO;
import com.xpker.xpicture.model.vo.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author 93729
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-07-07 10:40:27
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    long UserRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏后的登录用户
     *
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     */
    Boolean logout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 根据查询请求拼接条件构造器
     *
     * @param userQueryRequest
     * @return
     */
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 将密码加密
     * @param password
     * @return
     */
    public String getEncodePassword(String password);

    /**
     * 是否是管理员
     *
     * @param user 用户
     * @return
     */
    Boolean isAdmin(User user);
}
