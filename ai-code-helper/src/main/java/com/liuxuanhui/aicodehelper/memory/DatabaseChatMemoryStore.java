//package com.liuxuanhui.aicodehelper.memory;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import dev.langchain4j.data.message.AiMessage;
//import dev.langchain4j.data.message.ChatMessage;
//import dev.langchain4j.data.message.SystemMessage;
//import dev.langchain4j.data.message.UserMessage;
//import dev.langchain4j.store.memory.chat.ChatMemoryStore;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 基于 MySQL 的聊天记忆持久化存储
// * 实现 LangChain4j 的 ChatMemoryStore 接口
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DatabaseChatMemoryStore implements ChatMemoryStore {
//
//    private final ChatMessageRepository repository;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public List<ChatMessage> getMessages(Object memoryId) {
//        String id = memoryId.toString();
//        return repository.findByMemoryIdOrderBySortOrder(id)
//                .stream()
//                .map(this::deserialize)
//                .toList();
//    }
//
//    @Override
//    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
//        String id = memoryId.toString();
//        // 全量替换：先删除旧消息，再写入新消息
//        repository.deleteByMemoryId(id);
//        for (int i = 0; i < messages.size(); i++) {
//            ChatMessage msg = messages.get(i);
//            String json = serialize(msg);
//            String type = msg.type().name(); // USER / AI / SYSTEM
//            repository.save(new ChatMessageEntity(id, type, json, i));
//        }
//    }
//
//    @Override
//    public void deleteMessages(Object memoryId) {
//        repository.deleteByMemoryId(memoryId.toString());
//    }
//
//    // ---- 序列化 / 反序列化 ----
//
//    private String serialize(ChatMessage message) {
//        try {
//            return objectMapper.writeValueAsString(Map.of(
//                    "type", message.type().name(),
//                    "text", extractText(message)
//            ));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("序列化消息失败", e);
//        }
//    }
//
//    private ChatMessage deserialize(ChatMessageEntity entity) {
//        try {
//            Map<?, ?> map = objectMapper.readValue(entity.getMessageJson(), Map.class);
//            String text = (String) map.get("text");
//            return switch (entity.getMessageType()) {
//                case "USER" -> UserMessage.from(text);
//                case "AI" -> AiMessage.from(text);
//                case "SYSTEM" -> SystemMessage.from(text);
//                default -> throw new IllegalArgumentException("未知消息类型: " + entity.getMessageType());
//            };
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("反序列化消息失败", e);
//        }
//    }
//
//    private String extractText(ChatMessage message) {
//        return switch (message) {
//            case UserMessage um -> um.singleText();
//            case AiMessage am -> am.text();
//            case SystemMessage sm -> sm.text();
//            default -> message.toString();
//        };
//    }
//}
