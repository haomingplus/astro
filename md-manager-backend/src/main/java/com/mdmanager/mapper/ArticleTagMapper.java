package com.mdmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mdmanager.entity.ArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章标签关联Mapper接口
 *
 * <p>继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /**
     * 根据文章ID删除所有关联关系
     *
     * @param articleId 文章ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM t_article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据标签ID删除所有关联关系
     *
     * @param tagId 标签ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM t_article_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(@Param("tagId") Long tagId);
}
