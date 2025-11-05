package main.java.com.securechat.server;

import server.ChatServer;

import java.io.*;
import java.net.Socket;

/**
 * MODULE 1: Server Setup - Handle multiple clients using ServerSocket + threads
 * Handles communication with a single client in a separate thread
 * Team Member 1 should implement this class
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // Initialize streams
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Client connected from: " + clientSocket.getRemoteSocketAddress());

            // Main message handling loop
            while (running) {
                try {
                    String message = in.readLine();
                    if (message != null) {
                        handleMessage(message);
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    System.err.println("Error reading message: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Process incoming message based on type
     * Protocol: TYPE:data
     * CONNECT:username
     * GROUP:message
     * PRIVATE:recipient:message
     * DISCONNECT:username
     */
    private void handleMessage(String message) {
        System.out.println("Received message: " + message);
        
        String[] parts = message.split(":", 2);
        if (parts.length < 1) return;
        
        String type = parts[0];
        
        switch (type) {
            case "CONNECT":
                if (parts.length > 1) {
                    this.username = parts[1];
                    System.out.println("User connected: " + username);
                }
                break;
            case "GROUP":
                if (parts.length > 1 && username != null) {
                    server.broadcastMessage(username + ": " + parts[1]);
                }
                break;
            case "PRIVATE":
                String[] privateParts = parts[1].split(":", 2);
                if (privateParts.length == 2 && username != null) {
                    server.sendPrivateMessage(username + " (private): " + privateParts[1], privateParts[0]);
                }
                break;
            case "DISCONNECT":
                running = false;
                break;
        }
    }

    /**
     * Send a message to this client
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    /**
     * Get the username associated with this client handler
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username for this client handler
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Stop the client handler
     */
    public void stop() {
        running = false;
    }

    /**
     * Cleanup resources
     */
    private void cleanup() {
        try {
            running = false;
            if (username != null) {
                server.removeClient(this);
                System.out.println("Client disconnected: " + username);
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}
