package com.lxrkk.myalbumbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员审核图片请求
 *
 * @author : LXRkk
 * @date : 2025/7/23 15:05
 */
@Data
public class PictureReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}
