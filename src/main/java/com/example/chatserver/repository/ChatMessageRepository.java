package com.example.chatserver.repository;

import com.example.chatserver.model.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for ChatMessageEntity.
 * 
 * This repository provides database access methods for chat message persistence.
 * Spring Data JPA automatically implements basic CRUD operations.
 * 
 * TODO (Member 5 - Persistence):
 * Implement additional query methods:
 * - findByRoomIdOrderByTimestampDesc: Get recent messages for a room
 * - findByFromUserOrToUserOrderByTimestampDesc: Get user's message history
 * - findByRoomIdAndTimestampBetween: Get messages in a date range
 * - countByRoomId: Get message count per room
 * - deleteByTimestampBefore: Implement message retention (delete old messages)
 * - findEncryptedMessages: Find all encrypted messages for migration/audit
 * 
 * @author Team Member 5 - Persistence
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    /**
     * Find all messages in a specific room, ordered by timestamp (newest first).
     * 
     * @param roomId Room identifier
     * @return List of messages
     */
    List<ChatMessageEntity> findByRoomIdOrderByTimestampDesc(String roomId);

    /**
     * Find all messages sent by a user, ordered by timestamp (newest first).
     * 
     * @param fromUser Username
     * @return List of messages
     */
    List<ChatMessageEntity> findByFromUserOrderByTimestampDesc(String fromUser);

    /**
     * Find private messages between two users.
     * 
     * @param user1 First username
     * @param user2 Second username
     * @return List of messages
     */
    @Query("SELECT m FROM ChatMessageEntity m WHERE " +
           "(m.fromUser = :user1 AND m.toUser = :user2) OR " +
           "(m.fromUser = :user2 AND m.toUser = :user1) " +
           "ORDER BY m.timestamp DESC")
    List<ChatMessageEntity> findPrivateMessagesBetweenUsers(@Param("user1") String user1, 
                                                              @Param("user2") String user2);

    /**
     * Find recent messages in a room (limit to last N messages).
     * 
     * @param roomId Room identifier
     * @param limit Maximum number of messages
     * @return List of messages
     */
    @Query(value = "SELECT * FROM chat_messages WHERE room_id = :roomId " +
                   "ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<ChatMessageEntity> findRecentMessagesByRoom(@Param("roomId") String roomId, 
                                                       @Param("limit") int limit);

    /**
     * Find messages by message ID (UUID).
     * 
     * @param messageId Message UUID
     * @return ChatMessageEntity or null
     */
    ChatMessageEntity findByMessageId(String messageId);

    /**
     * Count messages in a room.
     * 
     * @param roomId Room identifier
     * @return Message count
     */
    long countByRoomId(String roomId);

    /**
     * Find all encrypted messages (for audit or migration).
     * 
     * @return List of encrypted messages
     */
    List<ChatMessageEntity> findByEncryptedTrue();

    /**
     * Delete messages older than a certain timestamp (for retention policy).
     * 
     * @param timestamp Timestamp threshold (milliseconds since epoch)
     */
    void deleteByTimestampBefore(Long timestamp);
}
