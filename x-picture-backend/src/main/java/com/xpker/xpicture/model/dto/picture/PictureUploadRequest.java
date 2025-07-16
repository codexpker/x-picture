package com.xpker.xpicture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片上传请求
 */
@Data
public class PictureUploadRequest implements Serializable {
    /**
     * 图片id（用于修改）
     */
    private Long id;

    /**
     * 文件地址(支持文件下载)
     */
    private String fileUrl;

    /**
     * 图片名称(批量抓取统一名字)
     */
    private String picName;

    /**
     * 分类(批量抓取可指定默认分类)
     */
    private String category;

    /**
     * 标签（批量抓取可指定默认标签）
     */
    private List<String> tags;
    private static final long serialVersionUID = 1L;
}
