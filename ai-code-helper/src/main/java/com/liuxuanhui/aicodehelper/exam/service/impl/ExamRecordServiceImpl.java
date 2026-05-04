package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.entity.Answer;
import com.liuxuanhui.aicodehelper.exam.entity.ExamQuestion;
import com.liuxuanhui.aicodehelper.exam.entity.ExamRecord;
import com.liuxuanhui.aicodehelper.exam.entity.User;
import com.liuxuanhui.aicodehelper.exam.exception.BusinessException;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.mapper.AnswerMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.ExamQuestionMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.ExamRecordMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.UserMapper;
import com.liuxuanhui.aicodehelper.exam.service.ExamRecordService;
import com.liuxuanhui.aicodehelper.exam.utils.JwtUtils;
import com.liuxuanhui.aicodehelper.exam.utils.RedisUtil;
import com.liuxuanhui.aicodehelper.exam.utils.SaltEncryption;
import com.liuxuanhui.aicodehelper.exam.utils.certificate.ContentStyle;
import com.liuxuanhui.aicodehelper.exam.utils.certificate.DateTimeUtil;
import com.liuxuanhui.aicodehelper.exam.utils.certificate.PDFUtil;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.TokenVo;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.liuxuanhui.aicodehelper.exam.utils.CommonUtils.setEqualsQueryWrapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    private final UserMapper userMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final AnswerMapper answerMapper;
    private final RedisUtil redisUtil;

    @Override
    public PageResponse<ExamRecord> getUserGrade(String username, Integer examId, Integer pageNo, Integer pageSize) {
        User user = Optional.ofNullable(userMapper.selectOne(new QueryWrapper<User>().eq("username", username)))
                .orElseThrow(() -> new BusinessException(CommonErrorCode.E_100102));
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("exam_id", examId));
        wrapper.orderBy(true, false, "exam_time");
        IPage<ExamRecord> page = examRecordMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResponse.<ExamRecord>builder().data(page.getRecords()).total(page.getTotal()).build();
    }

    @Cache(prefix = "exam:record", suffix = "#recordId", ttl = 10, randomTime = 2, timeUnit = TimeUnit.HOURS)
    @Override
    public ExamRecord getExamRecordById(Integer recordId) {
        return examRecordMapper.selectById(recordId);
    }

    @Override
    public void createExamCertificate(HttpServletResponse response, String examName, Integer examRecordId) {
        ExamRecord examRecord = getExamRecordById(examRecordId);
        User user = Optional.ofNullable(userMapper.selectById(examRecord.getUserId()))
                .orElse(User.builder().trueName("该用户已注销").build());

        String backgroundImage = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/images/certificateBg.png")).getPath();
        String logo = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/images/logo.png")).getPath();
        String pdfFilePath = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/templateCertificate.pdf")).getPath();

        PDFUtil pdfUtil = new PDFUtil();
        ContentStyle style1 = new ContentStyle();
        style1.setFontSize(15);
        ContentStyle style2 = new ContentStyle();
        style2.setFontSize(10);

        String trueName = user.getTrueName();
        Date examTime = examRecord.getExamTime();
        String userInfo = trueName + "同学：";
        String content = "您于" + DateTimeUtil.DateToString(examTime) + "在" + examName + "测评中取得优异成绩!";

        try {
            pdfUtil.openDocument(pdfFilePath)
                    .addImage(backgroundImage, 0, 400)
                    .addLogo(logo, 270, 480)
                    .addContent(userInfo, 85, 630, style1)
                    .addContent("特发此证,以资鼓励!", 125, 495, style2)
                    .addContent("Power By LiuXuanhui", 360, 495, style2);
            for (int i = 0, y = 590; i < content.length(); y -= 30) {
                int end = Math.min(i + 30, content.length());
                pdfUtil.addContent(content.substring(i, end), 125, y, style1);
                i = end;
            }
        } catch (Exception e) {
            log.error("生成证书错误: " + e);
        }
        pdfUtil.close();

        if (pdfFilePath.contains("%")) {
            pdfFilePath = URLDecoder.decode(pdfFilePath, StandardCharsets.UTF_8);
        }

        ServletOutputStream out = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(pdfFilePath);
            response.setContentType("application/pdf");
            out = response.getOutputStream();
            int len;
            byte[] buffer = new byte[1024 * 10];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("responseFileStream error:" + e);
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (Exception e) {
                log.error("stream close error:" + e);
            }
        }
    }

    @Override
    public Integer addExamRecord(ExamRecord examRecord, HttpServletRequest request) {
        TokenVo tokenVo = JwtUtils.getUserInfoByToken(request);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        examRecord.setUserId(user.getId());

        List<ExamRecord> examRecords = examRecordMapper.selectList(null);
        int id = examRecords.isEmpty() ? 1 : examRecords.get(examRecords.size() - 1).getRecordId() + 1;
        examRecord.setRecordId(id);

        List<Answer> answers = answerMapper.selectList(
                new QueryWrapper<Answer>().in("question_id", Arrays.asList(examRecord.getQuestionIds().split(","))));
        HashMap<String, String> map = new HashMap<>();
        ExamQuestion examQuestion = examQuestionMapper.selectOne(
                new QueryWrapper<ExamQuestion>().eq("exam_id", examRecord.getExamId()));
        String[] ids = examQuestion.getQuestionIds().split(",");
        String[] scores = examQuestion.getScores().split(",");
        for (int i = 0; i < ids.length; i++) {
            map.put(ids[i], scores[i]);
        }

        int logicScore = 0;
        StringBuilder sf = new StringBuilder();
        String[] userAnswers = examRecord.getUserAnswers().split("-");
        String[] questionIds = examRecord.getQuestionIds().split(",");
        for (int i = 0; i < questionIds.length; i++) {
            int index = SaltEncryption.getIndex(answers, Integer.parseInt(questionIds[i]));
            if (index != -1) {
                if (Objects.equals(userAnswers[i], answers.get(index).getTrueOption())) {
                    logicScore += Integer.parseInt(map.get(questionIds[i]));
                } else {
                    sf.append(questionIds[i]).append(",");
                }
            }
        }
        examRecord.setLogicScore(logicScore);
        if (sf.length() > 0) {
            examRecord.setErrorQuestionIds(sf.substring(0, sf.toString().length() - 1));
        }
        examRecord.setExamTime(new Date());
        examRecordMapper.insert(examRecord);
        return id;
    }

    @Override
    public PageResponse<ExamRecord> getExamRecord(Integer examId, Integer pageNo, Integer pageSize) {
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("exam_id", examId));
        wrapper.orderBy(true, false, "exam_time");
        IPage<ExamRecord> page = examRecordMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageResponse.<ExamRecord>builder().data(page.getRecords()).total(page.getTotal()).build();
    }

    @Override
    public void setObjectQuestionScore(Integer totalScore, Integer examRecordId) {
        ExamRecord examRecord = examRecordMapper.selectOne(
                new QueryWrapper<ExamRecord>().eq("record_id", examRecordId));
        examRecord.setTotalScore(totalScore);
        examRecordMapper.update(examRecord, new UpdateWrapper<ExamRecord>().eq("record_id", examRecordId));
        redisUtil.del("exam:record:" + examRecordId);
    }
}
