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
@Schema(description = "考试里的题目实体")
@TableName(value = "exam_question")
public class ExamQuestion implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 考试题目表的id", example = "1")
    private Integer id;

    @Schema(description = "问题的id字符串", example = "1,2,3")
    private String questionIds;

    @Schema(description = "考试的id", example = "1")
    private Integer examId;

    @Schema(description = "考试中每一题的分数", example = "1,2,3")
    private String scores;
}
