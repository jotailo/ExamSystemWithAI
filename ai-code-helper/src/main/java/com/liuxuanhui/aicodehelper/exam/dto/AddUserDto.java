package com.liuxuanhui.aicodehelper.exam.dto;

import com.liuxuanhui.aicodehelper.exam.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
@Builder
public class AddUserDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Integer roleId;

    private String trueName;

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
