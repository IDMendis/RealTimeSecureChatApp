package messaging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 5: Message Logging Module - Store chats in a file/database
 * Handles logging and persistence of chat messages
 * Team Member 5 should implement this class
 * 
 * Java Version: 17 LTS (Long Term Support)
 * Requires JDK 17 or higher
 */
public class MessageLogger {
    
    private static final String LOG_DIRECTORY = "chat_history";
    private static final String LOG_FILE_EXTENSION = ".log";
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MESSAGE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final Path logDirectory;
    private boolean loggingEnabled;
    
    public MessageLogger() {
        this.logDirectory = Paths.get(LOG_DIRECTORY);
        this.loggingEnabled = true;
        initializeLogDirectory();
    }
    
    /**
     * Initialize the log directory
     */
    private void initializeLogDirectory() {
        try {
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
                System.out.println("Created log directory: " + logDirectory.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
            loggingEnabled = false;
        }
    }
    
    /**
     * Log a message to file
     * @param message Message to log
     */
    public void logMessage(String message) {
        if (!loggingEnabled) {
            return;
        }
        
        try {
            // TODO: Full implementation by Team Member 5
            String logFileName = getLogFileName();
            Path logFile = logDirectory.resolve(logFileName);
            
            String logEntry = formatLogEntry(message);
            
            // Append to file
            Files.writeString(logFile, logEntry + System.lineSeparator(), 
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            
            // Message logged successfully
            
        } catch (IOException e) {
            System.err.println("Failed to log message: " + e.getMessage());
        }
    }
    
    /**
     * Format a message for logging
     */
    private String formatLogEntry(String message) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("[%s] %s",
            now.format(MESSAGE_TIME_FORMAT),
            message
        );
    }
    
    /**
     * Get log file name based on current date
     */
    private String getLogFileName() {
        return "chat_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + LOG_FILE_EXTENSION;
    }
    
    /**
     * Retrieve message history for a specific date
     * @param date Date to retrieve history for
     * @return List of log entries
     */
    public List<String> getMessageHistory(LocalDateTime date) {
        List<String> history = new ArrayList<>();
        
        try {
            String fileName = "chat_" + date.format(FILE_DATE_FORMAT) + LOG_FILE_EXTENSION;
            Path logFile = logDirectory.resolve(fileName);
            
            if (Files.exists(logFile)) {
                history = Files.readAllLines(logFile);
            }
            
        } catch (IOException e) {
            System.err.println("Failed to read message history: " + e.getMessage());
        }
        
        return history;
    }
    
    /**
     * Retrieve message history for today
     */
    public List<String> getTodayMessageHistory() {
        return getMessageHistory(LocalDateTime.now());
    }
    
    /**
     * Clear message history for a specific date
     */
    public boolean clearHistory(LocalDateTime date) {
        try {
            String fileName = "chat_" + date.format(FILE_DATE_FORMAT) + LOG_FILE_EXTENSION;
            Path logFile = logDirectory.resolve(fileName);
            
            if (Files.exists(logFile)) {
                Files.delete(logFile);
                System.out.println("Cleared history for: " + date.format(FILE_DATE_FORMAT));
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to clear history: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Export message history to a specified file
     */
    public boolean exportHistory(LocalDateTime date, Path exportPath) {
        try {
            String fileName = "chat_" + date.format(FILE_DATE_FORMAT) + LOG_FILE_EXTENSION;
            Path logFile = logDirectory.resolve(fileName);
            
            if (Files.exists(logFile)) {
                Files.copy(logFile, exportPath);
                System.out.println("Exported history to: " + exportPath);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to export history: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Enable or disable logging
     */
    public void setLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
        System.out.println("Message logging " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Check if logging is enabled
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }
}
