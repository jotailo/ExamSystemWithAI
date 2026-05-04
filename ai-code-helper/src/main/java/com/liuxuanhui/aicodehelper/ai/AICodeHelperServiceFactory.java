package com.liuxuanhui.aicodehelper.ai;

import com.liuxuanhui.aicodehelper.ai.mcp.MCPConfig;
import com.liuxuanhui.aicodehelper.ai.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author:Liu Xuanhui
 * @Description:
 * @DateTime: 2026/1/26 15:41
 * @Component:
 */

@Configuration
public class AICodeHelperServiceFactory {

    @Resource
    private ChatModel myqwenChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Bean
    public AICodeHelperService aiCodeHelperService() {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 构造aiservice
        AICodeHelperService aiCodeHelperService = AiServices.builder(AICodeHelperService.class)
                .chatModel(myqwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(chatMemory)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.withMaxMessages(10)) // 每个会话独立存储
//                .contentRetriever(contentRetriever) // RAG 检索增强生成
                .tools(new InterviewQuestionTool()) // 工具调用
                .toolProvider(mcpToolProvider) // MCP 工具调用
                .build();
        return aiCodeHelperService;
    }
}
