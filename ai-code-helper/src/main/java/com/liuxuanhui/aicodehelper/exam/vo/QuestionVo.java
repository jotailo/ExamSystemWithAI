package com.liuxuanhui.aicodehelper.exam.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVo {

    @NotNull
    private Integer questionType;

    private Integer questionId;

    @NotNull
    private Integer questionLevel;

    private Integer[] bankId;

    @NotBlank
    private String questionContent;

    private String[] images;

    private String analysis;

    private String createPerson;

    private Answer[] Answer;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Answer {

        private Integer id;

        private String isTrue;

        @NotBlank
        private String answer;

        private String[] images;

        private String analysis;
    }
}
