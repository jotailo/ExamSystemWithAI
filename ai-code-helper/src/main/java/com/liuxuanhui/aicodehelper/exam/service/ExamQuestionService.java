package com.liuxuanhui.aicodehelper.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxuanhui.aicodehelper.exam.entity.ExamQuestion;

public interface ExamQuestionService extends IService<ExamQuestion> {

    ExamQuestion getExamQuestionByExamId(Integer examId);
}
