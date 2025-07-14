package com.xpker.xpicture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

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
    private static final long serialVersionUID = 1L;
}
