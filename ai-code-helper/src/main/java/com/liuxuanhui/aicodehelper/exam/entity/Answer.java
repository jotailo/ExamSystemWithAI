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
@Schema(description = "答案表实体")
@TableName(value = "answer")
public class Answer implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 答案id", example = "1")
    private Integer id;

    @Schema(description = "所有的选项信息", example = "2,3,4,5(代表ABCD四个选项)")
    private String allOption;

    @Schema(description = "答案的图片列表", example = "img1URl, img2URl")
    private String images;

    @Schema(description = "答案解析", example = "1+1=2")
    private String analysis;

    @Schema(description = "问题id", example = "1")
    private Integer questionId;

    @Schema(description = "正确的答案的索引", example = "allOption[index]")
    private String trueOption;
}
