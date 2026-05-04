package com.liuxuanhui.aicodehelper.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "系统公告实体")
@TableName(value = "notice")
public class Notice implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 公告id", example = "1")
    private Integer nId;

    @Schema(description = "公告内容(Html片段)", example = "1. 修复系统BUG")
    private String content;

    @Schema(description = "公告创建时间", example = "2020-10-22 10:35:44")
    private Date createTime;

    @Schema(description = "公告修改时间", example = "2020-10-22 10:35:44")
    private Date updateTime;

    @Schema(description = "公告状态", example = "0(不是当前系统公告) 1(是当前系统公告)")
    private Integer status;
}
