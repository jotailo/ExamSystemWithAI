package com.liuxuanhui.aicodehelper.exam.controller;

import com.liuxuanhui.aicodehelper.exam.dto.LoginDto;
import com.liuxuanhui.aicodehelper.exam.dto.RegisterDto;
import com.liuxuanhui.aicodehelper.exam.dto.UpdateUserInfoDto;
import com.liuxuanhui.aicodehelper.exam.service.impl.UserRoleServiceImpl;
import com.liuxuanhui.aicodehelper.exam.service.impl.UserServiceImpl;
import com.liuxuanhui.aicodehelper.exam.utils.JwtUtils;
import com.liuxuanhui.aicodehelper.exam.vo.CommonResult;
import com.liuxuanhui.aicodehelper.exam.vo.TokenVo;
import com.liuxuanhui.aicodehelper.exam.vo.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.liuxuanhui.aicodehelper.exam.vo.UserVo.fromUser;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/common", "/exam/common"})
@Tag(name = "(学生,教师,管理员)通用相关接口")
public class CommonController {

    private final UserServiceImpl userService;
    private final UserRoleServiceImpl userRoleService;

    @RequestMapping("/error")
    public CommonResult<String> error() {
        return CommonResult.<String>builder().code(233).message("请求错误!").build();
    }

    @Operation(summary = "用户注册接口")
    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody @Valid RegisterDto registerDto) {
        return CommonResult.<String>builder().data(userService.register(registerDto)).build();
    }

    @Operation(summary = "用户名合法查询接口")
    @GetMapping("/check/{username}")
    public CommonResult<Boolean> checkUsername(@PathVariable(value = "username") String username) {
        return CommonResult.<Boolean>builder().data(userService.checkUsername(username)).build();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录接口")
    public CommonResult<String> login(@RequestBody @Valid LoginDto loginDto) {
        return CommonResult.<String>builder().data(userService.login(loginDto)).build();
    }

    @GetMapping("/getMenu")
    @Operation(summary = "获取不同用户的权限菜单")
    public CommonResult<String> getMenu(HttpServletRequest request) {
        return CommonResult.<String>builder()
                .data(userRoleService.getMenuInfo(JwtUtils.getUserInfoByToken(request).getRoleId()))
                .build();
    }

    @GetMapping("/checkToken")
    @Operation(summary = "验证用户token接口")
    public CommonResult<TokenVo> checkToken(HttpServletRequest request) {
        return CommonResult.<TokenVo>builder().data(JwtUtils.getUserInfoByToken(request)).build();
    }

    @GetMapping("/getCurrentUser")
    @Operation(summary = "供给普通用户查询个人信息使用")
    public CommonResult<UserVo> getCurrentUser(HttpServletRequest request) {
        return CommonResult.<UserVo>builder()
                .data(fromUser(userService.getUserByUsername(JwtUtils.getUserInfoByToken(request).getUsername())))
                .build();
    }

    @PostMapping("/updateCurrentUser")
    @Operation(summary = "供给用户更改个人信息")
    public CommonResult<Object> updateCurrentUser(@RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
        userService.updateUserInfo(updateUserInfoDto);
        return CommonResult.builder().build();
    }
}
