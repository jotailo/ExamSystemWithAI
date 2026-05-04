package com.liuxuanhui.aicodehelper.ai.guardrail;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrailResult;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import dev.langchain4j.service.guardrail.OutputGuardrails;

import java.util.Set;

/**
 * @Author:Liu Xuanhui
 * @Description:
 * @DateTime: 2026/3/15 16:28
 * @Component:
 */


public class SafeOutputGuardRail implements OutputGuardrail {

    private static final Set<String> sensitiveWords = Set.of(
            // 黑客攻击
            "sql注入",
            "ddos攻击教程",
            "暴力破解教程",
            "如何入侵网站",
            "网站漏洞利用",
            "服务器攻击方法",
            "木马制作教程",
            "病毒制作方法",

            // 违法内容
            "炸弹制作教程",
            "枪支制作教程",
            "毒品制作方法",

            // 仇恨 / 歧视
            "种族歧视",
            "地域歧视",
            "仇恨言论",
            "侮辱群体",

            // 隐私信息
            "身份证号",
            "银行卡号",
            "手机号",
            "家庭住址",
            "个人住址",
            "隐私信息",

            // 色情
            "色情内容",
            "成人影片",
            "性交易",
            "裸体图片");

    /**
     * 检测大模型输出是否安全
     */
    @Override
    public OutputGuardrailResult validate(AiMessage aiOutput){
        // 获取用户输入并转换为小写以确保大小写不敏感
        String outputText = aiOutput.text().toLowerCase();
        // 使用正则表达式分割输入文本为单词
//        String[] words = outputText.split("\\W+");
        // 遍历所有单词，检查是否存在敏感词
        for (String sensitiveWord : sensitiveWords) {// 直接通过敏感词去原句中匹配而不是将原句划分去敏感词中匹配
            if (outputText.contains(sensitiveWord)) {
                return fatal("Sensitive word detected: " + sensitiveWord);
            }
        }
        return success();
    }

}
