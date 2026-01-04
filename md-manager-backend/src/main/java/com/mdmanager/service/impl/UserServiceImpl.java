package com.mdmanager.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mdmanager.common.BusinessException;
import com.mdmanager.common.PageResult;
import com.mdmanager.common.ResultCode;
import com.mdmanager.dto.LoginDTO;
import com.mdmanager.dto.PasswordDTO;
import com.mdmanager.dto.UserDTO;
import com.mdmanager.dto.UserQueryDTO;
import com.mdmanager.entity.User;
import com.mdmanager.mapper.UserMapper;
import com.mdmanager.service.UserService;
import com.mdmanager.utils.JwtUtils;
import com.mdmanager.utils.RedisUtils;
import com.mdmanager.utils.UserContext;
import com.mdmanager.vo.LoginVO;
import com.mdmanager.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private RedisUtils redisUtils;

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

        // 保存用户会话到Redis
        redisUtils.saveUserSession(token, userVO);

        // 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(userVO);

        log.info("用户登录成功: {}", user.getUsername());
        return loginVO;
    }

    /**
     * 用户登出
     *
     * @param token 用户Token
     */
    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            // 从Redis中移除用户会话
            redisUtils.removeUserSession(token);
            log.info("用户登出成功");
        }
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

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询参数
     * @return 用户分页列表
     */
    @Override
    public PageResult<UserVO> getUserPage(UserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（用户名或昵称）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, queryDTO.getKeyword())
                    .or()
                    .like(User::getNickname, queryDTO.getKeyword()));
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(User::getCreateTime);

        // 分页查询
        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<User> userPage = this.page(page, queryWrapper);

        // 转换为VO列表
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    return userVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(userVOList, userPage.getTotal());
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO getUserDetail(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 新增用户
     *
     * @param userDTO 用户数据
     * @return 新增的用户ID
     */
    @Override
    public Long addUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        User existUser = getByUsername(userDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 密码必填验证
        if (!StringUtils.hasText(userDTO.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "密码不能为空");
        }

        // 构建用户实体
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 密码加密
        user.setPassword(BCrypt.hashpw(userDTO.getPassword()));

        // 设置默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        // 保存用户
        this.save(user);

        log.info("新增用户成功: {}", user.getUsername());
        return user.getId();
    }

    /**
     * 编辑用户
     *
     * @param id      用户ID
     * @param userDTO 用户数据
     */
    @Override
    public void updateUser(Long id, UserDTO userDTO) {
        // 检查用户是否存在
        User existUser = this.getById(id);
        if (existUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 如果修改了用户名，检查新用户名是否已存在
        if (!existUser.getUsername().equals(userDTO.getUsername())) {
            User sameNameUser = getByUsername(userDTO.getUsername());
            if (sameNameUser != null) {
                throw new BusinessException(ResultCode.USERNAME_EXISTS);
            }
        }

        // 构建更新实体
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setUsername(userDTO.getUsername());
        updateUser.setNickname(userDTO.getNickname());
        updateUser.setEmail(userDTO.getEmail());
        updateUser.setAvatar(userDTO.getAvatar());
        updateUser.setStatus(userDTO.getStatus());

        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            updateUser.setPassword(BCrypt.hashpw(userDTO.getPassword()));
        }

        // 更新用户
        this.updateById(updateUser);

        log.info("编辑用户成功: {}", userDTO.getUsername());
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Override
    public void deleteUser(Long id) {
        // 检查用户是否存在
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 不能删除自己
        Long currentUserId = UserContext.getUserId();
        if (id.equals(currentUserId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "不能删除自己的账号");
        }

        // 删除用户
        this.removeById(id);

        log.info("删除用户成功: {}", user.getUsername());
    }

    /**
     * 修改密码
     *
     * @param passwordDTO 密码数据
     */
    @Override
    public void changePassword(PasswordDTO passwordDTO) {
        // 获取当前用户
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 验证旧密码
        if (!BCrypt.checkpw(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "旧密码错误");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(BCrypt.hashpw(passwordDTO.getNewPassword()));
        this.updateById(updateUser);

        log.info("用户修改密码成功: {}", user.getUsername());
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 新密码
     */
    @Override
    public String resetPassword(Long id) {
        // 检查用户是否存在
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 生成随机密码（8位）
        String newPassword = RandomUtil.randomString(8);

        // 更新密码
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setPassword(BCrypt.hashpw(newPassword));
        this.updateById(updateUser);

        log.info("重置用户密码成功: {}", user.getUsername());
        return newPassword;
    }
}
