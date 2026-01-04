package com.mdmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mdmanager.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签Mapper接口
 *
 * <p>继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询所有标签（包含文章数量）
     *
     * @param keyword 搜索关键词
     * @return 标签列表
     */
    List<Tag> selectTagsWithArticleCount(@Param("keyword") String keyword);

    /**
     * 根据文章ID查询关联的标签列表
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    @Select("SELECT t.* FROM t_tag t " +
            "INNER JOIN t_article_tag at ON t.id = at.tag_id " +
            "WHERE at.article_id = #{articleId} AND t.is_deleted = 0")
    List<Tag> selectTagsByArticleId(@Param("articleId") Long articleId);
}
