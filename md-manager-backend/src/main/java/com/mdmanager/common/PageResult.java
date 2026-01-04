package com.mdmanager.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果类
 *
 * <p>用于封装分页查询的结果数据</p>
 *
 * @param <T> 数据列表元素类型
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 私有构造方法
     */
    private PageResult() {
    }

    /**
     * 创建分页结果
     *
     * @param list     数据列表
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param <T>      数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        // 计算总页数
        result.setPages((int) Math.ceil((double) total / pageSize));
        return result;
    }

    /**
     * 创建空的分页结果
     *
     * @param <T> 数据类型
     * @return 空的分页结果对象
     */
    public static <T> PageResult<T> empty() {
        PageResult<T> result = new PageResult<>();
        result.setList(List.of());
        result.setTotal(0L);
        result.setPageNum(1);
        result.setPageSize(10);
        result.setPages(0);
        return result;
    }
}
