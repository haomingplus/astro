package com.mdmanager.controller;

import com.mdmanager.common.Result;
import com.mdmanager.dto.LoginDTO;
import com.mdmanager.service.UserService;
import com.mdmanager.vo.LoginVO;
import com.mdmanager.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * <p>处理用户登录、登出、获取用户信息等认证相关请求</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、登出、获取用户信息等接口")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录响应（包含Token和用户信息）
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: {}", loginDTO.getUsername());
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        UserVO userVO = userService.getCurrentUserInfo();
        return Result.success(userVO);
    }

    /**
     * 用户登出
     *
     * @param request HTTP请求对象
     * @return 登出结果
     */
    @Operation(summary = "用户登出", description = "退出登录，清除用户会话")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        // 从请求头获取Token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 调用登出服务，清除Redis中的用户会话
        userService.logout(token);
        log.info("用户登出");
        return Result.success("退出成功", null);
    }
}
