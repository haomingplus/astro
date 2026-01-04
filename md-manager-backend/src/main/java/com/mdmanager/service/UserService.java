package com.mdmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mdmanager.dto.LoginDTO;
import com.mdmanager.entity.User;
import com.mdmanager.vo.LoginVO;
import com.mdmanager.vo.UserVO;

/**
 * 用户服务接口
 *
 * <p>定义用户相关的业务操作方法</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应（包含Token和用户信息）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息VO
     */
    UserVO getCurrentUserInfo();

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);
}
