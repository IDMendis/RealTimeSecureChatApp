package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MODULE 1: Server Setup - Handle multiple clients using ServerSocket + threads
 * Main server class that accepts client connections and manages client handlers
 * Team Member 1 should implement this class
 * 
 * Java Version: 17 LTS (Long Term Support)
 * Requires JDK 17 or higher
 */
public class ChatServer {
    
    private static final int DEFAULT_PORT = 8888;
    private static final int MAX_CLIENTS = 100;
    
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final Map<String, Object> connectedClients;  // Changed to Object temporarily
    private volatile boolean running = false;
    private final int port;
    
    public ChatServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.connectedClients = new ConcurrentHashMap<>();
    }
    
    /**
     * Start the server and begin accepting client connections
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("🚀 Chat Server started on port " + port);
            System.out.println("Waiting for client connections...");
            
            // Main accept loop
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    
                    // TODO: Create new client handler and submit to thread pool
                    // ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    // threadPool.execute(clientHandler);
                    
                    System.out.println("New client connection from: " + clientSocket.getRemoteSocketAddress());
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server on port " + port + ": " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * Stop the server and cleanup resources
     */
    public void stop() {
        running = false;
        
        // Close all client connections
        // TODO: Implement when ClientHandler is available
        // for (ClientHandler handler : connectedClients.values()) {
        //     handler.stop();
        // }
        connectedClients.clear();
        
        // Shutdown thread pool
        threadPool.shutdown();
        
        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        System.out.println("Server stopped");
    }
    
    /**
     * Add a client to the connected clients map
     */
    public void addClient(String userId, Object handler) {
        connectedClients.put(userId, handler);
        System.out.println("Client added: " + userId + ". Total clients: " + connectedClients.size());
    }
    
    /**
     * Remove a client from the connected clients map
     */
    public void removeClient(Object handler) {
        // TODO: Implementation by Team Member 1
        System.out.println("Client removed. Total clients: " + connectedClients.size());
    }
    
    /**
     * Broadcast a message to all connected clients
     * Used for group chat functionality
     */
    public void broadcastMessage(Object message) {
        // TODO: Implementation by Team Member 1
        System.out.println("Broadcasting message to " + connectedClients.size() + " clients");
    }
    
    /**
     * Send a private message to a specific client
     * MODULE 4: Private chat feature - Send direct messages (client-to-client routing via server)
     */
    public void sendPrivateMessage(Object message, String recipientId) {
        // TODO: Implementation by Team Member 4
        Object recipient = connectedClients.get(recipientId);
        if (recipient != null) {
            try {
                // recipient.sendMessage(message);
                System.out.println("Sending private message to: " + recipientId);
            } catch (Exception e) {
                System.err.println("Failed to send private message to " + recipientId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Get count of connected clients
     */
    public int getConnectedClientsCount() {
        return connectedClients.size();
    }
    
    /**
     * Main method to start the server
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        // Allow port to be specified as command line argument
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }
        
        ChatServer server = new ChatServer(port);
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
        }));
        
        server.start();
    }
}