package com.liuxuanhui.aicodehelper.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxuanhui.aicodehelper.exam.entity.ExamRecord;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ExamRecordService extends IService<ExamRecord> {

    PageResponse<ExamRecord> getUserGrade(String username, Integer examId, Integer pageNo, Integer pageSize);

    void createExamCertificate(HttpServletResponse response, String examName, Integer examRecordId);

    ExamRecord getExamRecordById(Integer recordId);

    Integer addExamRecord(ExamRecord examRecord, HttpServletRequest request);

    PageResponse<ExamRecord> getExamRecord(Integer examId, Integer pageNo, Integer pageSize);

    void setObjectQuestionScore(Integer totalScore, Integer examRecordId);
}
