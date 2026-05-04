package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.entity.ExamQuestion;
import com.liuxuanhui.aicodehelper.exam.mapper.ExamQuestionMapper;
import com.liuxuanhui.aicodehelper.exam.service.ExamQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamQuestionService {

    private final ExamQuestionMapper examQuestionMapper;

    @Override
    public ExamQuestion getExamQuestionByExamId(Integer examId) {
        return examQuestionMapper.selectOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examId));
    }
}
