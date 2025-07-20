package com.lxrkk.myalbumbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求包装类
 *
 * @author : LXRkk
 * @date : 2025/7/19 16:34
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}
