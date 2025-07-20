package com.lxrkk.myalbumbackend.common;

import com.lxrkk.myalbumbackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一的响应结果类
 *  返回结果包括
 *      调用码 code
 *      数据 data
 *      调用信息 message
 *
 * @author : LXRkk
 * @date : 2025/7/19 16:03
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
