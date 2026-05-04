package com.liuxuanhui.aicodehelper.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxuanhui.aicodehelper.exam.entity.Question;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.QuestionVo;

import java.util.List;

public interface QuestionService extends IService<Question> {

    PageResponse<Question> getQuestion(String questionType, String questionBank, String questionContent, Integer pageNo, Integer pageSize);

    QuestionVo getQuestionVoById(Integer id);

    PageResponse<QuestionVo> getQuestionVoByIds(List<Integer> ids);

    void deleteQuestionByIds(String questionIds);

    void addQuestion(QuestionVo questionVo);

    void updateQuestion(QuestionVo questionVo);
}
