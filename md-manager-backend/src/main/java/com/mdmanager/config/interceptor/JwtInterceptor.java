package com.mdmanager.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdmanager.common.Result;
import com.mdmanager.common.ResultCode;
import com.mdmanager.config.JwtConfig;
import com.mdmanager.utils.JwtUtils;
import com.mdmanager.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * JWT 认证拦截器
 *
 * <p>拦截需要认证的请求，验证JWT Token的有效性</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 请求处理前执行
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @return true-继续执行，false-中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行（CORS预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取Token
        String authHeader = request.getHeader(jwtConfig.getHeader());

        // 提取Token
        String token = jwtUtils.extractToken(authHeader);

        // 验证Token
        if (token == null || !jwtUtils.validateToken(token)) {
            log.warn("Token验证失败: {}", request.getRequestURI());
            writeErrorResponse(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 获取用户信息并存储到上下文
        Long userId = jwtUtils.getUserIdFromToken(token);
        String username = jwtUtils.getUsernameFromToken(token);

        if (userId == null || username == null) {
            log.warn("无法从Token中获取用户信息: {}", request.getRequestURI());
            writeErrorResponse(response, ResultCode.TOKEN_INVALID);
            return false;
        }

        // 将用户信息存储到ThreadLocal中，供后续使用
        UserContext.setUserId(userId);
        UserContext.setUsername(username);

        return true;
    }

    /**
     * 请求完成后执行（清理资源）
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @param ex       异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }

    /**
     * 写入错误响应
     *
     * @param response   HTTP响应
     * @param resultCode 错误码
     */
    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(resultCode);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
