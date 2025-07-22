package com.lxrkk.myalbumbackend.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片解析信息包装类
 *
 * @author : LXRkk
 * @date : 2025/7/22 16:24
 */
@Data
public class UploadPictureResult implements Serializable {

    /**
     * 图片地址
     */
    private String url;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private int picWidth;

    /**
     * 图片高度
     */
    private int picHeight;

    /**
     * 图片宽高比
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    private static final long serialVersionUID = 1L;
}
