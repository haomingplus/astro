package com.mdmanager.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mdmanager.entity.User;
import com.mdmanager.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化组件
 *
 * <p>在应用启动时检查并创建默认管理员用户</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    /**
     * 默认管理员用户名
     */
    private static final String DEFAULT_ADMIN_USERNAME = "admin";

    /**
     * 默认管理员密码
     */
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    /**
     * 应用启动时执行
     *
     * @param args 命令行参数
     */
    @Override
    public void run(String... args) {
        initAdminUser();
    }

    /**
     * 初始化管理员用户
     *
     * <p>如果管理员用户不存在，则自动创建</p>
     */
    private void initAdminUser() {
        // 检查管理员用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, DEFAULT_ADMIN_USERNAME);
        User existUser = userMapper.selectOne(queryWrapper);

        if (existUser == null) {
            // 创建管理员用户
            User admin = new User();
            admin.setUsername(DEFAULT_ADMIN_USERNAME);
            // 使用BCrypt加密密码
            admin.setPassword(BCrypt.hashpw(DEFAULT_ADMIN_PASSWORD));
            admin.setNickname("系统管理员");
            admin.setStatus(1);
            admin.setIsDeleted(0);

            userMapper.insert(admin);
            log.info("====================================");
            log.info("  默认管理员用户创建成功！");
            log.info("  用户名: {}", DEFAULT_ADMIN_USERNAME);
            log.info("  密码: {}", DEFAULT_ADMIN_PASSWORD);
            log.info("====================================");
        } else {
            log.info("管理员用户已存在，跳过初始化");
        }
    }
}
