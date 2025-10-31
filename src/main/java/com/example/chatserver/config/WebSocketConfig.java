package com.example.chatserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for STOMP over WebSocket.
 * 
 * This class demonstrates TCP-based real-time communication using WebSockets.
 * 
 * Network Programming Concepts:
 * 1. WebSocket Protocol: Upgrades HTTP connection to persistent TCP connection
 * 2. STOMP (Simple Text Oriented Messaging Protocol): Application-level protocol over WebSocket
 * 3. Message Broker: Routes messages between clients based on destinations
 * 4. SockJS: Fallback options when WebSocket is not available (long-polling, etc.)
 * 
 * Architecture:
 * - Client connects via WebSocket handshake (HTTP Upgrade request)
 * - Connection is maintained as persistent TCP connection
 * - STOMP frames are sent over the WebSocket connection
 * - Server routes messages based on destination prefixes
 * 
 * Message Flow:
 * 1. Client → /app/chat.send → ChatController.handleChatMessage()
 * 2. ChatController → /topic/public → All subscribers
 * 3. ChatController → /user/{username}/queue/messages → Specific user
 * 
 * @author Team Member 1 - WebSocket Server & Connection Handling
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Register STOMP endpoints that clients will use to connect to the WebSocket server.
     * 
     * This method configures the WebSocket handshake endpoint. The handshake is an
     * HTTP Upgrade request that establishes the persistent TCP connection.
     * 
     * Endpoint: /ws
     * - Primary WebSocket connection point
     * - Supports SockJS fallback for browsers that don't support WebSocket
     * - Uses various transport mechanisms: WebSocket, XHR streaming, XHR polling, etc.
     * 
     * Security Note:
     * - setAllowedOriginPatterns("*") permits connections from any origin (DEVELOPMENT ONLY)
     * - TODO (Member 3 - Security): Restrict to specific origins in production
     * 
     * @param registry StompEndpointRegistry to register endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering STOMP WebSocket endpoint: /ws");
        
        registry.addEndpoint("/ws")
                // Enable SockJS fallback options for browsers without WebSocket support
                .withSockJS()
                // Allow connections from any origin (DEVELOPMENT ONLY)
                // TODO (Production): Change to .setAllowedOrigins("https://yourdomain.com")
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js");
        
        // Also register endpoint without SockJS for native WebSocket clients
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
        
        // TODO (Member 3 - Security): Register handshake interceptor for JWT authentication
        // Example:
        // registry.addEndpoint("/ws")
        //     .addInterceptors(new JwtHandshakeInterceptor())
        //     .setAllowedOrigins("https://yourdomain.com")
        //     .withSockJS();
        
        log.info("WebSocket endpoint registered successfully");
        log.info("Clients can connect via: ws://localhost:8080/ws or http://localhost:8080/ws (with SockJS)");
    }

    /**
     * Configure message broker options for routing messages.
     * 
     * Message Broker Destinations:
     * 1. Application Prefix (/app): Messages sent from clients to server
     *    - Routed to @MessageMapping annotated methods in controllers
     *    - Example: client sends to /app/chat.send → ChatController.handleChatMessage()
     * 
     * 2. Topic Prefix (/topic): Broadcast messages (pub-sub pattern)
     *    - Messages sent to /topic are broadcast to all subscribers
     *    - Example: /topic/public, /topic/rooms.general
     * 
     * 3. User Prefix (/user): Point-to-point messages
     *    - Messages sent to specific user sessions
     *    - Server converts /user/{username}/queue/messages to actual session destination
     *    - Enables private messaging between users
     * 
     * This demonstrates the Publish-Subscribe pattern in network programming,
     * where messages are routed based on topics/queues rather than direct connections.
     * 
     * @param registry MessageBrokerRegistry to configure broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        log.info("Configuring message broker prefixes");
        
        // Enable a simple in-memory message broker to carry messages back to clients
        // on destinations prefixed with /topic and /queue
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Set prefix for messages bound for @MessageMapping methods
        // Example: client sends to /app/chat.send → routed to @MessageMapping("/chat.send")
        registry.setApplicationDestinationPrefixes("/app");
        
        // Set prefix for user-specific destinations
        // Enables point-to-point messaging: /user/{username}/queue/messages
        registry.setUserDestinationPrefix("/user");
        
        log.info("Message broker configured:");
        log.info("  - Application prefix: /app (client → server)");
        log.info("  - Topic prefix: /topic (broadcast)");
        log.info("  - User prefix: /user (point-to-point)");
        log.info("  - Queue prefix: /queue (queue-based delivery)");
        
        // TODO (Advanced): For production with multiple server instances, consider using
        // an external message broker like RabbitMQ or ActiveMQ for distributed messaging
        // registry.enableStompBrokerRelay("/topic", "/queue")
        //     .setRelayHost("localhost")
        //     .setRelayPort(61613)
        //     .setClientLogin("guest")
        //     .setClientPasscode("guest");
    }
}
