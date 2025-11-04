package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private volatile boolean running = false;
    
    private final String serverHost;
    private final int serverPort;
    
    public ChatClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }
    
    public boolean connect(String username) {
        try {
            this.username = username;
            socket = new Socket(serverHost, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println("CONNECT:" + username);
            
            running = true;
            System.out.println("Connected to server at " + serverHost + ":" + serverPort);
            
            Thread listenerThread = new Thread(this::listenForMessages);
            listenerThread.setDaemon(true);
            listenerThread.start();
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }
    
    private void listenForMessages() {
        while (running) {
            try {
                String message = in.readLine();
                if (message != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("Connection lost: " + e.getMessage());
                    disconnect();
                }
                break;
            }
        }
    }
    
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
    
    public void sendGroupMessage(String content) {
        sendMessage("GROUP:" + content);
    }
    
    public void sendPrivateMessage(String content, String recipientId) {
        sendMessage("PRIVATE:" + recipientId + ":" + content);
    }
    
    public void disconnect() {
        running = false;
        try {
            if (username != null) {
                sendMessage("DISCONNECT:" + username);
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Real-Time Secure Chat Application");
        System.out.println("Client v1.0.0");
        System.out.println();
        
        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            username = "User" + System.currentTimeMillis();
        }
        
        ChatClient client = new ChatClient(DEFAULT_HOST, DEFAULT_PORT);
        
        if (!client.connect(username)) {
            System.err.println("Failed to connect to server. Make sure the server is running.");
            System.exit(1);
        }
        
        System.out.println("Type message and press Enter to send");
        System.out.println("/quit - Exit chat");
        System.out.println();
        
        while (client.running) {
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            if (input.equalsIgnoreCase("/quit")) {
                break;
            } else {
                client.sendGroupMessage(input);
            }
        }
        
        client.disconnect();
        scanner.close();
    }
}
