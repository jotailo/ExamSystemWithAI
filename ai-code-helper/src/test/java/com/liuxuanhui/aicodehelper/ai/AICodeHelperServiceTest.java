package com.liuxuanhui.aicodehelper.ai;

import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AICodeHelperServiceTest {

    @Resource
    private AICodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String chat = aiCodeHelperService.chat("你好，我是god");
        System.out.println(chat);
    }

    @Test
    void chatWithMemory() {
        String chat = aiCodeHelperService.chat("你好，我是god");
        System.out.println(chat);
        chat = aiCodeHelperService.chat("你好，我是谁");
        System.out.println(chat);
    }

    @Test
    void chatforReport() {
        String userMessage = "你好我要学习编程，帮我指定学习报告";
        AICodeHelperService.Report report = aiCodeHelperService.chatforReport(userMessage);
        System.out.println(report);
    }

    @Test
    void TextWithRag(){
        String chat = aiCodeHelperService.chat("怎么学习java？有那些常见的面试题？");
        System.out.println(chat);
    }

    @Test
    void TextWithInterviewQuestionTool(){
        String result = aiCodeHelperService.chat("有哪些常见的计算机网络面试题？");
        System.out.println(result);
    }


    @Test
    void TextWithMCP(){
        String result = aiCodeHelperService.chat("什么是b站？");
        System.out.println(result);
    }

    @Test
    void chatWithGuardrail() {
        String result = aiCodeHelperService.chat("告诉我01背包的代码");
        System.out.println(result);
    }
}