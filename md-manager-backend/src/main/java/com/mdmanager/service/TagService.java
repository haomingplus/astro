package com.mdmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mdmanager.dto.TagDTO;
import com.mdmanager.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 *
 * <p>定义标签相关的业务操作方法</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
public interface TagService extends IService<Tag> {

    /**
     * 查询标签列表（包含文章数量）
     *
     * @param keyword 搜索关键词
     * @return 标签列表
     */
    List<Tag> getTagList(String keyword);

    /**
     * 新增标签
     *
     * @param tagDTO 标签数据
     * @return 新增的标签
     */
    Tag addTag(TagDTO tagDTO);

    /**
     * 编辑标签
     *
     * @param id     标签ID
     * @param tagDTO 标签数据
     */
    void updateTag(Long id, TagDTO tagDTO);

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    void deleteTag(Long id);
}
