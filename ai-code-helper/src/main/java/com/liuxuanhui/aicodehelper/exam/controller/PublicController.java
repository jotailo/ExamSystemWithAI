package com.liuxuanhui.aicodehelper.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuxuanhui.aicodehelper.exam.entity.Exam;
import com.liuxuanhui.aicodehelper.exam.service.ExamService;
import com.liuxuanhui.aicodehelper.exam.service.NoticeService;
import com.liuxuanhui.aicodehelper.exam.service.QuestionBankService;
import com.liuxuanhui.aicodehelper.exam.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "三个角色公共的相关接口")
@RequestMapping(value = {"/public", "/exam/public"})
public class PublicController {

    private final NoticeService noticeService;
    private final ExamService examService;
    private final QuestionBankService questionBankService;

    @PostMapping("/getExamInfo")
    @Operation(summary = "根据信息查询考试的信息")
    public CommonResult<PageResponse<Exam>> getExamInfo(@RequestBody ExamQueryVo examQueryVo) {
        return CommonResult.<PageResponse<Exam>>builder().data(examService.getExamPage(examQueryVo)).build();
    }

    @GetMapping("/getExamInfoById")
    @Operation(summary = "根据考试id查询考试的信息和题目列表")
    public CommonResult<AddExamByQuestionVo> getExamInfoById(@RequestParam Integer examId) {
        return CommonResult.<AddExamByQuestionVo>builder().data(examService.getExamInfoById(examId)).build();
    }

    @GetMapping("/allExamInfo")
    @Operation(summary = "查询考试所有信息")
    public CommonResult<List<Exam>> allExamInfo() {
        return CommonResult.<List<Exam>>builder().data(examService.list(new QueryWrapper<>())).build();
    }

    @GetMapping("/getBankHaveQuestionSumByType")
    @Operation(summary = "获取题库中所有题目类型的数量")
    public CommonResult<PageResponse<BankHaveQuestionSum>> getBankHaveQuestionSumByType(
            @RequestParam(required = false) String bankName, Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<BankHaveQuestionSum>>builder()
                .data(questionBankService.getBankHaveQuestionSumByType(bankName, pageNo, pageSize))
                .build();
    }

    @GetMapping("/getQuestionByBankIdAndType")
    @Operation(summary = "根据题库id和题目类型获取题目信息 type(1单选 2多选 3判断)")
    public CommonResult<List<QuestionVo>> getQuestionByBankIdAndType(Integer bankId, Integer type) {
        return CommonResult.<List<QuestionVo>>builder()
                .data(questionBankService.getQuestionByBankIdAndType(bankId, type))
                .build();
    }

    @GetMapping("/getQuestionByBank")
    @Operation(summary = "根据题库获取所有的题目信息(单选,多选,判断题)")
    public CommonResult<List<QuestionVo>> getQuestionByBank(Integer bankId) {
        return CommonResult.<List<QuestionVo>>builder()
                .data(questionBankService.getQuestionsByBankId(bankId))
                .build();
    }

    @GetMapping("/getCurrentNewNotice")
    @Operation(summary = "获取当前系统最新的公告")
    public CommonResult<String> getCurrentNewNotice() {
        return CommonResult.<String>builder().data(noticeService.getCurrentNotice()).build();
    }
}
