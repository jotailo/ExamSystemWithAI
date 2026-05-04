package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.entity.Answer;
import com.liuxuanhui.aicodehelper.exam.entity.Question;
import com.liuxuanhui.aicodehelper.exam.entity.QuestionBank;
import com.liuxuanhui.aicodehelper.exam.mapper.AnswerMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.QuestionBankMapper;
import com.liuxuanhui.aicodehelper.exam.mapper.QuestionMapper;
import com.liuxuanhui.aicodehelper.exam.service.QuestionBankService;
import com.liuxuanhui.aicodehelper.exam.utils.RedisUtil;
import com.liuxuanhui.aicodehelper.exam.vo.BankHaveQuestionSum;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import com.liuxuanhui.aicodehelper.exam.vo.QuestionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.liuxuanhui.aicodehelper.exam.utils.CommonUtils.setLikeWrapper;

@Service
@RequiredArgsConstructor
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    private final QuestionBankMapper questionBankMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final RedisUtil redisUtil;

    @Override
    public PageResponse<BankHaveQuestionSum> getBankHaveQuestionSumByType(String bankName, Integer pageNo, Integer pageSize) {
        QueryWrapper<QuestionBank> wrapper = new QueryWrapper<>();
        setLikeWrapper(wrapper, Collections.singletonMap("bank_name", bankName));
        IPage<QuestionBank> iPage = questionBankMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<QuestionBank> questionBanks = iPage.getRecords();

        List<BankHaveQuestionSum> bankHaveQuestionSums = new ArrayList<>();
        for (QuestionBank bank : questionBanks) {
            BankHaveQuestionSum sum = new BankHaveQuestionSum();
            sum.setQuestionBank(bank);
            sum.setSingleChoice(questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 1).like("qu_bank_name", bank.getBankName())).intValue());
            sum.setMultipleChoice(questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 2).like("qu_bank_name", bank.getBankName())).intValue());
            sum.setJudge(questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 3).like("qu_bank_name", bank.getBankName())).intValue());
            sum.setShortAnswer(questionMapper.selectCount(new QueryWrapper<Question>().eq("qu_type", 4).like("qu_bank_name", bank.getBankName())).intValue());
            bankHaveQuestionSums.add(sum);
        }
        return PageResponse.<BankHaveQuestionSum>builder().data(bankHaveQuestionSums).total(iPage.getTotal()).build();
    }

    @Cache(prefix = "questionBankQuestion", suffix = "#bankId", ttl = 6, randomTime = 1, timeUnit = TimeUnit.HOURS)
    @Override
    public List<QuestionVo> getQuestionsByBankId(Integer bankId) {
        QuestionBank bank = questionBankMapper.selectById(bankId);
        List<Question> questions = questionMapper.selectList(
                new QueryWrapper<Question>().like("qu_bank_name", bank.getBankName()).in("qu_type", 1, 2, 3));
        List<QuestionVo> questionVos = new ArrayList<>();
        for (Question question : questions) {
            QuestionVo questionVo = new QuestionVo();
            questionVo.setQuestionId(question.getId());
            questionVo.setQuestionLevel(question.getLevel());
            if (question.getImage() != null && !question.getImage().isEmpty())
                questionVo.setImages(question.getImage().split(","));
            questionVo.setCreatePerson(question.getCreatePerson());
            questionVo.setAnalysis(question.getAnalysis());
            questionVo.setQuestionContent(question.getQuContent());
            questionVo.setQuestionType(question.getQuType());

            Answer answer = answerMapper.selectOne(new QueryWrapper<Answer>().eq("question_id", question.getId()));
            String[] options = answer.getAllOption().split(",");
            String[] images = answer.getImages() != null ? answer.getImages().split(",") : new String[0];
            QuestionVo.Answer[] handleAnswer = new QuestionVo.Answer[options.length];
            for (int i = 0; i < options.length; i++) {
                QuestionVo.Answer a = new QuestionVo.Answer();
                if (images.length - 1 >= i && images[i] != null && !images[i].isEmpty())
                    a.setImages(new String[]{images[i]});
                a.setAnswer(options[i]);
                a.setId(i);
                a.setIsTrue("false");
                handleAnswer[i] = a;
            }
            if (question.getQuType() != 2) {
                int trueOption = Integer.parseInt(answer.getTrueOption());
                handleAnswer[trueOption].setIsTrue("true");
                handleAnswer[trueOption].setAnalysis(answer.getAnalysis());
            } else {
                String[] trueOptions = answer.getTrueOption().split(",");
                for (String trueOption : trueOptions) {
                    handleAnswer[Integer.parseInt(trueOption)].setIsTrue("true");
                    handleAnswer[Integer.parseInt(trueOption)].setAnalysis(answer.getAnalysis());
                }
            }
            questionVo.setAnswer(handleAnswer);
            questionVos.add(questionVo);
        }
        return questionVos;
    }

    @Override
    public List<QuestionVo> getQuestionByBankIdAndType(Integer bankId, Integer type) {
        List<QuestionVo> questionVoList = getQuestionsByBankId(bankId);
        questionVoList.removeIf(questionVo -> !Objects.equals(questionVo.getQuestionType(), type));
        return questionVoList;
    }

    @Cache(prefix = "questionBanks", ttl = 10, timeUnit = TimeUnit.HOURS)
    @Override
    public List<QuestionBank> getAllQuestionBanks() {
        return questionBankMapper.selectList(null);
    }

    @Override
    public void addQuestionToBank(String questionIds, String banks) {
        String[] quIds = questionIds.split(",");
        String[] bankIds = banks.split(",");
        for (String quId : quIds) {
            Question question = questionMapper.selectById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            Set<Integer> allId = new HashSet<>();
            if (quBankId != null && !quBankId.isEmpty()) {
                for (String s : quBankId.split(",")) allId.add(Integer.parseInt(s));
            }
            for (String bankId : bankIds) {
                redisUtil.del("questionBankQuestion:" + bankId);
                allId.add(Integer.parseInt(bankId));
            }
            String handleHaveBankIds = allId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            question.setQuBankId(handleHaveBankIds);
            StringBuilder bankNames = new StringBuilder();
            for (Integer id : allId) {
                bankNames.append(questionBankMapper.selectById(id).getBankName()).append(",");
            }
            question.setQuBankName(bankNames.substring(0, bankNames.toString().length() - 1));
            questionMapper.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
    }

    @Override
    public void removeBankQuestion(String questionIds, String banks) {
        String[] quIds = questionIds.split(",");
        String[] bankIds = banks.split(",");
        for (String quId : quIds) {
            Question question = questionMapper.selectById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            Set<Integer> handleId = new HashSet<>();
            if (quBankId != null && !quBankId.isEmpty()) {
                for (String s : quBankId.split(",")) handleId.add(Integer.parseInt(s));
            }
            for (String bankId : bankIds) {
                redisUtil.del("questionBankQuestion:" + bankId);
                handleId.remove(Integer.parseInt(bankId));
            }
            String handleHaveBankIds = handleId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            question.setQuBankId(handleHaveBankIds);
            if (!handleHaveBankIds.isEmpty()) {
                StringBuilder bankNames = new StringBuilder();
                for (Integer id : handleId) {
                    bankNames.append(questionBankMapper.selectById(id).getBankName()).append(",");
                }
                question.setQuBankName(bankNames.substring(0, bankNames.toString().length() - 1));
            } else {
                question.setQuBankName("");
            }
            questionMapper.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
    }

    @Transactional
    @Override
    public void deleteQuestionBank(String ids) {
        String[] bankId = ids.split(",");
        for (String s : bankId) {
            QuestionBank questionBank = questionBankMapper.selectById(s);
            List<Question> questions = questionMapper.selectList(
                    new QueryWrapper<Question>().like("qu_bank_name", questionBank.getBankName()));
            for (Question question : questions) {
                String quBankName = question.getQuBankName();
                String quBankId = question.getQuBankId();
                String[] name = quBankName.split(",");
                String[] id = quBankId.split(",");
                String[] newName = new String[name.length - 1];
                String[] newId = new String[id.length - 1];
                for (int i = 0, j = 0; i < name.length && j < name.length - 1; i++) {
                    if (!name[i].equals(questionBank.getBankName())) {
                        newName[j++] = name[i];
                    }
                }
                for (int i = 0, j = 0; i < id.length && j < name.length - 1; i++) {
                    if (!id[i].equals(String.valueOf(questionBank.getBankId()))) {
                        newId[j++] = id[i];
                    }
                }
                question.setQuBankName(Arrays.toString(newName).replaceAll(" ", "").replace("]", "").replace("[", ""));
                question.setQuBankId(Arrays.toString(newId).replaceAll(" ", "").replace("]", "").replace("[", ""));
                questionMapper.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
                redisUtil.del("questionVo:" + question.getId());
            }
            questionBankMapper.deleteById(Integer.parseInt(s));
            redisUtil.del("questionBankQuestion:" + s, "questionBanks");
        }
    }

    @Override
    public void addQuestionBank(QuestionBank questionBank) {
        questionBankMapper.insert(questionBank);
        redisUtil.del("questionBanks");
    }
}
