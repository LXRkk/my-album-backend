package com.lxrkk.myalbumbackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 常用的标签、分类
 *
 * @author : LXRkk
 * @date : 2025/7/22 22:15
 */
@Data
public class PictureTagCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 常用标签列表
     */
    private List<String> tagList;

    /**
     * 常用分类列表
     */
    private List<String> categoryList;
}
