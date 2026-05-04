package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.entity.Answer;
import com.liuxuanhui.aicodehelper.exam.mapper.AnswerMapper;
import com.liuxuanhui.aicodehelper.exam.service.AnswerService;
import org.springframework.stereotype.Service;

@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {
}
