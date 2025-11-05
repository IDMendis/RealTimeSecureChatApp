package main.java.com.securechat.common;

/**
 * Enum representing different types of messages in the chat system
 */
public enum MessageType {
    // Client -> Server
    CONNECT,        // Client connection request
    DISCONNECT,     // Client disconnect request
    GROUP_MESSAGE,  // Message to all clients
    PRIVATE_MESSAGE, // Direct message to specific client
    
    // Server -> Client
    CONNECT_ACK,    // Connection acknowledgment
    USER_LIST,      // List of connected users
    SERVER_MESSAGE, // Server notification
    ERROR,          // Error message
    
    // General
    HEARTBEAT       // Keep-alive message
}
