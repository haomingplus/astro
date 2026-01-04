package com.mdmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdmanager.entity.Article;
import com.mdmanager.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章Mapper接口
 *
 * <p>继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作</p>
 * <p>同时定义了一些自定义的复杂查询方法</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询文章列表（包含标签信息）
     *
     * @param page    分页参数
     * @param keyword 搜索关键词（标题）
     * @param tagId   标签ID筛选
     * @param status  状态筛选
     * @return 文章分页列表
     */
    IPage<ArticleVO> selectArticlePageWithTags(
            Page<ArticleVO> page,
            @Param("keyword") String keyword,
            @Param("tagId") Long tagId,
            @Param("status") Integer status
    );

    /**
     * 查询文章详情（包含标签信息）
     *
     * @param id 文章ID
     * @return 文章详情VO
     */
    ArticleVO selectArticleDetailById(@Param("id") Long id);
}
