package com.example.chatserver.listener;

import com.example.chatserver.dto.ChatMessage;
import com.example.chatserver.service.ChatRoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Set;

/**
 * WebSocket Event Listener for handling connection lifecycle events.
 * 
 * This listener demonstrates event-driven network programming where the server
 * responds to connection state changes (connect, disconnect, subscribe).
 * 
 * Network Programming Concepts:
 * 1. Connection Lifecycle: TCP connections go through states (established, closed)
 * 2. Resource Cleanup: Server must release resources when clients disconnect
 * 3. Event-Driven Architecture: React to network events asynchronously
 * 4. Session Management: Track active connections and their metadata
 * 
 * WebSocket Connection Lifecycle:
 * 1. Client initiates WebSocket handshake (HTTP Upgrade)
 * 2. Server accepts → SessionConnectedEvent
 * 3. Client subscribes to topics → SessionSubscribeEvent
 * 4. Client disconnects (gracefully or abruptly) → SessionDisconnectEvent
 * 5. Server cleans up resources (room membership, session data)
 * 
 * Why Event Listeners?
 * - Decouples connection management from business logic
 * - Centralized handling of connection state changes
 * - Automatic cleanup when clients disconnect unexpectedly
 * - Logs provide visibility into server state
 * 
 * This is crucial for Member 1's deliverable on managing user join/leave events.
 * 
 * @author Team Member 1 - WebSocket Server & Connection Handling
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    /**
     * SimpMessagingTemplate for sending messages to clients.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * ChatRoomManager for cleaning up room membership.
     */
    private final ChatRoomManager chatRoomManager;

    /**
     * Handle WebSocket connection established event.
     * 
     * This event is fired when a client successfully completes the WebSocket handshake.
     * The handshake upgrades an HTTP connection to a persistent WebSocket connection.
     * 
     * At this point:
     * - TCP connection is established
     * - Client can send/receive STOMP frames
     * - Session ID is assigned
     * 
     * Network Concept:
     * This represents the transition from HTTP (request-response) to WebSocket
     * (full-duplex). The underlying TCP connection is kept alive for bidirectional
     * communication without the overhead of repeated HTTP handshakes.
     * 
     * @param event Session connected event containing session metadata
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("========================================");
        log.info("New WebSocket Connection Established");
        log.info("Session ID: {}", sessionId);
        log.info("User: {}", headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anonymous");
        log.info("========================================");
        
        // TODO (Member 3 - Security): Extract and validate JWT token from headers
        // String token = headerAccessor.getFirstNativeHeader("Authorization");
        // if (token != null && jwtService.validateToken(token)) {
        //     String username = jwtService.extractUsername(token);
        //     headerAccessor.getSessionAttributes().put("username", username);
        //     log.info("Authenticated user: {}", username);
        // } else {
        //     log.warn("Unauthenticated connection from session {}", sessionId);
        // }
        
        // Log connection statistics
        log.debug("Active connections: {} (Note: actual count requires session tracking)", sessionId);
    }

    /**
     * Handle WebSocket disconnect event.
     * 
     * This event is fired when:
     * - Client explicitly closes the connection
     * - Network failure (TCP connection lost)
     * - Server shutdown
     * - Session timeout
     * 
     * Critical for resource cleanup:
     * - Remove user from all chat rooms
     * - Notify other users about disconnection
     * - Release session resources
     * - Prevent memory leaks
     * 
     * Network Concept:
     * TCP connections can close gracefully (FIN handshake) or abruptly (RST packet).
     * This event handler ensures cleanup happens in both cases, preventing resource
     * leaks and maintaining consistent state.
     * 
     * Concurrency Note:
     * Multiple users can disconnect simultaneously. ChatRoomManager uses thread-safe
     * collections to handle concurrent cleanup operations safely.
     * 
     * @param event Session disconnect event containing session metadata
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Retrieve username from session attributes (stored during join or connection)
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
        
        log.info("========================================");
        log.info("WebSocket Connection Closed");
        log.info("Session ID: {}", sessionId);
        log.info("User: {}", username != null ? username : "Unknown");
        log.info("Close Status: {}", event.getCloseStatus());
        log.info("========================================");
        
        // Cleanup: Remove user from all rooms and notify other users
        if (username != null) {
            log.info("Cleaning up resources for user: {}", username);
            
            // Get all rooms user was in before cleanup
            Set<String> userRooms = chatRoomManager.getAllRooms().stream()
                .filter(room -> chatRoomManager.isUserInRoom(room, username))
                .collect(java.util.stream.Collectors.toSet());
            
            // Remove user from all rooms
            chatRoomManager.leaveAllRooms(username);
            
            // Send LEAVE notification to each room the user was in
            for (String room : userRooms) {
                ChatMessage leaveMessage = ChatMessage.builder()
                    .type(ChatMessage.MessageType.LEAVE)
                    .from(username)
                    .roomId(room)
                    .content(username + " left the room")
                    .build();
                
                leaveMessage.generateId();
                leaveMessage.setServerTimestamp();
                
                String roomTopic = "/topic/rooms." + room;
                messagingTemplate.convertAndSend(roomTopic, leaveMessage);
                
                log.info("Sent LEAVE notification to room {}", room);
            }
            
            // Also send a general disconnect notification to public topic
            ChatMessage disconnectMessage = ChatMessage.builder()
                .type(ChatMessage.MessageType.LEAVE)
                .from(username)
                .content(username + " disconnected")
                .build();
            
            disconnectMessage.generateId();
            disconnectMessage.setServerTimestamp();
            
            messagingTemplate.convertAndSend("/topic/public", disconnectMessage);
            
            log.info("User {} cleanup completed", username);
        } else {
            log.debug("No username associated with session {}, skipping room cleanup", sessionId);
        }
        
        // TODO (Member 5 - Persistence): Log disconnection event to database
        // sessionLogService.logDisconnect(sessionId, username, timestamp);
    }

    /**
     * Handle client subscription to a destination.
     * 
     * This event is fired when a client subscribes to a topic or queue.
     * Useful for tracking active subscriptions and debugging.
     * 
     * Subscriptions:
     * - /topic/public: Public chat messages
     * - /topic/rooms.{roomId}: Room-specific messages
     * - /user/queue/messages: Private messages
     * 
     * Network Concept:
     * This demonstrates the publish-subscribe pattern. Clients declare their
     * interest in specific message types (topics), and the server only sends
     * relevant messages to each client, reducing bandwidth usage.
     * 
     * @param event Session subscribe event containing subscription details
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        log.debug("Client subscribed - Session: {}, Destination: {}", sessionId, destination);
        
        // TODO (Advanced): Track active subscriptions for analytics
        // subscriptionTracker.recordSubscription(sessionId, destination);
    }
}
