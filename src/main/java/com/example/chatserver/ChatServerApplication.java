package com.example.chatserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main Spring Boot Application class for the Real-Time Secure Chat Server.
 * 
 * This application demonstrates TCP-based communication using WebSockets over Spring Boot.
 * WebSockets run over TCP/IP and provide full-duplex communication channels over a single TCP connection.
 * 
 * Key Network Programming Concepts:
 * 1. TCP Connection: WebSocket handshake establishes a persistent TCP connection
 * 2. Server-Client Architecture: This server acts as the central hub for multiple clients
 * 3. Multithreading: Spring handles each WebSocket session in separate threads
 * 4. Concurrent Session Management: Multiple users can connect simultaneously
 * 
 * Architecture:
 * - WebSocket Endpoint: /ws (with SockJS fallback for older browsers)
 * - STOMP Protocol: Text-oriented messaging protocol over WebSocket
 * - Message Routing: Messages are routed to different destinations (/topic, /user, /app)
 * 
 * @author Team Member 1 - WebSocket Server & Connection Handling
 */
@Slf4j
@SpringBootApplication
public class ChatServerApplication {

    /**
     * Main entry point for the Spring Boot application.
     * Starts the embedded Tomcat server and initializes all Spring beans.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        log.info("========================================");
        log.info("Starting Real-Time Secure Chat Server");
        log.info("========================================");
        SpringApplication.run(ChatServerApplication.class, args);
    }

    /**
     * CommandLineRunner bean that executes after the application context is loaded.
     * Prints useful startup information and available endpoints for developers.
     * 
     * This demonstrates the application lifecycle and provides visibility into
     * the server's network configuration and available endpoints.
     * 
     * @return CommandLineRunner instance
     */
    @Bean
    public CommandLineRunner startupRunner() {
        return args -> {
            log.info("========================================");
            log.info("Chat Server Started Successfully!");
            log.info("========================================");
            
            try {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                String hostName = InetAddress.getLocalHost().getHostName();
                
                log.info("Server Information:");
                log.info("  Host Name: {}", hostName);
                log.info("  IP Address: {}", hostAddress);
                log.info("  Port: 8080");
                log.info("");
                
                log.info("Available Endpoints:");
                log.info("  WebSocket Endpoint: ws://{}:8080/ws", hostAddress);
                log.info("  WebSocket with SockJS: http://{}:8080/ws", hostAddress);
                log.info("  Test Client: http://{}:8080/test-client.html", hostAddress);
                log.info("  H2 Console: http://{}:8080/h2-console", hostAddress);
                log.info("");
                
                log.info("STOMP Destinations:");
                log.info("  Send Message: /app/chat.send");
                log.info("  Join Room: /app/chat.join");
                log.info("  Public Topic: /topic/public");
                log.info("  Room Topics: /topic/rooms.{roomId}");
                log.info("  Private Messages: /user/queue/messages");
                log.info("");
                
                log.info("Testing Instructions:");
                log.info("  1. Open http://{}:8080/test-client.html in your browser", hostAddress);
                log.info("  2. Open 2-3 more tabs with the same URL to simulate multiple clients");
                log.info("  3. Connect each client and test public/room/private messaging");
                log.info("");
                
                log.info("Network Programming Concepts Demonstrated:");
                log.info("  • TCP Connection Management (WebSocket over TCP)");
                log.info("  • Server-Client Architecture (Hub-and-Spoke Model)");
                log.info("  • Multithreading (Concurrent Session Handling)");
                log.info("  • Thread-Safe Collections (ConcurrentHashMap for rooms)");
                log.info("  • Event-Driven Communication (Session Connect/Disconnect)");
                log.info("");
                
                log.info("TODO for Team Members:");
                log.info("  Member 3 (Security): Implement JWT authentication in WebSocketConfig");
                log.info("  Member 3 (Encryption): Add AES/RSA encryption in ChatController");
                log.info("  Member 4 (File Transfer): Implement file transfer with NIO");
                log.info("  Member 5 (Persistence): Extend ChatMessageRepository for queries");
                
            } catch (UnknownHostException e) {
                log.error("Unable to determine host information", e);
            }
            
            log.info("========================================");
            log.info("Server is ready to accept connections!");
            log.info("========================================");
        };
    }
}
