package com.liuxuanhui.aicodehelper.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxuanhui.aicodehelper.exam.entity.Exam;
import com.liuxuanhui.aicodehelper.exam.vo.AddExamByBankVo;
import com.liuxuanhui.aicodehelper.exam.vo.AddExamByQuestionVo;
import com.liuxuanhui.aicodehelper.exam.vo.ExamQueryVo;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;

import java.util.List;

public interface ExamService extends IService<Exam> {

    PageResponse<Exam> getExamPage(ExamQueryVo examQueryVo);

    AddExamByQuestionVo getExamInfoById(Integer examId);

    void operationExam(Integer type, String ids);

    void addExamByBank(AddExamByBankVo addExamByBankVo);

    void addExamByQuestionList(AddExamByQuestionVo addExamByQuestionVo);

    void updateExamInfo(AddExamByQuestionVo addExamByQuestionVo);

    List<String> getExamPassRateEchartData();

    List<String> getExamNumbersEchartData();
}
