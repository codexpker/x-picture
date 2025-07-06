package com.xpker.xpicture.common;

import lombok.Data;

import java.io.Serializable;


/**
 * 删除请求通用类
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    private static final long serialVersionUID = 1L;
}
