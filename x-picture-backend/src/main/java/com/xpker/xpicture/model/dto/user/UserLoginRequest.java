package com.xpker.xpicture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */

@Data
public class UserLoginRequest implements Serializable {


    /**
     * 用户账号
     */
    private String UserAccount;
    /**
     * 用户密码
     */
    private String UserPassword;

    private static final long serialVersionUID = 1L;
}
