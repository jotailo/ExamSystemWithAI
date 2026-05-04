package com.liuxuanhui.aicodehelper.exam.controller;

import com.liuxuanhui.aicodehelper.exam.entity.ExamQuestion;
import com.liuxuanhui.aicodehelper.exam.entity.ExamRecord;
import com.liuxuanhui.aicodehelper.exam.service.ExamQuestionService;
import com.liuxuanhui.aicodehelper.exam.service.QuestionService;
import com.liuxuanhui.aicodehelper.exam.service.impl.ExamRecordServiceImpl;
import com.liuxuanhui.aicodehelper.exam.utils.FileUtil;
import com.liuxuanhui.aicodehelper.exam.vo.CommonResult;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.QuestionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "学生权限相关的接口")
@RequestMapping(value = {"/student", "/exam/student"})
public class StudentController {

    private final FileUtil fileUtil;
    private final ExamRecordServiceImpl examRecordService;
    private final QuestionService questionService;
    private final ExamQuestionService examQuestionService;

    @GetMapping("/getMyGrade")
    @Operation(summary = "获取个人成绩(分页 根据考试名查询)")
    public CommonResult<PageResponse<ExamRecord>> getMyGrade(String username, Integer pageNo, Integer pageSize,
                                                              @RequestParam(required = false) Integer examId) {
        return CommonResult.<PageResponse<ExamRecord>>builder()
                .data(examRecordService.getUserGrade(username, examId, pageNo, pageSize))
                .build();
    }

    @GetMapping("/getCertificate")
    @Operation(summary = "生成证书接口")
    public void getCertificate(HttpServletResponse response,
                                @RequestParam(name = "examName") String examName,
                                @RequestParam(name = "examRecordId") Integer examRecordId) throws UnsupportedEncodingException {
        examRecordService.createExamCertificate(response, URLDecoder.decode(examName, "UTF-8"), examRecordId);
    }

    @PostMapping("/addExamRecord")
    @Operation(summary = "保存考试记录信息,返回保存记录的id")
    public CommonResult<Integer> addExamRecord(@RequestBody ExamRecord examRecord, HttpServletRequest request) {
        return CommonResult.<Integer>builder().data(examRecordService.addExamRecord(examRecord, request)).build();
    }

    @GetMapping("/getQuestionById/{id}")
    @Operation(summary = "根据id获取题目信息")
    public CommonResult<QuestionVo> getQuestionById(@PathVariable("id") Integer id) {
        return CommonResult.<QuestionVo>builder().data(questionService.getQuestionVoById(id)).build();
    }

    @GetMapping("/getQuestionByIds")
    @Operation(summary = "根据id集合获取题目信息")
    public CommonResult<PageResponse<QuestionVo>> getQuestionByIds(@RequestParam("ids") List<Integer> ids) {
        return CommonResult.<PageResponse<QuestionVo>>builder()
                .data(questionService.getQuestionVoByIds(ids))
                .build();
    }

    @GetMapping("/getExamRecordById/{recordId}")
    @Operation(summary = "根据考试的记录id查询用户考试的信息")
    public CommonResult<ExamRecord> getExamRecordById(@PathVariable Integer recordId) {
        return CommonResult.<ExamRecord>builder().data(examRecordService.getExamRecordById(recordId)).build();
    }

    @GetMapping("/getExamQuestionByExamId/{examId}")
    @Operation(summary = "根据考试id查询考试中的每一道题目id和分值")
    public CommonResult<ExamQuestion> getExamQuestionByExamId(@PathVariable Integer examId) {
        return CommonResult.<ExamQuestion>builder()
                .data(examQuestionService.getExamQuestionByExamId(examId))
                .build();
    }

    @PostMapping("/uploadQuestionImage")
    @Operation(summary = "接受前端上传的图片,返回上传图片地址")
    public CommonResult<String> uploadQuestionImage(MultipartFile file) {
        log.info("开始上传文件: {}", file.getOriginalFilename());
        return CommonResult.<String>builder().data(fileUtil.uploadToLocal(file)).message("上传成功").build();
    }
}
