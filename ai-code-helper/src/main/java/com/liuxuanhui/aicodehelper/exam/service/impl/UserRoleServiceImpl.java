package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.entity.UserRole;
import com.liuxuanhui.aicodehelper.exam.mapper.UserRoleMapper;
import com.liuxuanhui.aicodehelper.exam.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    private final UserRoleMapper userRoleMapper;

    @Override
    @Cache(prefix = "menu", suffix = "#roleId", ttl = 60 * 60 * 24, randomTime = 60 * 60 * 10)
    public String getMenuInfo(Integer roleId) {
        return userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("role_id", roleId)).getMenuInfo();
    }

    @Override
    @Cache(prefix = "userRoles", ttl = 30, randomTime = 10, timeUnit = TimeUnit.DAYS)
    public List<UserRole> getUserRole() {
        return userRoleMapper.selectList(null);
    }
}
