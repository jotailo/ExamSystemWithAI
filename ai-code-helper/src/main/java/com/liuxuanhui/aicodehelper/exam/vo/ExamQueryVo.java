package com.liuxuanhui.aicodehelper.exam.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQueryVo {

    private Integer examType;

    private String startTime;

    private String endTime;

    private String examName;

    @NotNull
    private Integer pageNo;

    @NotNull
    private Integer pageSize;
}
