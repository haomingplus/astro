package com.mdmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mdmanager.common.BusinessException;
import com.mdmanager.common.ResultCode;
import com.mdmanager.dto.TagDTO;
import com.mdmanager.entity.Tag;
import com.mdmanager.mapper.ArticleTagMapper;
import com.mdmanager.mapper.TagMapper;
import com.mdmanager.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 *
 * <p>实现标签相关的业务逻辑</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    /**
     * 查询标签列表（包含文章数量）
     *
     * @param keyword 搜索关键词
     * @return 标签列表
     */
    @Override
    public List<Tag> getTagList(String keyword) {
        return baseMapper.selectTagsWithArticleCount(keyword);
    }

    /**
     * 新增标签
     *
     * @param tagDTO 标签数据
     * @return 新增的标签
     */
    @Override
    public Tag addTag(TagDTO tagDTO) {
        // 检查标签名是否已存在
        if (isTagNameExists(tagDTO.getName(), null)) {
            throw new BusinessException(ResultCode.TAG_NAME_EXISTS);
        }

        // 创建标签实体
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);

        // 如果没有设置slug，使用名称的拼音或小写
        if (StrUtil.isBlank(tag.getSlug())) {
            tag.setSlug(tagDTO.getName().toLowerCase().replace(" ", "-"));
        }

        // 如果没有设置排序，默认为0
        if (tag.getSortOrder() == null) {
            tag.setSortOrder(0);
        }

        // 保存标签
        this.save(tag);
        log.info("标签新增成功: {}", tag.getName());

        return tag;
    }

    /**
     * 编辑标签
     *
     * @param id     标签ID
     * @param tagDTO 标签数据
     */
    @Override
    public void updateTag(Long id, TagDTO tagDTO) {
        // 检查标签是否存在
        Tag existTag = this.getById(id);
        if (existTag == null) {
            throw new BusinessException(ResultCode.TAG_NOT_FOUND);
        }

        // 检查标签名是否已存在（排除自己）
        if (isTagNameExists(tagDTO.getName(), id)) {
            throw new BusinessException(ResultCode.TAG_NAME_EXISTS);
        }

        // 更新标签
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);
        tag.setId(id);

        this.updateById(tag);
        log.info("标签更新成功: {}", tag.getName());
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        // 检查标签是否存在
        Tag existTag = this.getById(id);
        if (existTag == null) {
            throw new BusinessException(ResultCode.TAG_NOT_FOUND);
        }

        // 删除标签与文章的关联关系
        articleTagMapper.deleteByTagId(id);

        // 逻辑删除标签
        this.removeById(id);
        log.info("标签删除成功: {}", existTag.getName());
    }

    /**
     * 检查标签名是否已存在
     *
     * @param name      标签名
     * @param excludeId 排除的标签ID（编辑时使用）
     * @return true-存在，false-不存在
     */
    private boolean isTagNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getName, name);
        if (excludeId != null) {
            queryWrapper.ne(Tag::getId, excludeId);
        }
        return this.count(queryWrapper) > 0;
    }
}
