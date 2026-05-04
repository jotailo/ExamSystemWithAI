package com.liuxuanhui.aicodehelper.exam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserInfoDto {

    @NotBlank
    private String username;

    private String password;

    private String trueName;
}
