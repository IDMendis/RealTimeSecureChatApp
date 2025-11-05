# 💬 Real-Time Secure Chat Application

A Java-based real-time chat system that demonstrates key **network programming concepts** such as TCP sockets, multithreading, and secure communication with AES encryption.

---

## 📋 Project Overview

The **Real-Time Secure Chat Application** enables multiple clients to connect to a central server and exchange messages securely. It supports both **group chats** and **private chats**, along with features such as **message encryption**, **multi-threaded handling**, and **client-server communication** using Java Sockets.

### ☕ Java Version
**Java 17 LTS (Long Term Support)**
- Minimum requirement: JDK 17 or higher
- Recommended: Latest JDK 17 LTS build
- Download: https://adoptium.net/temurin/releases/?version=17

---

## 🧩 Features & Team Member Responsibilities

| Module | Feature | Assigned To | Concepts Used |
|--------|---------|-------------|---------------|
| **Module 1** | Server Setup | Team Member 1 | ServerSocket, Multithreading, TCP Sockets, Thread Pools |
| **Module 2** | Client Application | Team Member 2 | Socket Programming, I/O Streams, Client-Server Communication |
| **Module 3** | Encryption Module | Team Member 3 | AES Encryption, javax.crypto, Secure Communication |
| **Module 4** | Private Chat Feature | Team Member 4 | Message Routing, Client-to-Client via Server |
| **Module 5** | Message Logging | Team Member 5 | File I/O, Data Persistence, Logging |

---

## 📁 Project Structure

```
RealTimeSecureChatApp/
├── pom.xml                          # Maven build configuration
├── .gitignore                       # Git ignore rules
├── README.md                        # This file
├── config/
│   └── application.properties       # Server configuration
├── src/
│   ├── main/java/com/securechat/
│   │   ├── common/                  # Shared classes
│   │   │   ├── Message.java         # Message entity
│   │   │   ├── MessageType.java     # Message type enum
│   │   │   └── User.java            # User entity
│   │   ├── server/                  # MODULE 1: Server components
│   │   │   ├── ChatServer.java      # Main server class
│   │   │   └── ClientHandler.java   # Client connection handler
│   │   ├── client/                  # MODULE 2: Client components
│   │   │   └── ChatClient.java      # Client application
│   │   ├── encryption/              # MODULE 3: Encryption
│   │   │   └── EncryptionUtil.java  # AES encryption utilities
│   │   └── messaging/               # MODULE 4 & 5: Messaging
│   │       ├── PrivateMessageHandler.java  # Private chat interface
│   │       └── MessageLogger.java   # Message logging
│   └── test/java/                   # Unit tests (to be added)
└── chat_history/                    # Message logs (created at runtime)
```

---

## 🔧 Network Programming Concepts Used

- ✅ **TCP Sockets** - Reliable connection-oriented communication
- ✅ **ServerSocket** - Server-side socket for accepting connections
- ✅ **Multithreading** - Handle multiple clients simultaneously
- ✅ **Thread Pools** - Efficient thread management with ExecutorService
- ✅ **ObjectStreams** - Serialize/deserialize Java objects over network
- ✅ **AES Encryption** - Symmetric encryption using javax.crypto
- ✅ **Client-Server Architecture** - Centralized message routing
- ✅ **Concurrent Collections** - Thread-safe data structures (ConcurrentHashMap)
- ✅ **File I/O** - Persistent message logging

---

## 🚀 How to Build and Run

### Prerequisites
- **JDK 17 or higher** (Download from [Adoptium](https://adoptium.net/))
- **Maven 3.6+** (or use IDE's built-in Maven)
- **Git** (for version control)

### Build with Maven

1. **Clone the repository:**
   ```bash
   git clone https://github.com/IDMendis/RealTimeSecureChatApp.git
   cd RealTimeSecureChatApp
   ```

2. **Build the project:**
   ```bash
   mvn clean compile
   ```

3. **Package into JAR files:**
   ```bash
   mvn package
   ```

### Run the Server

**Option 1: Using Maven**
```bash
mvn exec:java -Dexec.mainClass="com.securechat.server.ChatServer"
```

**Option 2: Using Java directly (after mvn package)**
```bash
java -cp target/RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer
```

**Option 3: With custom port**
```bash
java -cp target/RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer 9999
```

### Run the Client

**Option 1: Using Maven**
```bash
mvn exec:java -Dexec.mainClass="com.securechat.client.ChatClient"
```

**Option 2: Using Java directly**
```bash
java -cp target/RealTimeSecureChatApp-1.0.0-client.jar com.securechat.client.ChatClient
```

### Run Multiple Clients
Open multiple terminal windows and run the client command in each one.

---

## 🎯 Development Workflow

### For Team Members

Each team member should work on their assigned module:

1. **Team Member 1 (Server Setup)**
   - Complete implementation in `ChatServer.java`
   - Complete `ClientHandler.java`
   - Implement thread management and connection handling
   - Test with multiple client connections

2. **Team Member 2 (Client Application)**
   - Complete `ChatClient.java`
   - Implement user interface (console-based)
   - Handle user input and message display
   - Test connection to server

3. **Team Member 3 (Encryption)**
   - Complete `EncryptionUtil.java`
   - Implement AES encryption/decryption
   - Integrate encryption with message sending
   - Test encryption strength

4. **Team Member 4 (Private Chat)**
   - Implement `PrivateMessageHandler.java`
   - Add private message routing logic to server
   - Test direct messaging between clients
   - Implement user online/offline status

5. **Team Member 5 (Message Logging)**
   - Complete `MessageLogger.java`
   - Implement file-based logging
   - Add message history retrieval
   - Optional: Add database support

### Git Workflow

```bash
# Create your feature branch
git checkout -b feature/module-X-yourname

# Make changes and commit
git add .
git commit -m "Implement Module X: [description]"

# Push to remote
git push origin feature/module-X-yourname

# Create pull request for review
```

---

## 🧪 Testing

### Manual Testing Steps

1. **Start the server:**
   ```bash
   java -cp target/RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer
   ```

2. **Start multiple clients** (in separate terminals):
   ```bash
   java -cp target/RealTimeSecureChatApp-1.0.0-client.jar com.securechat.client.ChatClient
   ```

3. **Test scenarios:**
   - Connect multiple clients
   - Send group messages
   - Send private messages: `/pm <userId> <message>`
   - Disconnect clients
   - Check message logs in `chat_history/` folder

### Unit Tests (To be implemented)
```bash
mvn test
```

---

## 📝 Client Commands

| Command | Description | Example |
|---------|-------------|---------|
| `<message>` | Send group message | `Hello everyone!` |
| `/pm <userId> <message>` | Send private message | `/pm user123 Hi there` |
| `/quit` | Exit the chat | `/quit` |

---

## 🔐 Security Features

- **AES-256 Encryption** - Industry-standard symmetric encryption
- **Secure Key Exchange** - (To be implemented in future)
- **Message Integrity** - Encrypted messages cannot be tampered with
- **SSL/TLS Support** - Optional secure socket layer (future enhancement)

---

## 📚 Learning Resources

### Network Programming Concepts
- [Oracle Java Networking Tutorial](https://docs.oracle.com/javase/tutorial/networking/)
- [Java Socket Programming](https://www.baeldung.com/a-guide-to-java-sockets)
- [Multithreading in Java](https://www.baeldung.com/java-thread-pool)

### Encryption
- [Java Cryptography Architecture (JCA)](https://docs.oracle.com/en/java/javase/17/security/java-cryptography-architecture-jca-reference-guide.html)
- [AES Encryption in Java](https://www.baeldung.com/java-aes-encryption-decryption)

### Maven
- [Maven in 5 Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

---

## 🚀 Future Enhancements

- [ ] SSL/TLS encrypted connections
- [ ] Database integration for message persistence
- [ ] User authentication and authorization
- [ ] JavaFX or Web-based UI
- [ ] File sharing capability
- [ ] Voice/Video chat support
- [ ] Message read receipts
- [ ] User profiles and avatars
- [ ] Chat rooms with different topics
- [ ] Message search functionality

---

## 👥 Team Members

| Member | Module | Responsibilities |
|--------|--------|------------------|
| Team Member 1 | Server Setup | ServerSocket, Multithreading, Client Management |
| Team Member 2 | Client App | Socket Connection, UI, Message Display |
| Team Member 3 | Encryption | AES Encryption, Key Management |
| Team Member 4 | Private Chat | Message Routing, User Status |
| Team Member 5 | Logging | Message Persistence, History Retrieval |

---

## 📄 License

This project is created for educational purposes as part of a Network Programming course.

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📞 Contact

For questions or issues, please open an issue on GitHub or contact the team lead.

---

**Happy Coding! 🎉**
   cd RealTimeSecureChatApp/src
