package com.liuxuanhui.aicodehelper.exam.controller;

import com.liuxuanhui.aicodehelper.exam.dto.AddUserDto;
import com.liuxuanhui.aicodehelper.exam.entity.Notice;
import com.liuxuanhui.aicodehelper.exam.entity.UserRole;
import com.liuxuanhui.aicodehelper.exam.service.NoticeService;
import com.liuxuanhui.aicodehelper.exam.service.UserRoleService;
import com.liuxuanhui.aicodehelper.exam.service.UserService;
import com.liuxuanhui.aicodehelper.exam.vo.CommonResult;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "超级管理员权限相关的接口")
@RequestMapping(value = {"/admin", "/exam/admin"})
public class AdminController {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final NoticeService noticeService;

    @GetMapping("/getUser")
    @Operation(summary = "获取用户信息,可分页")
    public CommonResult<PageResponse<UserInfoVo>> getUser(@RequestParam(required = false) String loginName,
                                                          @RequestParam(required = false) String trueName,
                                                          Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<UserInfoVo>>builder()
                .data(userService.getUser(loginName, trueName, pageNo, pageSize))
                .build();
    }

    @GetMapping("/handleUser/{type}")
    @Operation(summary = "管理员操作用户: type=1(启用) 2(禁用) 3(删除)")
    public CommonResult<Void> handleUser(@PathVariable("type") Integer type, String userIds) {
        userService.handlerUser(type, userIds);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/addUser")
    @Operation(summary = "管理员新增用户")
    public CommonResult<Void> addUser(@RequestBody @Validated AddUserDto userDto) {
        userService.addUser(userDto);
        return CommonResult.<Void>builder().build();
    }

    @GetMapping("/getRole")
    @Operation(summary = "查询系统存在的所有角色信息")
    public CommonResult<List<UserRole>> getRole() {
        return CommonResult.<List<UserRole>>builder().data(userRoleService.getUserRole()).build();
    }

    @GetMapping("/getAllNotice")
    @Operation(summary = "获取系统发布的所有公告(分页 条件查询)")
    public CommonResult<PageResponse<Notice>> getAllNotice(@RequestParam(required = false, name = "noticeContent") String content,
                                                           Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<Notice>>builder()
                .data(noticeService.getAllNotices(content, pageNo, pageSize))
                .build();
    }

    @PostMapping("/publishNotice")
    @Operation(summary = "发布新公告")
    public CommonResult<Void> publishNotice(@RequestBody Notice notice) {
        noticeService.publishNotice(notice);
        return CommonResult.<Void>builder().build();
    }

    @GetMapping("/deleteNotice")
    @Operation(summary = "批量删除公告")
    public CommonResult<Void> deleteNotice(@RequestParam(name = "ids") String noticeIds) {
        noticeService.deleteNoticeByIds(noticeIds);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/updateNotice")
    @Operation(summary = "更新公告")
    public CommonResult<Void> updateNotice(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return CommonResult.<Void>builder().build();
    }
}
