package com.mdmanager.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章查询请求数据传输对象
 *
 * <p>用于接收前端文章列表查询的请求参数</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class ArticleQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码，默认第1页
     */
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10条
     */
    private Integer pageSize = 10;

    /**
     * 搜索关键词（标题）
     */
    private String keyword;

    /**
     * 标签ID筛选
     */
    private Long tagId;

    /**
     * 状态筛选：0-草稿，1-已发布
     */
    private Integer status;
}
