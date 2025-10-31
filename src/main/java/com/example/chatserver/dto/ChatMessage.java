package com.example.chatserver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for chat messages.
 * 
 * This class represents a message exchanged between clients through the WebSocket server.
 * Messages are serialized to JSON for transmission over the WebSocket connection.
 * 
 * Message Types:
 * - CHAT: Regular chat message (public, room, or private)
 * - JOIN: User joining a room or the chat
 * - LEAVE: User leaving a room or the chat
 * - PRIVATE: Direct message to a specific user
 * 
 * Routing Logic:
 * - If 'to' is set: Route to specific user via /user/{to}/queue/messages
 * - If 'roomId' is set: Route to room via /topic/rooms.{roomId}
 * - Otherwise: Route to public topic via /topic/public
 * 
 * Network Programming Note:
 * This DTO demonstrates data serialization for network transmission. The object
 * is converted to JSON (text format) and sent as STOMP frames over the TCP connection.
 * 
 * @author Team Member 1 - WebSocket Server & Connection Handling
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /**
     * Unique identifier for the message.
     * Generated server-side using UUID to ensure uniqueness across distributed systems.
     */
    private String id;

    /**
     * Type of message (CHAT, JOIN, LEAVE, PRIVATE).
     */
    private MessageType type;

    /**
     * Username of the sender.
     * Set by the client or extracted from authentication token.
     * TODO (Member 3 - Security): Extract from JWT token instead of trusting client
     */
    private String from;

    /**
     * Username of the recipient (for private messages).
     * If null, message is public or room-scoped.
     */
    private String to;

    /**
     * Room identifier (for room-scoped messages).
     * If null, message is public or private.
     * Examples: "general", "team-alpha", "project-discussion"
     */
    private String roomId;

    /**
     * Message content (text).
     * TODO (Member 3 - Encryption): Encrypt this field using AES before transmission
     * and decrypt on the client side using a shared key or RSA key exchange.
     */
    private String content;

    /**
     * Timestamp when the message was sent (milliseconds since epoch).
     * Set server-side to ensure consistency and prevent client manipulation.
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long timestamp;

    /**
     * Metadata map for extensibility.
     * Can store additional information like:
     * - encrypted: "true" (indicates if content is encrypted)
     * - iv: "base64-encoded-initialization-vector" (for AES decryption)
     * - fileUrl: "https://..." (for file attachments)
     * - fileName: "document.pdf" (original filename)
     * - fileSize: "1024576" (file size in bytes)
     * 
     * TODO (Member 3 - Encryption): Store encryption metadata here
     * TODO (Member 4 - File Transfer): Store file metadata here
     */
    @Builder.Default
    private Map<String, String> meta = new HashMap<>();

    /**
     * Generate a new message ID using UUID.
     * Called server-side to ensure unique message identification.
     */
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Set server timestamp to current time.
     * Called server-side to ensure accurate and consistent timestamps.
     */
    public void setServerTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Check if this is a private message (targeted to a specific user).
     * @return true if the message has a recipient
     */
    public boolean isPrivate() {
        return this.to != null && !this.to.isEmpty();
    }

    /**
     * Check if this is a room-scoped message.
     * @return true if the message belongs to a specific room
     */
    public boolean isRoomMessage() {
        return this.roomId != null && !this.roomId.isEmpty();
    }

    /**
     * Add metadata entry.
     * @param key Metadata key
     * @param value Metadata value
     */
    public void addMeta(String key, String value) {
        if (this.meta == null) {
            this.meta = new HashMap<>();
        }
        this.meta.put(key, value);
    }

    /**
     * Enum representing different message types.
     * This allows the client to handle different types of messages appropriately
     * (e.g., display JOIN messages differently from CHAT messages).
     */
    public enum MessageType {
        /**
         * Regular chat message (text content).
         */
        CHAT,

        /**
         * User joined the chat or a room.
         */
        JOIN,

        /**
         * User left the chat or a room.
         */
        LEAVE,

        /**
         * Private message to a specific user.
         */
        PRIVATE
    }
}
