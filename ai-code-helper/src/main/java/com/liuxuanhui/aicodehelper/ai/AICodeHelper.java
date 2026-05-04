package com.liuxuanhui.aicodehelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author:Liu Xuanhui
 * @Description: 多模态和系统提示词
 * @DateTime: 2026/1/13 17:39
 * @Component:
 */

@Service
@Slf4j
public class AICodeHelper {

    @Resource
    private ChatModel qwenChatModel;

    // 告诉 AI “你是谁？你能做什么？”
    private static final String SYSTEM_MESSAGE = """
        你是编程领域的小助手，帮助用户解答编程学习和求职面试相关的问题，并给出建议。重点关注 4 个方向：
        1. 规划清晰的编程学习路线
        2. 提供项目学习建议
        3. 给出程序员求职全流程指南（比如简历优化、投递技巧）
        4. 分享高频面试题和面试技巧
        请用简洁易懂的语言回答，助力用户高效学习与求职。
        """;

    // 实现和大模型对话，只限文本内容
    public String chat(String message){
        SystemMessage systemMessage = SystemMessage.from(SYSTEM_MESSAGE);
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse chatResponse = qwenChatModel.chat(userMessage, systemMessage);// 传入用户的消息以及设置 AI 模型行为规则和角色定位的隐藏指令
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI 输出：" + aiMessage.toString());
        return aiMessage.text();
    }

    // 实现多模态大模型对话，包括识别图片等不同类型的数据
    public String chatWithOtherType(UserMessage userMessage){
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI 输出：" + aiMessage.toString());
        return aiMessage.text();
    }
}
