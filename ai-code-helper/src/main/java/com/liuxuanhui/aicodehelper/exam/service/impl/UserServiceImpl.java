package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.dto.AddUserDto;
import com.liuxuanhui.aicodehelper.exam.dto.LoginDto;
import com.liuxuanhui.aicodehelper.exam.dto.RegisterDto;
import com.liuxuanhui.aicodehelper.exam.dto.UpdateUserInfoDto;
import com.liuxuanhui.aicodehelper.exam.entity.User;
import com.liuxuanhui.aicodehelper.exam.exception.BusinessException;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.mapper.UserMapper;
import com.liuxuanhui.aicodehelper.exam.service.UserService;
import com.liuxuanhui.aicodehelper.exam.utils.JwtUtils;
import com.liuxuanhui.aicodehelper.exam.utils.SaltEncryption;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.UserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.liuxuanhui.aicodehelper.exam.utils.CommonUtils.setLikeWrapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public String register(RegisterDto registerDto) {
        if (!checkUsername(registerDto.getUsername())) {
            throw new BusinessException(CommonErrorCode.E_100103);
        }
        String salt = UUID.randomUUID().toString().substring(0, 6);
        String newPwd = SaltEncryption.saltEncryption(registerDto.getPassword(), salt);

        User user = new User();
        BeanUtils.copyProperties(registerDto, user);
        user.setPassword(newPwd);
        user.setSalt(salt);
        user.setRoleId(1);
        user.setStatus(1);
        user.setCreateDate(new Date());

        userMapper.insert(user);
        return JwtUtils.createToken(user);
    }

    @Override
    public Boolean checkUsername(String username) {
        return userMapper.selectCount(new QueryWrapper<User>().eq("username", username)) < 1;
    }

    @Override
    public String login(LoginDto loginDto) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        if (user == null) {
            throw new BusinessException(CommonErrorCode.E_100102);
        }
        String saltPassword = SaltEncryption.saltEncryption(loginDto.getPassword(), user.getSalt());
        if (saltPassword.equals(user.getPassword()) && user.getStatus() == 1) {
            return JwtUtils.createToken(user);
        } else {
            throw new BusinessException(CommonErrorCode.E_100101);
        }
    }

    @Override
    @Cache(prefix = "user", suffix = "#username", ttl = 10, randomTime = 2, timeUnit = TimeUnit.HOURS)
    public User getUserByUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    @Cache(prefix = "user", suffix = "#updateUserInfoDto.getUsername()", ttl = 10, randomTime = 2, timeUnit = TimeUnit.HOURS, resetCache = true)
    public User updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
        User user = getUserByUsername(updateUserInfoDto.getUsername());
        user.updateFrom(updateUserInfoDto);
        userMapper.updateById(user);
        return user;
    }

    @Override
    public PageResponse<UserInfoVo> getUser(String loginName, String trueName, Integer pageNo, Integer pageSize) {
        IPage<User> userPage = new Page<>(pageNo, pageSize);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("username", loginName);
        queryParams.put("true_name", trueName);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        setLikeWrapper(wrapper, queryParams);
        wrapper.orderByDesc("role_id", "create_date");
        wrapper.orderByAsc("status");

        userPage = userMapper.selectPage(userPage, wrapper);
        List<UserInfoVo> records = userPage.getRecords().stream()
                .map(UserInfoVo::fromUser)
                .collect(Collectors.toList());

        return PageResponse.<UserInfoVo>builder().data(records).total(userPage.getTotal()).build();
    }

    @Override
    public void handlerUser(Integer type, String userIds) {
        String[] ids = userIds.split(",");
        switch (type) {
            case 1 -> updateUserStatus(ids, 1);
            case 2 -> updateUserStatus(ids, 2);
            case 3 -> {
                for (String id : ids) {
                    userMapper.deleteById(Integer.parseInt(id));
                }
            }
            default -> throw new BusinessException(CommonErrorCode.E_100105);
        }
    }

    @Override
    public void addUser(AddUserDto addUserDto) {
        String salt = UUID.randomUUID().toString().substring(0, 6);
        String newPwd = SaltEncryption.saltEncryption(addUserDto.getPassword(), salt);
        User user = addUserDto.toUser();
        user.setPassword(newPwd);
        user.setSalt(salt);
        user.setStatus(1);
        user.setCreateDate(new Date());
        userMapper.insert(user);
    }

    @Override
    public UserInfoVo getUserInfoById(Integer userId) {
        return UserInfoVo.fromUser(userMapper.selectById(userId));
    }

    @Override
    public List<UserInfoVo> getUserInfoByIds(List<Integer> userIds) {
        return userMapper.selectBatchIds(userIds).stream()
                .map(UserInfoVo::fromUser)
                .collect(Collectors.toList());
    }

    private void updateUserStatus(String[] ids, Integer status) {
        for (String id : ids) {
            User user = userMapper.selectById(Integer.parseInt(id));
            user.setStatus(status);
            userMapper.updateById(user);
        }
    }
}
