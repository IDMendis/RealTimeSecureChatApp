# Team Member Assignments & Implementation Guide

## Project: Real-Time Secure Chat Application
**Java Version: 17 LTS**

---

## 📋 Module Assignments

### **Module 1: Server Setup** 👤 Team Member 1
**Files to implement:**
- `src/server/ChatServer.java`
- `src/server/ClientHandler.java`

**Responsibilities:**
1. Implement `ServerSocket` to listen on port 8888
2. Accept incoming client connections in a loop
3. Create a new `ClientHandler` thread for each client
4. Manage thread pool using `ExecutorService`
5. Handle client disconnections gracefully
6. Implement broadcast messaging to all clients
7. Maintain a thread-safe list of connected clients using `ConcurrentHashMap`

**Key Concepts:**
- TCP Sockets
- Multithreading
- Thread Pools (ExecutorService)
- Concurrent collections

**Testing:**
- Start server
- Connect multiple clients
- Verify each client gets its own thread
- Check server logs for connection/disconnection events

---

### **Module 2: Client Application** 👤 Team Member 2
**Files to implement:**
- `src/client/ChatClient.java`

**Responsibilities:**
1. Connect to server using `Socket`
2. Create `ObjectInputStream` and `ObjectOutputStream`
3. Implement user input handling (console-based)
4. Create a separate thread to listen for incoming messages
5. Display messages in console with timestamps
6. Implement commands: `/pm`, `/quit`
7. Handle disconnection and reconnection

**Key Concepts:**
- Client-side Socket programming
- I/O Streams (ObjectInputStream/ObjectOutputStream)
- User input handling
- Message display formatting

**Testing:**
- Connect to running server
- Send group messages
- Receive messages from other clients
- Test private message command
- Test graceful disconnection

---

### **Module 3: Encryption Module** 👤 Team Member 3
**Files to implement:**
- `src/utils/EncryptionUtil.java`

**Responsibilities:**
1. Implement AES-256 encryption using `javax.crypto`
2. Create methods for key generation
3. Implement `encrypt(String message, SecretKey key)` method
4. Implement `decrypt(String encryptedMessage, SecretKey key)` method
5. Handle Base64 encoding for encrypted data
6. Implement key serialization/deserialization
7. Add error handling for encryption failures

**Key Concepts:**
- AES Encryption
- javax.crypto package
- SecretKey and KeyGenerator
- Cipher modes
- Base64 encoding

**Testing:**
- Generate encryption keys
- Encrypt sample messages
- Decrypt and verify original message
- Test with various message lengths
- Benchmark encryption speed

**Integration:**
- Modify `Message` class to mark encrypted messages
- Integrate with `ChatClient` to encrypt before sending
- Integrate with `ClientHandler` to decrypt on receiving

---

### **Module 4: Private Chat Feature** 👤 Team Member 4
**Files to implement:**
- `src/messaging/PrivateMessageHandler.java`
- Integration with `ChatServer.java`
- Integration with `ClientHandler.java`

**Responsibilities:**
1. Implement `PrivateMessageHandler` interface
2. Add private message routing logic in `ChatServer`
3. Maintain online user list with unique IDs
4. Implement `/pm <userId> <message>` command parsing
5. Route private messages to correct recipient
6. Handle recipient offline scenarios
7. Add user status (online/offline) tracking

**Key Concepts:**
- Message routing
- HashMap for user lookup
- Client-to-client communication via server
- Message types and filtering

**Testing:**
- Send private message between two clients
- Verify other clients don't receive private messages
- Test with offline recipient
- Test user list functionality

---

### **Module 5: Message Logging** 👤 Team Member 5
**Files to implement:**
- `src/messaging/MessageLogger.java`

**Responsibilities:**
1. Implement file-based message logging
2. Create log files with date-based naming
3. Log all messages (group and private) with timestamps
4. Implement message history retrieval
5. Add methods to export chat history
6. Implement log rotation (daily logs)
7. Add configuration for enabling/disabling logging

**Key Concepts:**
- File I/O (java.nio.file)
- File operations (create, append, read)
- Date formatting
- Path management

**Testing:**
- Verify logs are created in `chat_history/` folder
- Check log file format and content
- Test history retrieval
- Test log export functionality
- Verify daily log rotation

**Optional Enhancement:**
- Add SQLite database support
- Implement message search functionality
- Add log file compression

---

## 🔗 Integration Points

### Between Module 1 & 2:
- Server must accept client Socket connections
- Both use `ObjectInputStream` and `ObjectOutputStream`
- Both must serialize/deserialize `Message` objects

### Between Module 3 & 2:
- Client encrypts messages before sending
- Client decrypts received messages
- Key exchange mechanism (initial implementation: shared key)

### Between Module 4 & 1:
- Server routes private messages to correct recipient
- ClientHandler must check message type and route accordingly
- Server maintains user ID to ClientHandler mapping

### Between Module 5 & 1:
- Server calls `MessageLogger.logMessage()` for all messages
- Integration in `ClientHandler` when message is received
- Log both group and private messages

---

## 🛠️ Development Steps

### Phase 1: Basic Setup (Week 1)
1. Set up project structure
2. Implement basic Message, User classes
3. Module 1: Basic server accepting connections
4. Module 2: Basic client connecting to server

### Phase 2: Core Messaging (Week 2)
1. Module 1: Implement broadcasting
2. Module 2: Implement message sending/receiving
3. Test group chat functionality

### Phase 3: Encryption (Week 2-3)
1. Module 3: Implement encryption utilities
2. Integrate encryption with client
3. Test encrypted communication

### Phase 4: Advanced Features (Week 3-4)
1. Module 4: Implement private messaging
2. Module 5: Implement message logging
3. Integration testing

### Phase 5: Testing & Polish (Week 4)
1. Complete integration testing
2. Bug fixes
3. Documentation
4. Demo preparation

---

## 📝 Coding Standards

### General Guidelines:
- Use Java 17 features where appropriate
- Follow Java naming conventions
- Add JavaDoc comments for all public methods
- Use SLF4J for logging
- Handle exceptions properly (don't swallow exceptions)
- Use try-with-resources for streams

### Code Review Checklist:
- [ ] Code compiles without warnings
- [ ] All methods have JavaDoc
- [ ] Proper exception handling
- [ ] Thread-safe where necessary
- [ ] No hardcoded values (use constants)
- [ ] Logging for important events
- [ ] Unit tests written (optional but recommended)

---

## 🐛 Common Issues & Solutions

### Issue 1: "Connection Refused"
**Solution:** Make sure server is running before starting clients

### Issue 2: "ClassNotFoundException"
**Solution:** Ensure Message class is serializable and available to both server and client

### Issue 3: Thread Blocking
**Solution:** Use separate threads for reading and writing

### Issue 4: Port Already in Use
**Solution:** Change port number or close existing server

### Issue 5: Encryption Errors
**Solution:** Ensure both parties use same key and algorithm

---

## 📊 Success Criteria

✅ Server handles at least 10 concurrent clients
✅ Messages are sent and received in real-time (< 100ms latency)
✅ All messages are encrypted with AES-256
✅ Private messages reach only intended recipient
✅ All messages are logged to file
✅ System handles client disconnections gracefully
✅ No memory leaks or thread issues
✅ Clean code with proper documentation

---

**Good luck team! 🚀**
