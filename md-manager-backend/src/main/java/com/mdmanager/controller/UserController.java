package com.mdmanager.controller;

import com.mdmanager.common.PageResult;
import com.mdmanager.common.Result;
import com.mdmanager.dto.PasswordDTO;
import com.mdmanager.dto.UserDTO;
import com.mdmanager.dto.UserQueryDTO;
import com.mdmanager.service.UserService;
import com.mdmanager.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * <p>处理用户的增删改查等管理操作</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户的增删改查等管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询参数
     * @return 用户分页列表
     */
    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户列表")
    @GetMapping
    public Result<PageResult<UserVO>> getUserPage(UserQueryDTO queryDTO) {
        log.info("分页查询用户列表: {}", queryDTO);
        PageResult<UserVO> pageResult = userService.getUserPage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("获取用户详情: {}", id);
        UserVO userVO = userService.getUserDetail(id);
        return Result.success(userVO);
    }

    /**
     * 新增用户
     *
     * @param userDTO 用户数据
     * @return 新增的用户ID
     */
    @Operation(summary = "新增用户", description = "创建新用户")
    @PostMapping
    public Result<Long> addUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("新增用户: {}", userDTO.getUsername());
        Long userId = userService.addUser(userDTO);
        return Result.success("新增用户成功", userId);
    }

    /**
     * 编辑用户
     *
     * @param id      用户ID
     * @param userDTO 用户数据
     * @return 操作结果
     */
    @Operation(summary = "编辑用户", description = "根据用户ID更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("编辑用户: {}", id);
        userService.updateUser(id, userDTO);
        return Result.success("编辑用户成功", null);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("删除用户: {}", id);
        userService.deleteUser(id);
        return Result.success("删除用户成功", null);
    }

    /**
     * 修改密码
     *
     * @param passwordDTO 密码数据
     * @return 操作结果
     */
    @Operation(summary = "修改密码", description = "当前用户修改自己的密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        log.info("修改密码");
        userService.changePassword(passwordDTO);
        return Result.success("修改密码成功", null);
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 新密码
     */
    @Operation(summary = "重置用户密码", description = "管理员重置指定用户的密码")
    @PostMapping("/{id}/reset-password")
    public Result<String> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("重置用户密码: {}", id);
        String newPassword = userService.resetPassword(id);
        return Result.success("密码重置成功", newPassword);
    }
}
