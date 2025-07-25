package com.xpker.xpicture.model.dto.picture;

import com.xpker.xpicture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;


    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 搜索词（同时搜索名称，简介等）
     */
    private String searchText;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 是否只查询spaceId为 null 的数据
     */
    private boolean nullSpaceId;

    /**
     * 审核状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 ID
     */
    private Long reviewerId;


    private static final long serialVersionUID = 1L;
}
