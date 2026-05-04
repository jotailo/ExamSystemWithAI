package com.liuxuanhui.aicodehelper.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "题库实体")
@TableName(value = "question_bank")
public class QuestionBank {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 题库id", example = "1")
    private Integer bankId;

    @Schema(description = "题库名称", example = "小学数学")
    private String bankName;
}
