package com.liuxuanhui.aicodehelper.exam.vo;

import com.liuxuanhui.aicodehelper.exam.entity.QuestionBank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankHaveQuestionSum {
    private QuestionBank questionBank;
    private Integer singleChoice;
    private Integer multipleChoice;
    private Integer judge;
    private Integer shortAnswer;
}
