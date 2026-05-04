package com.liuxuanhui.aicodehelper.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "题目实体")
@TableName(value = "question")
public class Question {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 题目id", example = "1")
    private Integer id;

    @Schema(description = "问题内容", example = "1+1等于几")
    private String quContent;

    @Schema(description = "创建时间", example = "2020-10-24 14:58")
    private Date createTime;

    @Schema(description = "创建人的username", example = "wzz")
    private String createPerson;

    @Schema(description = "问题类型", example = " 1单选 2多选 3判断 4简答")
    private Integer quType;

    @Schema(description = "问题难度", example = "1")
    private Integer level;

    @Schema(description = "问题相关的图片", example = "imageUrl")
    private String image;

    @Schema(description = "所属题库的id", example = "1,2")
    private String quBankId;

    @Schema(description = "所属题库的名称", example = "小学题库")
    private String quBankName;

    @Schema(description = "题目解析", example = "题目解析")
    private String analysis;
}
