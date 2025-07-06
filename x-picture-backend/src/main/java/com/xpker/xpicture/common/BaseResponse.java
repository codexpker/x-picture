package com.xpker.xpicture.common;

import com.xpker.xpicture.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共响应类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    int code;
    T data;
    String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 操作失败（操作成功，错误码为0）
     * @param errorCode 错误码
     */

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
