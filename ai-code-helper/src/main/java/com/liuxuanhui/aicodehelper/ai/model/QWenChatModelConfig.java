package com.liuxuanhui.aicodehelper.ai.model;

import com.liuxuanhui.aicodehelper.ai.listener.ChatModelListenerConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @Author:Liu Xuanhui
 * @Description:
 * @DateTime: 2026/3/16 19:52
 * @Component:
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.community.dashscope.chat-model")
@Data
public class QWenChatModelConfig {

    private String modelName;
    private String apiKey;

    @Resource
    private ChatModelListener chatModelListener;
    @Bean
    @Primary // 容器中会存在两个bean，需要指定一个默认的chatmodel
    public ChatModel myQwenChatModel(){
        return QwenChatModel.builder()
                .modelName(modelName)
                .apiKey(apiKey)
                .listeners(List.of(chatModelListener))
                .build();
    }
}
