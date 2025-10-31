package com.example.chatserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ChatRoomManager.
 * 
 * These tests demonstrate concurrent access patterns and thread-safety.
 * 
 * Key Test Scenarios:
 * 1. Basic join/leave operations
 * 2. Concurrent joins to the same room (thread-safety)
 * 3. Concurrent leaves from the same room
 * 4. Room cleanup when empty
 * 5. User in multiple rooms simultaneously
 * 
 * Network Programming Concepts Tested:
 * - Thread-safety with concurrent access
 * - Atomic operations on shared data structures
 * - Race condition prevention
 * 
 * @author Team Member 1 - WebSocket Server & Connection Handling
 */
class ChatRoomManagerTest {

    private ChatRoomManager chatRoomManager;

    @BeforeEach
    void setUp() {
        chatRoomManager = new ChatRoomManager();
    }

    @Test
    @DisplayName("User can join a room")
    void testJoinRoom() {
        chatRoomManager.joinRoom("general", "Alice");
        
        Set<String> members = chatRoomManager.getMembers("general");
        
        assertEquals(1, members.size());
        assertTrue(members.contains("Alice"));
    }

    @Test
    @DisplayName("Multiple users can join the same room")
    void testMultipleUsersJoinRoom() {
        chatRoomManager.joinRoom("general", "Alice");
        chatRoomManager.joinRoom("general", "Bob");
        chatRoomManager.joinRoom("general", "Charlie");
        
        Set<String> members = chatRoomManager.getMembers("general");
        
        assertEquals(3, members.size());
        assertTrue(members.contains("Alice"));
        assertTrue(members.contains("Bob"));
        assertTrue(members.contains("Charlie"));
    }

    @Test
    @DisplayName("User can leave a room")
    void testLeaveRoom() {
        chatRoomManager.joinRoom("general", "Alice");
        chatRoomManager.joinRoom("general", "Bob");
        
        chatRoomManager.leaveRoom("general", "Alice");
        
        Set<String> members = chatRoomManager.getMembers("general");
        
        assertEquals(1, members.size());
        assertFalse(members.contains("Alice"));
        assertTrue(members.contains("Bob"));
    }

    @Test
    @DisplayName("Room is removed when last member leaves")
    void testRoomCleanupWhenEmpty() {
        chatRoomManager.joinRoom("general", "Alice");
        chatRoomManager.leaveRoom("general", "Alice");
        
        Set<String> allRooms = chatRoomManager.getAllRooms();
        
        assertFalse(allRooms.contains("general"));
    }

    @Test
    @DisplayName("User can be in multiple rooms simultaneously")
    void testUserInMultipleRooms() {
        chatRoomManager.joinRoom("general", "Alice");
        chatRoomManager.joinRoom("team", "Alice");
        chatRoomManager.joinRoom("project", "Alice");
        
        assertTrue(chatRoomManager.isUserInRoom("general", "Alice"));
        assertTrue(chatRoomManager.isUserInRoom("team", "Alice"));
        assertTrue(chatRoomManager.isUserInRoom("project", "Alice"));
    }

    @Test
    @DisplayName("leaveAllRooms removes user from all rooms")
    void testLeaveAllRooms() {
        chatRoomManager.joinRoom("general", "Alice");
        chatRoomManager.joinRoom("team", "Alice");
        chatRoomManager.joinRoom("project", "Alice");
        
        chatRoomManager.leaveAllRooms("Alice");
        
        assertFalse(chatRoomManager.isUserInRoom("general", "Alice"));
        assertFalse(chatRoomManager.isUserInRoom("team", "Alice"));
        assertFalse(chatRoomManager.isUserInRoom("project", "Alice"));
    }

    @Test
    @DisplayName("Concurrent joins to the same room are thread-safe")
    void testConcurrentJoins() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 100 threads simultaneously join the same room
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    chatRoomManager.joinRoom("general", "User" + userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();
        
        // Verify all users joined successfully
        Set<String> members = chatRoomManager.getMembers("general");
        assertEquals(threadCount, members.size());
    }

    @Test
    @DisplayName("Concurrent leaves from the same room are thread-safe")
    void testConcurrentLeaves() throws InterruptedException {
        int userCount = 100;
        
        // First, add 100 users to the room
        for (int i = 0; i < userCount; i++) {
            chatRoomManager.joinRoom("general", "User" + i);
        }
        
        assertEquals(userCount, chatRoomManager.getRoomSize("general"));
        
        // Now, 100 threads simultaneously leave the room
        int threadCount = userCount;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    chatRoomManager.leaveRoom("general", "User" + userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();
        
        // Verify room is empty and removed
        assertFalse(chatRoomManager.getAllRooms().contains("general"));
    }

    @Test
    @DisplayName("Concurrent joins to different rooms are thread-safe")
    void testConcurrentJoinsToMultipleRooms() throws InterruptedException {
        int roomCount = 50;
        int usersPerRoom = 10;
        int totalThreads = roomCount * usersPerRoom;
        
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(totalThreads);
        
        // Create multiple rooms with multiple users concurrently
        for (int roomId = 0; roomId < roomCount; roomId++) {
            for (int userId = 0; userId < usersPerRoom; userId++) {
                final int finalRoomId = roomId;
                final int finalUserId = userId;
                
                executorService.submit(() -> {
                    try {
                        chatRoomManager.joinRoom("room" + finalRoomId, "User" + finalUserId);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        
        // Wait for all threads to complete
        latch.await(15, TimeUnit.SECONDS);
        executorService.shutdown();
        
        // Verify all rooms were created with correct member counts
        assertEquals(roomCount, chatRoomManager.getTotalRooms());
        
        for (int roomId = 0; roomId < roomCount; roomId++) {
            assertEquals(usersPerRoom, chatRoomManager.getRoomSize("room" + roomId));
        }
    }
}
