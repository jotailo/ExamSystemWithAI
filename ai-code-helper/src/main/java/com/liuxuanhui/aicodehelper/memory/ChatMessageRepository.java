//package com.liuxuanhui.aicodehelper.memory;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
//
//    /** 按会话 ID 查询所有消息，按顺序排列 */
//    List<ChatMessageEntity> findByMemoryIdOrderBySortOrder(String memoryId);
//
//    /** 查询某会话的消息数量 */
//    int countByMemoryId(String memoryId);
//
//    /** 删除某会话的所有消息 */
//    @Modifying
//    @Transactional
//    void deleteByMemoryId(String memoryId);
//
//    /** 删除某会话中最早的一条消息（用于维持窗口大小） */
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM ChatMessageEntity c WHERE c.id = " +
//           "(SELECT MIN(c2.id) FROM ChatMessageEntity c2 WHERE c2.memoryId = :memoryId)")
//    void deleteOldestByMemoryId(String memoryId);
//}
