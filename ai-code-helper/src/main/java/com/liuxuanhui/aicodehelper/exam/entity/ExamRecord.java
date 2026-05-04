package com.liuxuanhui.aicodehelper.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "考试记录")
@TableName(value = "exam_record")
public class ExamRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 考试主键的id", example = "1")
    private Integer recordId;

    @Schema(description = "考试用户id", example = "1")
    private Integer userId;

    @Schema(description = "用户考试中的答案", example = "1")
    private String userAnswers;

    @Schema(description = "考试过程中的信用截图", example = "imgUrl")
    private String creditImgUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "考试时间", example = "2020-10-20")
    private Date examTime;

    @Schema(description = "考试逻辑得分", example = "69")
    private Integer logicScore;

    @Schema(description = "考试的id", example = "1")
    private Integer examId;

    @Schema(description = "考试的题目id", example = "1")
    private String questionIds;

    @Schema(description = "考试总得分", example = "100")
    private Integer totalScore;

    @Schema(description = "考试错题id", example = "1,2,3")
    private String errorQuestionIds;
}
