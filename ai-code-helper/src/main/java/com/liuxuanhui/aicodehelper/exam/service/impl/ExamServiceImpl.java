package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.entity.Exam;
import com.liuxuanhui.aicodehelper.exam.entity.ExamQuestion;
import com.liuxuanhui.aicodehelper.exam.entity.ExamRecord;
import com.liuxuanhui.aicodehelper.exam.entity.Question;
import com.liuxuanhui.aicodehelper.exam.exception.BusinessException;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.mapper.ExamMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.ExamQuestionMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.ExamRecordMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.QuestionMapper;
import com.liuxuanhui.aicodehelper.exam.service.ExamService;
import com.liuxuanhui.aicodehelper.exam.utils.RedisUtil;
import com.liuxuanhui.aicodehelper.exam.vo.AddExamByBankVo;
import com.liuxuanhui.aicodehelper.exam.vo.AddExamByQuestionVo;
import com.liuxuanhui.aicodehelper.exam.vo.ExamQueryVo;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.liuxuanhui.aicodehelper.exam.utils.CommonUtils.setEqualsQueryWrapper;
import static com.liuxuanhui.aicodehelper.exam.utils.CommonUtils.setLikeWrapper;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    private final QuestionMapper questionMapper;
    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final RedisUtil redisUtil;

    @Override
    public PageResponse<Exam> getExamPage(ExamQueryVo examQueryVo) {
        QueryWrapper<Exam> wrapper = new QueryWrapper<>();
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("type", examQueryVo.getExamType()));
        setLikeWrapper(wrapper, Collections.singletonMap("exam_name", examQueryVo.getExamName()));
        if (examQueryVo.getStartTime() != null) {
            wrapper.gt("start_time", examQueryVo.getStartTime().substring(0, examQueryVo.getStartTime().indexOf("T")));
        }
        if (examQueryVo.getEndTime() != null) {
            wrapper.lt("end_time", examQueryVo.getEndTime().substring(0, examQueryVo.getEndTime().indexOf("T")));
        }
        IPage<Exam> page = examMapper.selectPage(new Page<>(examQueryVo.getPageNo(), examQueryVo.getPageSize()), wrapper);
        return PageResponse.<Exam>builder().data(page.getRecords()).total(page.getTotal()).build();
    }

    @Cache(prefix = "examInfo", suffix = "#examId", ttl = 4, randomTime = 2, timeUnit = TimeUnit.HOURS)
    @Override
    public AddExamByQuestionVo getExamInfoById(Integer examId) {
        Exam exam = Optional.ofNullable(examMapper.selectById(examId))
                .orElseThrow(() -> new BusinessException(CommonErrorCode.E_400001));
        AddExamByQuestionVo vo = AddExamByQuestionVo.builder()
                .examDesc(exam.getExamDesc())
                .examDuration(exam.getDuration())
                .examId(examId)
                .examName(exam.getExamName())
                .passScore(exam.getPassScore())
                .totalScore(exam.getTotalScore())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .type(exam.getType())
                .password(exam.getPassword())
                .status(exam.getStatus())
                .build();
        ExamQuestion examQuestion = examQuestionMapper.selectOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examId));
        vo.setQuestionIds(examQuestion.getQuestionIds());
        vo.setScores(examQuestion.getScores());
        return vo;
    }

    @Override
    public void operationExam(Integer type, String ids) {
        String[] id = ids.split(",");
        switch (type) {
            case 1 -> setExamStatus(id, 1);
            case 2 -> setExamStatus(id, 2);
            case 3 -> {
                Map<String, Object> map = new HashMap<>();
                for (String s : id) {
                    map.clear();
                    map.put("exam_id", Integer.parseInt(s));
                    examMapper.deleteByMap(map);
                    examQuestionMapper.deleteByMap(map);
                    redisUtil.del("examInfo:" + s);
                }
            }
            default -> throw new BusinessException(CommonErrorCode.E_100106);
        }
    }

    @Transactional
    @Override
    public void addExamByBank(AddExamByBankVo addExamByBankVo) {
        Exam exam = new Exam();
        exam.setStatus(addExamByBankVo.getStatus());
        exam.setDuration(addExamByBankVo.getExamDuration());
        if (addExamByBankVo.getEndTime() != null) exam.setEndTime(addExamByBankVo.getEndTime());
        if (addExamByBankVo.getStartTime() != null) exam.setStartTime(addExamByBankVo.getStartTime());
        exam.setExamDesc(addExamByBankVo.getExamDesc());
        exam.setExamName(addExamByBankVo.getExamName());
        exam.setPassScore(addExamByBankVo.getPassScore());
        exam.setType(addExamByBankVo.getType());
        if (addExamByBankVo.getPassword() != null) exam.setPassword(addExamByBankVo.getPassword());

        ExamQuestion examQuestion = buildExamQuestion(exam);
        HashSet<Integer> set = new HashSet<>();
        String[] bankNames = addExamByBankVo.getBankNames().split(",");
        for (String bankName : bankNames) {
            List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>().like("qu_bank_name", bankName));
            for (Question question : questions) {
                set.add(question.getId());
            }
        }
        String quIds = set.toString().substring(1, set.toString().length() - 1).replaceAll(" ", "");
        examQuestion.setQuestionIds(quIds);

        String[] s = quIds.split(",");
        int totalScore = 0;
        StringBuilder sf = new StringBuilder();
        for (String s1 : s) {
            Question question = questionMapper.selectById(Integer.parseInt(s1));
            if (question.getQuType() == 1) {
                sf.append(addExamByBankVo.getSingleScore()).append(",");
                totalScore += addExamByBankVo.getSingleScore();
            } else if (question.getQuType() == 2) {
                sf.append(addExamByBankVo.getMultipleScore()).append(",");
                totalScore += addExamByBankVo.getMultipleScore();
            } else if (question.getQuType() == 3) {
                sf.append(addExamByBankVo.getJudgeScore()).append(",");
                totalScore += addExamByBankVo.getJudgeScore();
            } else if (question.getQuType() == 4) {
                sf.append(addExamByBankVo.getShortScore()).append(",");
                totalScore += addExamByBankVo.getShortScore();
            }
        }
        examQuestion.setScores(sf.substring(0, sf.toString().length() - 1));
        exam.setTotalScore(totalScore);
        examMapper.insert(exam);
        examQuestionMapper.insert(examQuestion);
    }

    @Transactional
    @Override
    public void addExamByQuestionList(AddExamByQuestionVo addExamByQuestionVo) {
        Exam exam = new Exam();
        exam.setTotalScore(addExamByQuestionVo.getTotalScore());
        exam.setType(addExamByQuestionVo.getType());
        exam.setPassScore(addExamByQuestionVo.getPassScore());
        if (addExamByQuestionVo.getEndTime() != null) exam.setEndTime(addExamByQuestionVo.getEndTime());
        if (addExamByQuestionVo.getStartTime() != null) exam.setStartTime(addExamByQuestionVo.getStartTime());
        exam.setExamDesc(addExamByQuestionVo.getExamDesc());
        exam.setExamName(addExamByQuestionVo.getExamName());
        exam.setDuration(addExamByQuestionVo.getExamDuration());
        if (addExamByQuestionVo.getPassword() != null) exam.setPassword(addExamByQuestionVo.getPassword());
        exam.setStatus(addExamByQuestionVo.getStatus());

        ExamQuestion examQuestion = buildExamQuestion(exam);
        examQuestion.setScores(addExamByQuestionVo.getScores());
        examQuestion.setQuestionIds(addExamByQuestionVo.getQuestionIds());
        examMapper.insert(exam);
        examQuestionMapper.insert(examQuestion);
    }

    @Override
    public void updateExamInfo(AddExamByQuestionVo addExamByQuestionVo) {
        Exam exam = new Exam();
        exam.setTotalScore(addExamByQuestionVo.getTotalScore());
        exam.setType(addExamByQuestionVo.getType());
        exam.setPassScore(addExamByQuestionVo.getPassScore());
        exam.setEndTime(addExamByQuestionVo.getEndTime());
        exam.setStartTime(addExamByQuestionVo.getStartTime());
        exam.setExamDesc(addExamByQuestionVo.getExamDesc());
        exam.setExamName(addExamByQuestionVo.getExamName());
        exam.setDuration(addExamByQuestionVo.getExamDuration());
        exam.setPassword(addExamByQuestionVo.getPassword());
        exam.setStatus(addExamByQuestionVo.getStatus());
        exam.setExamId(addExamByQuestionVo.getExamId());

        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExamId(addExamByQuestionVo.getExamId());
        examQuestion.setScores(addExamByQuestionVo.getScores());
        examQuestion.setQuestionIds(addExamByQuestionVo.getQuestionIds());

        examMapper.update(exam, new UpdateWrapper<Exam>().eq("exam_id", exam.getExamId()));
        examQuestionMapper.update(examQuestion, new UpdateWrapper<ExamQuestion>().eq("exam_id", exam.getExamId()));
        redisUtil.del("examInfo:" + exam.getExamId());
    }

    @Override
    public List<String> getExamPassRateEchartData() {
        List<Exam> exams = examMapper.selectList(null);
        List<ExamRecord> examRecords = examRecordMapper.selectList(new QueryWrapper<ExamRecord>().isNotNull("total_score"));
        String[] examNames = new String[exams.size()];
        double[] passRates = new double[exams.size()];

        for (int i = 0; i < exams.size(); i++) {
            examNames[i] = exams.get(i).getExamName();
            double total = 0, pass = 0;
            for (ExamRecord examRecord : examRecords) {
                if (Objects.equals(examRecord.getExamId(), exams.get(i).getExamId())) {
                    total++;
                    if (examRecord.getTotalScore() >= exams.get(i).getPassScore()) pass++;
                }
            }
            passRates[i] = total == 0 ? 0 : pass / total;
        }

        List<String> list = new ArrayList<>();
        String res1 = Arrays.toString(examNames);
        String res2 = Arrays.toString(passRates);
        list.add(res1.substring(1, res1.length() - 1).replaceAll(" ", ""));
        list.add(res2.substring(1, res2.length() - 1).replaceAll(" ", ""));
        return list;
    }

    @Override
    public List<String> getExamNumbersEchartData() {
        List<Exam> exams = examMapper.selectList(null);
        List<ExamRecord> examRecords = examRecordMapper.selectList(null);
        String[] examNames = new String[exams.size()];
        String[] examNumbers = new String[exams.size()];

        for (int i = 0; i < exams.size(); i++) {
            examNames[i] = exams.get(i).getExamName();
            int cur = 0;
            for (ExamRecord examRecord : examRecords) {
                if (Objects.equals(examRecord.getExamId(), exams.get(i).getExamId())) cur++;
            }
            examNumbers[i] = cur + "";
        }

        List<String> list = new ArrayList<>();
        String res1 = Arrays.toString(examNames);
        String res2 = Arrays.toString(examNumbers);
        list.add(res1.substring(1, res1.length() - 1).replaceAll(" ", ""));
        list.add(res2.substring(1, res2.length() - 1).replaceAll(" ", ""));
        return list;
    }

    private ExamQuestion buildExamQuestion(Exam exam) {
        List<Exam> examList = examMapper.selectList(null);
        int id = examList.isEmpty() ? 0 : examList.get(examList.size() - 1).getExamId() + 1;
        exam.setExamId(id);
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExamId(id);
        return examQuestion;
    }

    private void setExamStatus(String[] id, int status) {
        for (String s : id) {
            Exam exam = examMapper.selectOne(new QueryWrapper<Exam>().eq("exam_id", Integer.parseInt(s)));
            exam.setStatus(status);
            examMapper.update(exam, new UpdateWrapper<Exam>().eq("exam_id", s));
        }
    }
}
