package com.liuxuanhui.aicodehelper.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 5, max = 20)
    private String password;

    private String trueName;
}
