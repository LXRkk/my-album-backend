package com.lxrkk.myalbumbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 注释
 *
 * @author : LXRkk
 * @date : 2025/7/23 20:22
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    private  static final long serialVersionUID = 1L;

    /**
     * 图片搜索关键词
     */
    private String keywords;

    /**
     * 抓取数量
     */
    private Integer count;

    /**
     * 给图片一个命名前缀
     */
    private String namePrefix;

}
