package com.mdmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签请求数据传输对象
 *
 * <p>用于接收前端新增/编辑标签的请求参数</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class TagDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /**
     * 标签别名，用于URL（可选）
     */
    private String slug;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 排序序号
     */
    private Integer sortOrder;
}
