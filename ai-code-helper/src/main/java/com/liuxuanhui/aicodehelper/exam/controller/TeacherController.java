package com.liuxuanhui.aicodehelper.exam.controller;

import com.liuxuanhui.aicodehelper.exam.entity.ExamRecord;
import com.liuxuanhui.aicodehelper.exam.entity.Question;
import com.liuxuanhui.aicodehelper.exam.entity.QuestionBank;
import com.liuxuanhui.aicodehelper.exam.service.ExamRecordService;
import com.liuxuanhui.aicodehelper.exam.service.ExamService;
import com.liuxuanhui.aicodehelper.exam.service.QuestionBankService;
import com.liuxuanhui.aicodehelper.exam.service.QuestionService;
import com.liuxuanhui.aicodehelper.exam.service.UserService;
import com.liuxuanhui.aicodehelper.exam.utils.FileUtil;
import com.liuxuanhui.aicodehelper.exam.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "老师权限相关的接口")
@RequestMapping(value = {"/teacher", "/exam/teacher"})
public class TeacherController {

    private final FileUtil fileUtil;
    private final ExamService examService;
    private final UserService userService;
    private final QuestionService questionService;
    private final ExamRecordService examRecordService;
    private final QuestionBankService questionBankService;

    @GetMapping("/getQuestionBank")
    @Operation(summary = "获取所有题库信息")
    public CommonResult<List<QuestionBank>> getQuestionBank() {
        return CommonResult.<List<QuestionBank>>builder().data(questionBankService.getAllQuestionBanks()).build();
    }

    @GetMapping("/getQuestion")
    @Operation(summary = "获取题目信息,可分页")
    public CommonResult<PageResponse<Question>> getQuestion(@RequestParam(required = false) String questionType,
                                                             @RequestParam(required = false) String questionBank,
                                                             @RequestParam(required = false) String questionContent,
                                                             Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<Question>>builder()
                .data(questionService.getQuestion(questionType, questionBank, questionContent, pageNo, pageSize))
                .build();
    }

    @GetMapping("/deleteQuestion")
    @Operation(summary = "根据id批量删除")
    public CommonResult<Void> deleteQuestion(String questionIds) {
        questionService.deleteQuestionByIds(questionIds);
        return CommonResult.<Void>builder().build();
    }

    @GetMapping("/addBankQuestion")
    @Operation(summary = "将问题加入题库")
    public CommonResult<String> addBankQuestion(String questionIds, String banks) {
        questionBankService.addQuestionToBank(questionIds, banks);
        return CommonResult.<String>builder().build();
    }

    @GetMapping("/removeBankQuestion")
    @Operation(summary = "将问题从题库移除")
    public CommonResult<Void> removeBankQuestion(String questionIds, String banks) {
        questionBankService.removeBankQuestion(questionIds, banks);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/uploadQuestionImage")
    @Operation(summary = "接受前端上传的图片,返回上传图片地址")
    public CommonResult<String> uploadQuestionImage(MultipartFile file) throws Exception {
        log.info("开始上传文件: {}", file.getOriginalFilename());
        return CommonResult.<String>builder().data(fileUtil.uploadToLocal(file)).message("上传成功").build();
    }

    @PostMapping("/addQuestion")
    @Operation(summary = "添加试题")
    public CommonResult<Void> addQuestion(@RequestBody @Valid QuestionVo questionVo) {
        questionService.addQuestion(questionVo);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/updateQuestion")
    @Operation(summary = "更新试题")
    public CommonResult<Void> updateQuestion(@RequestBody @Valid QuestionVo questionVo) {
        questionService.updateQuestion(questionVo);
        return CommonResult.<Void>builder().build();
    }

    @GetMapping("/deleteQuestionBank")
    @Operation(summary = "删除题库并去除所有题目中的包含此题库的信息")
    public CommonResult<Void> deleteQuestionBank(String ids) {
        questionBankService.deleteQuestionBank(ids);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/addQuestionBank")
    @Operation(summary = "添加题库信息")
    public CommonResult<Void> addQuestionBank(@RequestBody QuestionBank questionBank) {
        questionBankService.addQuestionBank(questionBank);
        return CommonResult.<Void>builder().build();
    }

    @GetMapping("/operationExam/{type}")
    @Operation(summary = "操作考试的信息表(type 1启用 2禁用 3删除)")
    public CommonResult<Void> operationExam(@PathVariable("type") Integer type, String ids) {
        examService.operationExam(type, ids);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/addExamByBank")
    @Operation(summary = "根据题库添加考试")
    public CommonResult<Void> addExamByBank(@RequestBody @Valid AddExamByBankVo addExamByBankVo) {
        examService.addExamByBank(addExamByBankVo);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/addExamByQuestionList")
    @Operation(summary = "根据题目列表添加考试")
    public CommonResult<Void> addExamByQuestionList(@RequestBody @Valid AddExamByQuestionVo addExamByQuestionVo) {
        examService.addExamByQuestionList(addExamByQuestionVo);
        return CommonResult.<Void>builder().build();
    }

    @PostMapping("/updateExamInfo")
    @Operation(summary = "更新考试的信息")
    public CommonResult<Void> updateExamInfo(@RequestBody AddExamByQuestionVo addExamByQuestionVo) {
        examService.updateExamInfo(addExamByQuestionVo);
        return CommonResult.<Void>builder().message("更新成功").build();
    }

    @GetMapping("/getExamRecord")
    @Operation(summary = "获取考试记录信息")
    public CommonResult<PageResponse<ExamRecord>> getExamRecord(@RequestParam(required = false) Integer examId,
                                                                  Integer pageNo, Integer pageSize) {
        return CommonResult.<PageResponse<ExamRecord>>builder()
                .data(examRecordService.getExamRecord(examId, pageNo, pageSize))
                .build();
    }

    @GetMapping("/getUserById/{userId}")
    @Operation(summary = "根据用户id查询用户信息")
    public CommonResult<UserInfoVo> getUserById(@PathVariable Integer userId) {
        return CommonResult.<UserInfoVo>builder().data(userService.getUserInfoById(userId)).build();
    }

    @GetMapping("/getUserByIds")
    @Operation(summary = "根据用户ids查询用户信息")
    public CommonResult<List<UserInfoVo>> getUserByIds(@RequestParam("userIds") List<Integer> userIds) {
        return CommonResult.<List<UserInfoVo>>builder().data(userService.getUserInfoByIds(userIds)).build();
    }

    @GetMapping("/setObjectQuestionScore")
    @Operation(summary = "设置考试记录的客观题得分")
    public CommonResult<Void> setObjectQuestionScore(Integer totalScore, Integer examRecordId) {
        examRecordService.setObjectQuestionScore(totalScore, examRecordId);
        return CommonResult.<Void>builder().build();
    }

    @GetMapping("/getExamPassRate")
    @Operation(summary = "提供每一门考试的通过率数据(echarts绘图)")
    public CommonResult<List<String>> getExamPassRate() {
        return CommonResult.<List<String>>builder().data(examService.getExamPassRateEchartData()).build();
    }

    @GetMapping("/getExamNumbers")
    @Operation(summary = "提供每一门考试的考试次数(echarts绘图)")
    public CommonResult<List<String>> getExamNumbers() {
        return CommonResult.<List<String>>builder().data(examService.getExamNumbersEchartData()).build();
    }
}
