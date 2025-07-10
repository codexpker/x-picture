package com.xpker.xpicture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpker.xpicture.annotation.AuthCheck;
import com.xpker.xpicture.common.BaseResponse;
import com.xpker.xpicture.common.DeleteRequest;
import com.xpker.xpicture.common.ResultUtils;
import com.xpker.xpicture.constant.UserConstant;
import com.xpker.xpicture.exception.ErrorCode;
import com.xpker.xpicture.exception.ThrowUtils;
import com.xpker.xpicture.model.dto.user.*;
import com.xpker.xpicture.model.entity.User;
import com.xpker.xpicture.model.vo.user.LoginUserVO;
import com.xpker.xpicture.model.vo.user.UserVO;
import com.xpker.xpicture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册请求
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验参数
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        // 调用业务层代码
        long id = userService.UserRegister(userRegisterRequest);
        return ResultUtils.success(id);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest
     * @param request
     * @return
     */

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 调用业务层代码
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取用户信息接口
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LoginUserVO loginUserVO = userService.getLoginUserVO(loginUser);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        Boolean result = userService.logout(request);
        return ResultUtils.success(result);
    }

//----------------------------------用户管理 -----------------------------


    /**
     * 新增用户
     * @param userAddRequest 新增用户请求
     * @return
     */
    //region
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        // 校验参数
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        // 使用BeanUtil拷贝过去
        BeanUtils.copyProperties(userAddRequest, user);
        // 补充默认信息
        String encodePassword = userService.getEncodePassword(UserConstant.DEFAULT_PASSWORD);
        user.setUserPassword(encodePassword);
        boolean saveResult = userService.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     * @param deleteRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest){
        // 校验参数
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 修改用户
     * @param userUpdateRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        // 校验参数
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null,
                ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据id获取用户（仅管理员）
     * @param id
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id){
        // 校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取包装类（脱敏）
     * @param id
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id){
        // 校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        UserVO userVO = new UserVO();
        User user = userService.getById(id);
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 分页查询（管理员）
     * @param userQueryRequest 查询请求参数
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest){
        // 校验
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取页面基本信息(后面page参数需要long类型)
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        /*
          @param current   起始页码
         * @param pageSize  页面大小
         * @param total 总共记录数
         */
        // 定义页面Page(long current, long pageSize, long total)
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
    //endregion

}
