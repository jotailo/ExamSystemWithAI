package com.liuxuanhui.aicodehelper.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liuxuanhui.aicodehelper.exam.dto.UpdateUserInfoDto;
import com.liuxuanhui.aicodehelper.exam.utils.SaltEncryption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户实体")
@TableName(value = "user")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 用户id", example = "1")
    private Integer id;

    @Schema(description = "用户角色id", example = "1(学生) 2(教师) 3(管理员)")
    private Integer roleId;

    @Schema(description = "登录用户名", example = "wzz")
    private String username;

    @Schema(description = "真实姓名", example = "wzz")
    private String trueName;

    @Schema(description = "密码", example = "12345")
    private String password;

    @Schema(description = "加密盐值", example = "salt")
    private String salt;

    @Schema(description = "用户状态", example = "1正常 2禁用")
    private Integer status;

    @Schema(description = "用户创建时间", example = "2020-10-22 10:35:44")
    private Date createDate;

    public void updateFrom(UpdateUserInfoDto updateUserInfoDto) {
        if (StringUtils.hasLength(updateUserInfoDto.getPassword())) {
            String newPwd = SaltEncryption.saltEncryption(updateUserInfoDto.getPassword(), this.getSalt());
            this.setPassword(newPwd);
        }
        if (StringUtils.hasLength(updateUserInfoDto.getTrueName())) {
            this.setTrueName(updateUserInfoDto.getTrueName());
        }
    }
}
