package main.java.com.securechat.common;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a user in the chat system
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private LocalDateTime connectedAt;
    private boolean online;

    public User() {
        this.connectedAt = LocalDateTime.now();
        this.online = true;
    }

    public User(String userId, String username) {
        this();
        this.userId = userId;
        this.username = username;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(LocalDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", online=" + online +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId != null && userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
