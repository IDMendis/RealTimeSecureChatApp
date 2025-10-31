package com.example.chatserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity for persisting chat messages to the database.
 * 
 * This entity demonstrates database persistence in a real-time application.
 * Messages are stored asynchronously to avoid blocking the message flow.
 * 
 * Persistence Strategy:
 * - Real-time: Messages are sent immediately via WebSocket
 * - Persistence: Messages are saved to DB asynchronously for history
 * - Query: Users can retrieve message history when connecting
 * 
 * TODO (Member 5 - Persistence):
 * - Implement message history retrieval by room/user/date range
 * - Add indexes on frequently queried columns (roomId, fromUser, timestamp)
 * - Implement message search functionality
 * - Add support for message editing and deletion (soft delete)
 * - Implement message retention policies (delete old messages)
 * 
 * Encryption Note:
 * - If encrypted=true, the content field contains encrypted data
 * - The iv (initialization vector) and other crypto metadata are stored in meta fields
 * - TODO (Member 3): Implement encryption before persistence
 * 
 * @author Team Member 5 - Persistence
 */
@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_room_timestamp", columnList = "room_id, timestamp"),
    @Index(name = "idx_from_timestamp", columnList = "from_user, timestamp"),
    @Index(name = "idx_to_timestamp", columnList = "to_user, timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {

    /**
     * Primary key (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Message UUID (from ChatMessage DTO).
     */
    @Column(nullable = false, unique = true)
    private String messageId;

    /**
     * Message type (CHAT, JOIN, LEAVE, PRIVATE).
     */
    @Column(nullable = false, length = 20)
    private String messageType;

    /**
     * Sender username.
     */
    @Column(name = "from_user", nullable = false, length = 100)
    private String fromUser;

    /**
     * Recipient username (for private messages, null otherwise).
     */
    @Column(name = "to_user", length = 100)
    private String toUser;

    /**
     * Room identifier (for room messages, null otherwise).
     */
    @Column(name = "room_id", length = 100)
    private String roomId;

    /**
     * Message content (plain text or encrypted).
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Message timestamp (milliseconds since epoch).
     */
    @Column(nullable = false)
    private Long timestamp;

    /**
     * Flag indicating if content is encrypted.
     * TODO (Member 3 - Encryption): Set to true when storing encrypted messages
     */
    @Column(nullable = false)
    private Boolean encrypted = false;

    /**
     * Initialization Vector (IV) for AES decryption (Base64 encoded).
     * Only populated if encrypted=true.
     */
    @Column(length = 255)
    private String iv;

    /**
     * Additional metadata (JSON string or key-value pairs).
     * Can store file URLs, encryption keys, or other extensible data.
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    /**
     * Timestamp when the record was created in the database.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    /**
     * Set createdAt before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
    }
}
