package com.liuxuanhui.aicodehelper.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "角色实体")
@TableName(value = "user_role")
public class UserRole implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 id", example = "1")
    private Integer id;

    @Schema(description = "角色id", example = "1(学生) 2(教师) 3(管理员)")
    private Integer roleId;

    @Schema(description = "用户角色名称", example = "1(学生) 2(教师) 3(管理员)")
    private String roleName;

    @Schema(description = "权限对应的功能菜单", example = "json串")
    private String menuInfo;
}
