package com.mdmanager.controller;

import com.mdmanager.common.Result;
import com.mdmanager.dto.TagDTO;
import com.mdmanager.entity.Tag;
import com.mdmanager.service.TagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 *
 * <p>处理标签的增删改查请求</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 查询标签列表
     *
     * @param keyword 搜索关键词（可选）
     * @return 标签列表
     */
    @GetMapping("/list")
    public Result<List<Tag>> getTagList(@RequestParam(required = false) String keyword) {
        log.info("查询标签列表, keyword: {}", keyword);
        List<Tag> tags = tagService.getTagList(keyword);
        return Result.success(tags);
    }

    /**
     * 获取标签详情
     *
     * @param id 标签ID
     * @return 标签详情
     */
    @GetMapping("/{id}")
    public Result<Tag> getTagDetail(@PathVariable Long id) {
        log.info("查询标签详情, ID: {}", id);
        Tag tag = tagService.getById(id);
        return Result.success(tag);
    }

    /**
     * 新增标签
     *
     * @param tagDTO 标签数据
     * @return 新增结果
     */
    @PostMapping
    public Result<Tag> addTag(@Valid @RequestBody TagDTO tagDTO) {
        log.info("新增标签: {}", tagDTO.getName());
        Tag tag = tagService.addTag(tagDTO);
        return Result.success("新增成功", tag);
    }

    /**
     * 编辑标签
     *
     * @param id     标签ID
     * @param tagDTO 标签数据
     * @return 编辑结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateTag(@PathVariable Long id, @Valid @RequestBody TagDTO tagDTO) {
        log.info("编辑标签, ID: {}, 名称: {}", id, tagDTO.getName());
        tagService.updateTag(id, tagDTO);
        return Result.success("编辑成功", null);
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        log.info("删除标签, ID: {}", id);
        tagService.deleteTag(id);
        return Result.success("删除成功", null);
    }
}
