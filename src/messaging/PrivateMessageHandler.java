package messaging;

/**
 * MODULE 4: Private Chat Feature - Send direct messages (client-to-client routing via server)
 * Interface for handling private message routing
 * Team Member 4 should implement this functionality
 */
public interface PrivateMessageHandler {
    
    /**
     * Send a private message from one user to another
     * @param message The private message to send
     * @param sender The user sending the message
     * @param recipientId The ID of the recipient
     * @return true if message was delivered, false otherwise
     */
    boolean sendPrivateMessage(String message, String sender, String recipientId);
    
    /**
     * Check if a user is online and available for private messaging
     * @param userId The user ID to check
     * @return true if user is online, false otherwise
     */
    boolean isUserOnline(String userId);
    
    /**
     * Get the list of online users
     * @return Array of online user IDs
     */
    String[] getOnlineUsers();
}
