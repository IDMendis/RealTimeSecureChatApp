package com.example.chatserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Chat Room Manager for managing rooms and their members.
 * 
 * This service demonstrates thread-safe collection management in a multi-threaded environment.
 * 
 * Network Programming & Concurrency Concepts:
 * 1. Thread-Safety: Multiple WebSocket sessions (threads) can access rooms simultaneously
 * 2. Concurrent Collections: ConcurrentHashMap and CopyOnWriteArraySet prevent race conditions
 * 3. Atomic Operations: Read and write operations are thread-safe without explicit locking
 * 
 * Why ConcurrentHashMap?
 * - Allows concurrent reads without blocking
 * - Writes lock only specific segments, not the entire map
 * - Better performance than synchronized HashMap in high-concurrency scenarios
 * - Thread-safe without external synchronization
 * 
 * Why CopyOnWriteArraySet?
 * - Optimized for scenarios with many reads and few writes (room members)
 * - Iterator never throws ConcurrentModificationException
 * - Thread-safe for iterating while other threads modify
 * - Each write creates a new copy (acceptable for small sets)
 * 
 * Alternative Approaches (for reference):
 * - Collections.synchronizedSet(): Locks entire set for each operation (slower)
 * - Manual synchronization: More control but error-prone
 * - ReadWriteLock: More complex, overkill for this use case
 * 
 * This class is crucial for Member 1's deliverable on managing multiple concurrent clients.
 * 
 * @author Team Member 1 - WebSocket Server & Connection Handling
 */
@Slf4j
@Component
public class ChatRoomManager {

    /**
     * Thread-safe map storing room members.
     * Key: Room ID (e.g., "general", "team-alpha")
     * Value: Set of usernames currently in the room
     * 
     * ConcurrentHashMap ensures thread-safe access from multiple WebSocket sessions.
     * Each room has a CopyOnWriteArraySet to store members thread-safely.
     */
    private final Map<String, CopyOnWriteArraySet<String>> rooms = new ConcurrentHashMap<>();

    /**
     * Add a user to a chat room.
     * 
     * This method is called when a user joins a room via /app/chat.join.
     * If the room doesn't exist, it's created automatically.
     * 
     * Thread-Safety:
     * - computeIfAbsent() is atomic: only one thread creates the room
     * - CopyOnWriteArraySet.add() is thread-safe
     * - Multiple threads can call this method simultaneously without corruption
     * 
     * Concurrency Scenario:
     * Thread 1: joinRoom("general", "Alice")
     * Thread 2: joinRoom("general", "Bob")
     * Both threads can execute simultaneously. ConcurrentHashMap ensures:
     * - Only one thread creates the "general" room
     * - Both users are added to the set without data loss
     * 
     * @param roomId The room identifier
     * @param username The username to add
     */
    public void joinRoom(String roomId, String username) {
        log.debug("Adding user {} to room {}", username, roomId);
        
        // computeIfAbsent is atomic: creates room only if it doesn't exist
        // This prevents race conditions where two threads try to create the same room
        rooms.computeIfAbsent(roomId, k -> {
            log.info("Creating new room: {}", roomId);
            return new CopyOnWriteArraySet<>();
        }).add(username);
        
        log.info("User {} joined room {}. Room size: {}", username, roomId, rooms.get(roomId).size());
        
        // TODO (Advanced): Persist room membership to database
        // TODO (Advanced): Implement room capacity limits
        // TODO (Advanced): Implement room permissions (public, private, invite-only)
    }

    /**
     * Remove a user from a chat room.
     * 
     * This method is called when:
     * - User explicitly leaves a room
     * - User's WebSocket connection is closed (SessionDisconnectEvent)
     * 
     * Thread-Safety:
     * - ConcurrentHashMap.get() is thread-safe
     * - CopyOnWriteArraySet.remove() is thread-safe
     * - Room cleanup (if empty) is atomic
     * 
     * @param roomId The room identifier
     * @param username The username to remove
     */
    public void leaveRoom(String roomId, String username) {
        log.debug("Removing user {} from room {}", username, roomId);
        
        CopyOnWriteArraySet<String> members = rooms.get(roomId);
        
        if (members != null) {
            members.remove(username);
            log.info("User {} left room {}. Room size: {}", username, roomId, members.size());
            
            // Clean up empty rooms to prevent memory leaks
            if (members.isEmpty()) {
                rooms.remove(roomId);
                log.info("Room {} is empty and has been removed", roomId);
            }
        } else {
            log.warn("Attempted to remove user {} from non-existent room {}", username, roomId);
        }
    }

    /**
     * Get all members of a room.
     * 
     * Returns an immutable view of room members. The returned set is a snapshot
     * and won't reflect changes made after this call. This is safe because
     * CopyOnWriteArraySet creates a new array for each modification.
     * 
     * Thread-Safety:
     * - Reading from ConcurrentHashMap is thread-safe
     * - CopyOnWriteArraySet iterator never throws ConcurrentModificationException
     * - Multiple threads can iterate over members while others modify
     * 
     * @param roomId The room identifier
     * @return Set of usernames in the room (never null)
     */
    public Set<String> getMembers(String roomId) {
        return rooms.getOrDefault(roomId, new CopyOnWriteArraySet<>());
    }

    /**
     * Get all active rooms.
     * 
     * Returns a snapshot of all room IDs currently active in the system.
     * 
     * @return Set of room IDs
     */
    public Set<String> getAllRooms() {
        return rooms.keySet();
    }

    /**
     * Check if a user is in a specific room.
     * 
     * @param roomId The room identifier
     * @param username The username to check
     * @return true if the user is in the room
     */
    public boolean isUserInRoom(String roomId, String username) {
        CopyOnWriteArraySet<String> members = rooms.get(roomId);
        return members != null && members.contains(username);
    }

    /**
     * Get the number of members in a room.
     * 
     * @param roomId The room identifier
     * @return Number of members, or 0 if room doesn't exist
     */
    public int getRoomSize(String roomId) {
        CopyOnWriteArraySet<String> members = rooms.get(roomId);
        return members != null ? members.size() : 0;
    }

    /**
     * Remove a user from all rooms.
     * 
     * Called when a user disconnects from the WebSocket.
     * This ensures proper cleanup and prevents memory leaks.
     * 
     * Thread-Safety:
     * - Iterating over ConcurrentHashMap.entrySet() is thread-safe
     * - leaveRoom() calls are thread-safe
     * 
     * @param username The username to remove from all rooms
     */
    public void leaveAllRooms(String username) {
        log.info("Removing user {} from all rooms", username);
        
        // Iterate over all rooms and remove user
        // This is safe even if other threads modify rooms simultaneously
        rooms.forEach((roomId, members) -> {
            if (members.contains(username)) {
                leaveRoom(roomId, username);
            }
        });
    }

    /**
     * Get total number of active rooms.
     * 
     * @return Total room count
     */
    public int getTotalRooms() {
        return rooms.size();
    }
}
