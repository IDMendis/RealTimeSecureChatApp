# Quick Reference Card

## 🚀 Java Version
**Java 17 LTS** - Download: https://adoptium.net/temurin/releases/?version=17

## 🛠️ Build Commands

### Build Project
```bash
mvn clean package
```

### Run Server
```bash
mvn exec:java -Dexec.mainClass="com.securechat.server.ChatServer"
```

### Run Client
```bash
mvn exec:java -Dexec.mainClass="com.securechat.client.ChatClient"
```

## 📁 Important Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven configuration |
| `README.md` | Project overview |
| `TEAM_GUIDE.md` | Module assignments |
| `BUILD_GUIDE.md` | Build instructions |
| `PROJECT_SUMMARY.md` | Initialization summary |

## 👥 Module Assignments

| Module | Team Member | Main File |
|--------|-------------|-----------|
| 1 | Server Setup | `src/server/ChatServer.java` |
| 2 | Client App | `src/client/ChatClient.java` |
| 3 | Encryption | `src/utils/EncryptionUtil.java` |
| 4 | Private Chat | `src/messaging/PrivateMessageHandler.java` |
| 5 | Message Logging | `src/messaging/MessageLogger.java` |

## 💬 Client Commands

| Command | Description |
|---------|-------------|
| `Hello!` | Send group message |
| `/pm userId message` | Send private message |
| `/quit` | Exit chat |

## 🔧 Configuration

Edit `config/application.properties`:
- `server.port=8888` - Server port
- `server.max.clients=100` - Max connections
- `encryption.enabled=true` - Enable encryption
- `logging.enabled=true` - Enable logging

## 📚 Network Concepts

- **TCP Sockets** - Client-Server communication
- **Multithreading** - Handle multiple clients
- **AES Encryption** - Secure messages
- **Message Routing** - Private & group chat
- **File I/O** - Message logging

## 🐛 Common Issues

### Port Already in Use
```bash
# Use different port
java -cp target/RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer 9999
```

### Maven Not Found
Use IDE's built-in Maven or install from https://maven.apache.org/

### Wrong Java Version
```bash
java -version  # Should show 17.x.x
```

## 📖 Documentation Flow

1. **README.md** - Start here for overview
2. **SETUP_CHECKLIST.md** - Setup your environment
3. **TEAM_GUIDE.md** - Read your module section
4. **BUILD_GUIDE.md** - Build and run instructions
5. **PROJECT_SUMMARY.md** - Initialization details

## 🎯 Success Criteria

- [ ] Server handles 10+ concurrent clients
- [ ] Messages encrypted with AES-256
- [ ] Private messages work correctly
- [ ] All messages logged to file
- [ ] Clean code with documentation
- [ ] No thread issues or memory leaks

## 🚀 Development Workflow

1. Create feature branch: `git checkout -b feature/module-X-name`
2. Implement your module
3. Test frequently
4. Commit regularly: `git commit -m "Implement XYZ"`
5. Push: `git push origin feature/module-X-name`
6. Create Pull Request

## ⚡ Quick Test

```bash
# Terminal 1: Start Server
mvn exec:java -Dexec.mainClass="com.securechat.server.ChatServer"

# Terminal 2: Start Client 1
mvn exec:java -Dexec.mainClass="com.securechat.client.ChatClient"

# Terminal 3: Start Client 2
mvn exec:java -Dexec.mainClass="com.securechat.client.ChatClient"
```

---

**Keep this file handy for quick reference! 📌**
