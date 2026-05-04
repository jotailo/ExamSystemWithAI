package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.entity.Answer;
import com.liuxuanhui.aicodehelper.exam.entity.Question;
import com.liuxuanhui.aicodehelper.exam.mapper.AnswerMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.QuestionBankMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.QuestionMapper;
import com.liuxuanhui.aicodehelper.exam.service.QuestionService;
import com.liuxuanhui.aicodehelper.exam.utils.RedisUtil;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.QuestionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.liuxuanhui.aicodehelper.exam.utils.CommonUtils.setEqualsQueryWrapper;
import static com.liuxuanhui.aicodehelper.exam.utils.CommonUtils.setLikeWrapper;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionBankMapper questionBankMapper;
    private final AnswerMapper answerMapper;
    private final RedisUtil redisUtil;

    @Override
    public PageResponse<Question> getQuestion(String questionType, String questionBank, String questionContent,
                                               Integer pageNo, Integer pageSize) {
        IPage<Question> questionPage = new Page<>(pageNo, pageSize);
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        Map<String, Object> likeQueryParams = new HashMap<>(2);
        likeQueryParams.put("qu_bank_name", questionBank);
        likeQueryParams.put("qu_content", questionContent);
        setLikeWrapper(wrapper, likeQueryParams);
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("qu_type", questionType));
        questionPage = questionMapper.selectPage(questionPage, wrapper);
        return PageResponse.<Question>builder().data(questionPage.getRecords()).total(questionPage.getTotal()).build();
    }

    @Cache(prefix = "questionVo", suffix = "#id", ttl = 5, timeUnit = TimeUnit.HOURS)
    @Override
    public QuestionVo getQuestionVoById(Integer id) {
        Question question = questionMapper.selectById(id);
        Answer answer = answerMapper.selectOne(new QueryWrapper<Answer>().eq("question_id", id));
        return buildQuestionVoByQuestionAndAnswer(question, answer);
    }

    @Override
    public PageResponse<QuestionVo> getQuestionVoByIds(List<Integer> ids) {
        List<Question> questions = questionMapper.selectBatchIds(ids);
        List<Answer> answers = answerMapper.selectList(new QueryWrapper<Answer>().in("question_id", ids));
        List<QuestionVo> questionVos = questions.stream()
                .map(question -> {
                    Answer currentQuestionAnswer = answers.stream()
                            .filter(answer -> answer.getQuestionId().equals(question.getId()))
                            .findFirst()
                            .orElse(null);
                    return buildQuestionVoByQuestionAndAnswer(question, currentQuestionAnswer);
                }).collect(Collectors.toList());
        return PageResponse.<QuestionVo>builder().data(questionVos).total(questionVos.size()).build();
    }

    @Override
    public void deleteQuestionByIds(String questionIds) {
        String[] ids = questionIds.split(",");
        Map<String, Object> map = new HashMap<>();
        for (String id : ids) {
            map.clear();
            map.put("question_id", id);
            questionMapper.deleteById(Integer.parseInt(id));
            answerMapper.deleteByMap(map);
            redisUtil.del("questionVo:" + id);
        }
        redisUtil.del("questionBanks");
    }

    @Transactional
    @Override
    public void addQuestion(QuestionVo questionVo) {
        Question question = new Question();
        question.setQuType(questionVo.getQuestionType());
        setQuestionField(question, questionVo);
        if (questionVo.getImages() != null && questionVo.getImages().length != 0) {
            String quImages = Arrays.toString(questionVo.getImages());
            question.setImage(quImages.substring(1, quImages.length() - 1).replaceAll(" ", ""));
        }
        buildBankName(questionVo, question);
        questionMapper.insert(question);
        Integer currentQuId = question.getId();

        StringBuilder multipleChoice = new StringBuilder();
        if (questionVo.getQuestionType() != 4) {
            Answer answer = new Answer();
            answer.setQuestionId(currentQuId);
            StringBuilder imgs = new StringBuilder();
            StringBuilder answers = new StringBuilder();
            for (int i = 0; i < questionVo.getAnswer().length; i++) {
                if (questionVo.getAnswer()[i].getImages() != null && questionVo.getAnswer()[i].getImages().length > 0) {
                    imgs.append(questionVo.getAnswer()[i].getImages()[0]).append(",");
                }
                buildAnswer(answers, questionVo, i, multipleChoice, answer);
            }
            buildMultiQuestionAnswer(questionVo, multipleChoice, answer, imgs, answers);
            answerMapper.insert(answer);
        }
        clearQuestionBankCache(questionVo);
    }

    @Override
    public void updateQuestion(QuestionVo questionVo) {
        Question question = new Question();
        question.setQuType(questionVo.getQuestionType());
        question.setId(questionVo.getQuestionId());
        setQuestionField(question, questionVo);
        if (questionVo.getImages() != null && questionVo.getImages().length != 0) {
            String quImages = Arrays.toString(questionVo.getImages());
            question.setImage(quImages.substring(1, quImages.length() - 1).replaceAll(" ", ""));
        }
        buildBankName(questionVo, question);
        questionMapper.update(question, new UpdateWrapper<Question>().eq("id", questionVo.getQuestionId()));

        StringBuilder multipleChoice = new StringBuilder();
        if (questionVo.getQuestionType() != 4) {
            Answer answer = new Answer();
            answer.setQuestionId(questionVo.getQuestionId());
            StringBuilder imgs = new StringBuilder();
            StringBuilder answers = new StringBuilder();
            for (int i = 0; i < questionVo.getAnswer().length; i++) {
                if (questionVo.getAnswer()[i].getImages() != null && questionVo.getAnswer()[i].getImages().length > 0) {
                    imgs.append(questionVo.getAnswer()[i].getImages()[0]).append(",");
                }
                buildAnswer(answers, questionVo, i, multipleChoice, answer);
            }
            buildMultiQuestionAnswer(questionVo, multipleChoice, answer, imgs, answers);
            answerMapper.update(answer, new UpdateWrapper<Answer>().eq("question_id", questionVo.getQuestionId()));
            redisUtil.del("questionVo:" + questionVo.getQuestionId());
            clearQuestionBankCache(questionVo);
        }
    }

    private void clearQuestionBankCache(QuestionVo questionVo) {
        if (questionVo.getBankId() != null) {
            for (Integer bankId : questionVo.getBankId()) {
                redisUtil.del("questionBankQuestion:" + bankId);
            }
        }
    }

    private void buildAnswer(StringBuilder answers, QuestionVo questionVo, int i,
                              StringBuilder multipleChoice, Answer answer) {
        answers.append(questionVo.getAnswer()[i].getAnswer()).append(",");
        if (questionVo.getQuestionType() == 2) {
            if ("true".equals(questionVo.getAnswer()[i].getIsTrue())) multipleChoice.append(i).append(",");
        } else {
            if ("true".equals(questionVo.getAnswer()[i].getIsTrue())) {
                answer.setTrueOption(i + "");
                answer.setAnalysis(questionVo.getAnswer()[i].getAnalysis());
            }
        }
    }

    private void buildMultiQuestionAnswer(QuestionVo questionVo, StringBuilder multipleChoice,
                                           Answer answer, StringBuilder imgs, StringBuilder answers) {
        if (questionVo.getQuestionType() == 2)
            answer.setTrueOption(multipleChoice.substring(0, multipleChoice.toString().length() - 1));
        String handleImgs = imgs.toString();
        String handleAnswers = answers.toString();
        if (!handleImgs.isEmpty()) handleImgs = handleImgs.substring(0, handleImgs.length() - 1);
        if (!handleAnswers.isEmpty()) handleAnswers = handleAnswers.substring(0, handleAnswers.length() - 1);
        answer.setImages(handleImgs);
        answer.setAllOption(handleAnswers);
    }

    private void buildBankName(QuestionVo questionVo, Question question) {
        StringBuilder bankNames = new StringBuilder();
        if (questionVo.getBankId() != null) {
            for (Integer integer : questionVo.getBankId()) {
                bankNames.append(questionBankMapper.selectById(integer).getBankName()).append(",");
            }
        }
        String names = bankNames.toString();
        if (!names.isEmpty()) names = names.substring(0, names.length() - 1);
        question.setQuBankName(names);
    }

    private void setQuestionField(Question question, QuestionVo questionVo) {
        question.setCreateTime(new Date());
        question.setLevel(questionVo.getQuestionLevel());
        question.setAnalysis(questionVo.getAnalysis());
        question.setQuContent(questionVo.getQuestionContent());
        question.setCreatePerson(questionVo.getCreatePerson());
        if (questionVo.getBankId() != null) {
            String bankIds = Arrays.toString(questionVo.getBankId());
            question.setQuBankId(bankIds.substring(1, bankIds.length() - 1).replaceAll(" ", ""));
        }
    }

    private QuestionVo buildQuestionVoByQuestionAndAnswer(Question question, Answer answer) {
        QuestionVo questionVo = new QuestionVo();
        questionVo.setQuestionContent(question.getQuContent());
        questionVo.setAnalysis(question.getAnalysis());
        questionVo.setQuestionType(question.getQuType());
        questionVo.setQuestionLevel(question.getLevel());
        questionVo.setQuestionId(question.getId());
        if (question.getImage() != null && !question.getImage().isEmpty()) {
            questionVo.setImages(question.getImage().split(","));
        }
        questionVo.setCreatePerson(question.getCreatePerson());
        if (question.getQuBankId() != null && !question.getQuBankId().isEmpty()) {
            String[] bids = question.getQuBankId().split(",");
            Integer[] bankIds = new Integer[bids.length];
            for (int i = 0; i < bids.length; i++) {
                bankIds[i] = Integer.parseInt(bids[i]);
            }
            questionVo.setBankId(bankIds);
        }
        if (answer != null) {
            String[] allOption = answer.getAllOption().split(",");
            String[] imgs = answer.getImages() != null ? answer.getImages().split(",") : new String[0];
            QuestionVo.Answer[] qa = new QuestionVo.Answer[allOption.length];
            if (question.getQuType() != 2) {
                for (int i = 0; i < allOption.length; i++) {
                    QuestionVo.Answer a = new QuestionVo.Answer();
                    a.setId(i);
                    a.setAnswer(allOption[i]);
                    if (i < imgs.length && !imgs[i].isEmpty()) a.setImages(new String[]{imgs[i]});
                    if (answer.getTrueOption() != null && i == Integer.parseInt(answer.getTrueOption())) {
                        a.setIsTrue("true");
                        a.setAnalysis(answer.getAnalysis());
                    }
                    qa[i] = a;
                }
            } else {
                List<Integer> multiTrueOptions = Arrays.stream(answer.getTrueOption().split(","))
                        .map(Integer::parseInt).collect(Collectors.toList());
                for (int i = 0; i < allOption.length; i++) {
                    QuestionVo.Answer a = new QuestionVo.Answer();
                    a.setId(i);
                    a.setAnswer(allOption[i]);
                    a.setImages(imgs);
                    if (multiTrueOptions.contains(i)) {
                        a.setIsTrue("true");
                        a.setAnalysis(answer.getAnalysis());
                    }
                    qa[i] = a;
                }
            }
            questionVo.setAnswer(qa);
        }
        return questionVo;
    }
}
