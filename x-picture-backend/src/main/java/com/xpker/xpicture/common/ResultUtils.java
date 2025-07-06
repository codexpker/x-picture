package com.xpker.xpicture.common;

import com.xpker.xpicture.exception.ErrorCode;

/**
 * 返回工具类
 */
public class ResultUtils {
    /**
     * 成功返回
     * @param data 数据
     * @return 响应
     * @param <T> 数据类型
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data, ErrorCode.SUCCESS.getMessage());
    }

    /**
     * 失败返回
     * @param errorCode 错误码
     * @return 响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败返回
     * @param errorCode 错误码
     * @param message  错误信息
     * @return 响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message){
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

    /**
     * 失败返回
     * @param code 状态码
     * @param message 错误信息
     * @return 响应
     */
    public static BaseResponse<?> error(int code, String message){
        return new BaseResponse<>(code, null, message);
    }
}
