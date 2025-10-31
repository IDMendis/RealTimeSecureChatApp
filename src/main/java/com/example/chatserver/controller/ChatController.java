package com.example.chatserver.controller;

import com.example.chatserver.dto.ChatMessage;
import com.example.chatserver.service.ChatRoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for handling chat messages.
 * 
 * This controller demonstrates the server-side message handling in a WebSocket application.
 * Messages are received from clients via STOMP protocol and routed to appropriate destinations.
 * 
 * Network Programming Concepts:
 * 1. Message Routing: Server decides where to route each message based on content
 * 2. Broadcast vs Unicast: Messages can be sent to all users or specific users
 * 3. Topic-based Messaging: Clients subscribe to topics and receive relevant messages
 * 4. Session Management: Each WebSocket connection has a unique session ID
 * 
 * Message Routing Logic:
 * - Private message (to != null): → /user/{username}/queue/messages
 * - Room message (roomId != null): → /topic/rooms.{roomId}
 * - Public message: → /topic/public
 * 
 * @MessageMapping:
 * - Maps STOMP messages from clients to handler methods
 * - Client sends to /app/chat.send → routed to handleChatMessage()
 * - Similar to @RequestMapping in REST controllers
 * 
 * @author Team Member 1 - WebSocket Server & Connection Handling
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    /**
     * SimpMessagingTemplate for sending messages to clients.
     * This is the primary way to send messages from server to clients.
     * 
     * Methods:
     * - convertAndSend(destination, payload): Broadcast to all subscribers
     * - convertAndSendToUser(user, destination, payload): Send to specific user
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * ChatRoomManager for managing chat rooms and members.
     */
    private final ChatRoomManager chatRoomManager;

    /**
     * Handle incoming chat messages from clients.
     * 
     * This method is invoked when a client sends a message to /app/chat.send.
     * The message is processed, validated, and routed to the appropriate destination.
     * 
     * Routing Logic:
     * 1. If message.to is set → Send to specific user (private message)
     * 2. Else if message.roomId is set → Broadcast to room members
     * 3. Else → Broadcast to all connected users (public message)
     * 
     * Network Concept:
     * This demonstrates message routing in a publish-subscribe system. The server
     * acts as an intelligent router, directing messages based on their content and
     * destination. This is more efficient than peer-to-peer connections where each
     * client would need to maintain connections to all other clients.
     * 
     * @param chatMessage The message sent by the client
     * @param headerAccessor Access to STOMP message headers (session info, user info)
     */
    @MessageMapping("/chat.send")
    public void handleChatMessage(@Payload ChatMessage chatMessage, 
                                   SimpMessageHeaderAccessor headerAccessor) {
        
        // Extract session information
        String sessionId = headerAccessor.getSessionId();
        
        // TODO (Member 3 - Security): Extract username from JWT token in header
        // String username = jwtService.extractUsername(headerAccessor.getFirstNativeHeader("Authorization"));
        // chatMessage.setFrom(username);
        
        log.info("Received message from session {}: type={}, from={}, to={}, roomId={}", 
                 sessionId, chatMessage.getType(), chatMessage.getFrom(), 
                 chatMessage.getTo(), chatMessage.getRoomId());
        
        // Set server-side metadata
        chatMessage.generateId();
        chatMessage.setServerTimestamp();
        
        // Basic validation
        if (chatMessage.getFrom() == null || chatMessage.getFrom().isEmpty()) {
            log.warn("Message rejected: 'from' field is required");
            return;
        }
        
        if (chatMessage.getContent() == null || chatMessage.getContent().isEmpty()) {
            log.warn("Message rejected: 'content' field is required");
            return;
        }
        
        // TODO (Member 3 - Encryption): Encrypt message content before routing
        // if (encryptionService.isEnabled()) {
        //     String encryptedContent = encryptionService.encrypt(chatMessage.getContent());
        //     chatMessage.setContent(encryptedContent);
        //     chatMessage.addMeta("encrypted", "true");
        //     chatMessage.addMeta("iv", encryptionService.getIV());
        // }
        
        // Route message based on destination
        if (chatMessage.isPrivate()) {
            // Private message to specific user
            log.info("Routing private message from {} to {}", chatMessage.getFrom(), chatMessage.getTo());
            messagingTemplate.convertAndSendToUser(
                chatMessage.getTo(), 
                "/queue/messages", 
                chatMessage
            );
            
            // Also send a copy back to sender for confirmation
            messagingTemplate.convertAndSendToUser(
                chatMessage.getFrom(), 
                "/queue/messages", 
                chatMessage
            );
            
        } else if (chatMessage.isRoomMessage()) {
            // Room-scoped message
            String roomTopic = "/topic/rooms." + chatMessage.getRoomId();
            log.info("Broadcasting message to room topic: {}", roomTopic);
            messagingTemplate.convertAndSend(roomTopic, chatMessage);
            
        } else {
            // Public message to all users
            log.info("Broadcasting message to public topic");
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
        
        // TODO (Member 5 - Persistence): Save message to database asynchronously
        // chatMessageService.saveAsync(chatMessage);
        
        log.debug("Message routed successfully: id={}", chatMessage.getId());
    }

    /**
     * Handle user joining a chat room.
     * 
     * When a user joins a room, this method:
     * 1. Adds the user to the room in ChatRoomManager
     * 2. Broadcasts a JOIN notification to all room members
     * 3. Returns the current room member list to the client
     * 
     * This demonstrates event-driven communication where user actions trigger
     * notifications to other connected clients.
     * 
     * @param chatMessage Join message (type=JOIN, roomId required, from=username)
     * @param headerAccessor Access to STOMP message headers
     */
    @MessageMapping("/chat.join")
    public void handleJoinRoom(@Payload ChatMessage chatMessage, 
                               SimpMessageHeaderAccessor headerAccessor) {
        
        String sessionId = headerAccessor.getSessionId();
        String username = chatMessage.getFrom();
        String roomId = chatMessage.getRoomId();
        
        log.info("User {} (session {}) joining room {}", username, sessionId, roomId);
        
        // Validation
        if (username == null || username.isEmpty()) {
            log.warn("Join rejected: 'from' field is required");
            return;
        }
        
        if (roomId == null || roomId.isEmpty()) {
            log.warn("Join rejected: 'roomId' field is required");
            return;
        }
        
        // Add user to room
        chatRoomManager.joinRoom(roomId, username);
        
        // Store username in WebSocket session attributes for disconnect handling
        headerAccessor.getSessionAttributes().put("username", username);
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        
        // Prepare JOIN notification
        ChatMessage joinNotification = ChatMessage.builder()
            .type(ChatMessage.MessageType.JOIN)
            .from(username)
            .roomId(roomId)
            .content(username + " joined the room")
            .build();
        
        joinNotification.generateId();
        joinNotification.setServerTimestamp();
        
        // Broadcast JOIN notification to all room members
        String roomTopic = "/topic/rooms." + roomId;
        messagingTemplate.convertAndSend(roomTopic, joinNotification);
        
        log.info("User {} joined room {}. Current members: {}", 
                 username, roomId, chatRoomManager.getMembers(roomId));
    }
}
