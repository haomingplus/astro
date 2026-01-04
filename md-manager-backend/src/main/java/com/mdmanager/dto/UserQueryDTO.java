package com.mdmanager.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求数据传输对象
 *
 * <p>用于接收前端用户列表查询的请求参数</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Data
public class UserQueryDTO implements Serializable {

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
     * 搜索关键词（用户名/昵称）
     */
    private String keyword;

    /**
     * 状态筛选：0-禁用，1-启用
     */
    private Integer status;
}
