# System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    REAL-TIME SECURE CHAT APPLICATION             │
│                         (Java 17 LTS)                            │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                         CLIENT SIDE                               │
└──────────────────────────────────────────────────────────────────┘

┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  Client 1   │  │  Client 2   │  │  Client 3   │  ... (up to 100)
│  (Alice)    │  │   (Bob)     │  │  (Charlie)  │
└──────┬──────┘  └──────┬──────┘  └──────┬──────┘
       │                │                │
       │  ┌─────────────▼────────────┐   │
       └──┤   ChatClient.java        ├───┘
          │  (Module 2)              │
          │  - Socket connection     │
          │  - Message send/receive  │
          │  - User input handler    │
          │  - Console UI            │
          └─────────────┬────────────┘
                        │
                        │  Encryption/Decryption
                        ▼
          ┌──────────────────────────┐
          │  EncryptionUtil.java     │
          │  (Module 3)              │
          │  - AES-256 encryption    │
          │  - Key generation        │
          │  - Encrypt/Decrypt       │
          └──────────────────────────┘

═══════════════════════════════════════════════════════════════════
                        NETWORK LAYER
                      (TCP/IP Sockets)
═══════════════════════════════════════════════════════════════════

┌──────────────────────────────────────────────────────────────────┐
│                         SERVER SIDE                               │
└──────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│                     ChatServer.java                            │
│                     (Module 1)                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  ServerSocket (Port 8888)                                │ │
│  │  - Accept client connections                             │ │
│  │  - Thread pool (ExecutorService)                         │ │
│  │  - Manage connected clients (ConcurrentHashMap)          │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │ClientHandler│  │ClientHandler│  │ClientHandler│  ...       │
│  │  (Thread 1) │  │  (Thread 2) │  │  (Thread 3) │           │
│  │  - Client 1 │  │  - Client 2 │  │  - Client 3 │           │
│  │  - Read Msg │  │  - Read Msg │  │  - Read Msg │           │
│  │  - Route    │  │  - Route    │  │  - Route    │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
└──────────┬──────────────────┬──────────────────┬───────────────┘
           │                  │                  │
           ▼                  ▼                  ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Group Message    │  │ Private Message  │  │ Message Logging  │
│   Broadcast      │  │     Routing      │  │   Persistence    │
└──────────────────┘  └──────────────────┘  └──────────────────┘
        │                     │                       │
        ▼                     ▼                       ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  All Clients     │  │PrivateMessage    │  │ MessageLogger    │
│                  │  │   Handler.java   │  │    .java         │
│  - Client 1      │  │  (Module 4)      │  │  (Module 5)      │
│  - Client 2      │  │  - Route to      │  │  - Save to file  │
│  - Client 3      │  │    specific user │  │  - Daily logs    │
│  - ...           │  │  - User status   │  │  - History       │
└──────────────────┘  └──────────────────┘  └─────────┬────────┘
                                                       │
                                                       ▼
                                            ┌──────────────────┐
                                            │  chat_history/   │
                                            │  chat_2025-11-04 │
                                            │       .log       │
                                            └──────────────────┘

═══════════════════════════════════════════════════════════════════
                        DATA LAYER
═══════════════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────────────────┐
│                    COMMON ENTITIES                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │  Message     │  │  User        │  │  MessageType │        │
│  │  .java       │  │  .java       │  │  .java       │        │
│  │              │  │              │  │              │        │
│  │ - messageId  │  │ - userId     │  │ - CONNECT    │        │
│  │ - senderId   │  │ - username   │  │ - DISCONNECT │        │
│  │ - recipientId│  │ - online     │  │ - GROUP_MSG  │        │
│  │ - content    │  │ - timestamp  │  │ - PRIVATE_MSG│        │
│  │ - type       │  │              │  │ - SERVER_MSG │        │
│  │ - encrypted  │  │              │  │              │        │
│  └──────────────┘  └──────────────┘  └──────────────┘        │
└────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════

MESSAGE FLOW EXAMPLE:

1. GROUP MESSAGE:
   Client 1 → Encrypt → Server → Decrypt → Broadcast → All Clients
   
2. PRIVATE MESSAGE:
   Client 1 → Encrypt → Server → Route → Client 2 (only)
   
3. MESSAGE LOGGING:
   Server → MessageLogger → chat_history/chat_DATE.log

═══════════════════════════════════════════════════════════════════

THREADING MODEL:

Server:
├── Main Thread (Accept connections)
├── Thread Pool (ExecutorService)
│   ├── ClientHandler Thread 1
│   ├── ClientHandler Thread 2
│   ├── ClientHandler Thread 3
│   └── ... (up to 100)
└── Shutdown Hook (Cleanup)

Client:
├── Main Thread (User input)
└── Listener Thread (Receive messages)

═══════════════════════════════════════════════════════════════════
```

## Key Components Interaction

```
┌─────────────────────────────────────────────────────────────┐
│  Module 1: Server Setup (Team Member 1)                    │
│  ┌─────────────┐         ┌─────────────────┐              │
│  │ ChatServer  │────────▶│ ClientHandler   │              │
│  │             │ creates │ (per client)    │              │
│  │ - Port 8888 │         │ - Socket        │              │
│  │ - Thread    │         │ - I/O Streams   │              │
│  │   Pool      │         │ - Message Route │              │
│  └─────────────┘         └─────────────────┘              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Module 2: Client App (Team Member 2)                      │
│  ┌─────────────────────────────────────────────┐           │
│  │ ChatClient                                  │           │
│  │  ┌──────────────┐      ┌───────────────┐   │           │
│  │  │ Input Thread │      │Listener Thread│   │           │
│  │  │ (Send)       │      │ (Receive)     │   │           │
│  │  └──────────────┘      └───────────────┘   │           │
│  └─────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Module 3: Encryption (Team Member 3)                      │
│  ┌───────────────────────────────────────┐                 │
│  │ EncryptionUtil                        │                 │
│  │  - generateKey()                      │                 │
│  │  - encrypt(message, key)              │                 │
│  │  - decrypt(encrypted, key)            │                 │
│  │  - Base64 encoding                    │                 │
│  └───────────────────────────────────────┘                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Module 4: Private Chat (Team Member 4)                    │
│  ┌───────────────────────────────────────┐                 │
│  │ PrivateMessageHandler                 │                 │
│  │  - sendPrivateMessage()               │                 │
│  │  - isUserOnline()                     │                 │
│  │  - getOnlineUsers()                   │                 │
│  └───────────────────────────────────────┘                 │
│  Integration: ChatServer routes to specific ClientHandler  │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Module 5: Message Logging (Team Member 5)                 │
│  ┌───────────────────────────────────────┐                 │
│  │ MessageLogger                         │                 │
│  │  - logMessage(message)                │                 │
│  │  - getMessageHistory(date)            │                 │
│  │  - exportHistory()                    │                 │
│  │  - clearHistory()                     │                 │
│  └───────────────────────────────────────┘                 │
│  Output: chat_history/chat_2025-11-04.log                  │
└─────────────────────────────────────────────────────────────┘
```

## Network Programming Concepts Demonstrated

```
┌──────────────────────────────────────────────────────────────┐
│  CONCEPT                  │  WHERE IT'S USED                 │
├──────────────────────────────────────────────────────────────┤
│  TCP Sockets              │  Client ↔ Server connection      │
│  ServerSocket             │  ChatServer accepts connections  │
│  Multithreading           │  ExecutorService, ClientHandler  │
│  Thread Pools             │  Manage multiple clients         │
│  Concurrent Collections   │  ConcurrentHashMap for clients   │
│  Object Serialization     │  Message objects over network    │
│  I/O Streams              │  ObjectInput/OutputStream        │
│  AES Encryption           │  javax.crypto for security       │
│  File I/O                 │  java.nio.file for logging       │
│  Client-Server Pattern    │  Centralized message routing     │
│  Request-Response         │  Connection ACK, message routing │
│  Publish-Subscribe        │  Broadcast to all clients        │
└──────────────────────────────────────────────────────────────┘
```

---

**This architecture ensures:**
- ✅ Scalability (up to 100 concurrent clients)
- ✅ Thread safety (concurrent collections)
- ✅ Security (AES-256 encryption)
- ✅ Modularity (separate concerns)
- ✅ Reliability (graceful error handling)
- ✅ Persistence (message logging)
