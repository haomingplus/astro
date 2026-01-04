package com.mdmanager.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mdmanager.common.BusinessException;
import com.mdmanager.common.ResultCode;
import com.mdmanager.dto.LoginDTO;
import com.mdmanager.entity.User;
import com.mdmanager.mapper.UserMapper;
import com.mdmanager.service.UserService;
import com.mdmanager.utils.JwtUtils;
import com.mdmanager.utils.UserContext;
import com.mdmanager.vo.LoginVO;
import com.mdmanager.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * <p>实现用户相关的业务逻辑</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应（包含Token和用户信息）
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 根据用户名查询用户
        User user = getByUsername(loginDTO.getUsername());

        // 用户不存在
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 验证密码（使用BCrypt验证）
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            log.warn("登录失败，密码错误: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            log.warn("登录失败，用户已被禁用: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 更新最后登录时间
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(LocalDateTime.now());
        this.updateById(updateUser);

        // 生成JWT Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 构建用户信息VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(userVO);

        log.info("用户登录成功: {}", user.getUsername());
        return loginVO;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息VO
     */
    @Override
    public UserVO getCurrentUserInfo() {
        // 从上下文获取当前用户ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 查询用户信息
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 转换为VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return this.getOne(queryWrapper);
    }
}
