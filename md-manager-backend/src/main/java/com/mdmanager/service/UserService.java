package com.mdmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mdmanager.common.PageResult;
import com.mdmanager.dto.LoginDTO;
import com.mdmanager.dto.PasswordDTO;
import com.mdmanager.dto.UserDTO;
import com.mdmanager.dto.UserQueryDTO;
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
     * 用户登出
     *
     * @param token 用户Token
     */
    void logout(String token);

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

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询参数
     * @return 用户分页列表
     */
    PageResult<UserVO> getUserPage(UserQueryDTO queryDTO);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO getUserDetail(Long id);

    /**
     * 新增用户
     *
     * @param userDTO 用户数据
     * @return 新增的用户ID
     */
    Long addUser(UserDTO userDTO);

    /**
     * 编辑用户
     *
     * @param id      用户ID
     * @param userDTO 用户数据
     */
    void updateUser(Long id, UserDTO userDTO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     *
     * @param passwordDTO 密码数据
     */
    void changePassword(PasswordDTO passwordDTO);

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 新密码
     */
    String resetPassword(Long id);
}
