package com.mdmanager.vo;

import com.mdmanager.entity.Tag;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象
 *
 * <p>用于返回文章信息给前端，包含关联的标签信息</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容，Markdown格式
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * MD文件名
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者
     */
    private String author;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 关联的标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 关联的标签列表
     */
    private List<Tag> tags;

    /**
     * 标签名称字符串（逗号分隔）
     */
    private String tagNames;
}
