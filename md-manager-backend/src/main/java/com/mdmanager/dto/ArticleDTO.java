package com.mdmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章请求数据传输对象
 *
 * <p>用于接收前端新增/编辑文章的请求参数</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class ArticleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    private String title;

    /**
     * 文章内容，Markdown格式
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /**
     * 文章摘要（可选，为空时自动从内容截取）
     */
    private String summary;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者
     */
    private String author;

    /**
     * 关联的标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;
}
