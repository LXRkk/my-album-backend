package com.lxrkk.myalbumbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传请求
 *
 * @author : LXRkk
 * @date : 2025/7/22 16:01
 */
@Data
public class PictureUploadRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 图片 id（用于修改）
     */
    private Long id;

    /**
     * 文件地址
     */

    private String fileUrl;
    /**
     * 图片名称
     */
    private String picName;

    /**
     * 空间 id
     */
    private Long spaceId;

}
