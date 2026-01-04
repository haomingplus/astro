package com.mdmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mdmanager.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * <p>继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 继承BaseMapper后，自动拥有以下方法：
    // - insert(T entity): 插入一条记录
    // - deleteById(Serializable id): 根据ID删除
    // - updateById(T entity): 根据ID更新
    // - selectById(Serializable id): 根据ID查询
    // - selectList(Wrapper<T> queryWrapper): 条件查询列表
    // - selectPage(Page<T> page, Wrapper<T> queryWrapper): 分页查询
    // 等更多方法...

}
