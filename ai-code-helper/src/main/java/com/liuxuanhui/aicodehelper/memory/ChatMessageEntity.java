//package com.liuxuanhui.aicodehelper.memory;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * 持久化聊天消息的数据库实体
// */
//@Entity
//@Table(name = "chat_messages")
//@Data
//@NoArgsConstructor
//public class ChatMessageEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    /** 会话 ID，用于区分不同用户 */
//    @Column(name = "memory_id", nullable = false)
//    private String memoryId;
//
//    /** 消息类型：USER / AI / SYSTEM */
//    @Column(name = "message_type", nullable = false)
//    private String messageType;
//
//    /** 消息内容，序列化为 JSON 存储 */
//    @Column(name = "message_json", nullable = false, columnDefinition = "TEXT")
//    private String messageJson;
//
//    /** 消息顺序，用于按序还原对话 */
//    @Column(name = "sort_order", nullable = false)
//    private int sortOrder;
//
//    public ChatMessageEntity(String memoryId, String messageType, String messageJson, int sortOrder) {
//        this.memoryId = memoryId;
//        this.messageType = messageType;
//        this.messageJson = messageJson;
//        this.sortOrder = sortOrder;
//    }
//}
