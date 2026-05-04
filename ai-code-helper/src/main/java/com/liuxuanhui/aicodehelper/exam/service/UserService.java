package com.liuxuanhui.aicodehelper.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxuanhui.aicodehelper.exam.dto.AddUserDto;
import com.liuxuanhui.aicodehelper.exam.dto.LoginDto;
import com.liuxuanhui.aicodehelper.exam.dto.RegisterDto;
import com.liuxuanhui.aicodehelper.exam.dto.UpdateUserInfoDto;
import com.liuxuanhui.aicodehelper.exam.entity.User;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.UserInfoVo;

import java.util.List;

public interface UserService extends IService<User> {

    String register(RegisterDto registerDto);

    Boolean checkUsername(String username);

    String login(LoginDto loginDto);

    User getUserByUsername(String username);

    User updateUserInfo(UpdateUserInfoDto updateUserInfoDto);

    PageResponse<UserInfoVo> getUser(String loginName, String trueName, Integer pageNo, Integer pageSize);

    void handlerUser(Integer type, String userIds);

    void addUser(AddUserDto addUserDto);

    UserInfoVo getUserInfoById(Integer userId);

    List<UserInfoVo> getUserInfoByIds(List<Integer> userIds);
}
