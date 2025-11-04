# Build and Run Instructions

## Prerequisites

### 1. Install Java JDK 17
- Download from: https://adoptium.net/temurin/releases/?version=17
- Verify installation:
  ```bash
  java -version
  ```
  Should show: `openjdk version "17.x.x"`

### 2. Install Maven (Optional, for building)
- Download from: https://maven.apache.org/download.cgi
- Or use IDE's built-in Maven

### 3. Verify Java Version
```bash
javac -version
```

---

## Building the Project

### Option 1: Using Maven
```bash
# Navigate to project root
cd RealTimeSecureChatApp

# Clean previous builds
mvn clean

# Compile source code
mvn compile

# Package into JAR files
mvn package

# This creates:
# - target/RealTimeSecureChatApp-1.0.0-server.jar
# - target/RealTimeSecureChatApp-1.0.0-client.jar
```

### Option 2: Using IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open project folder
2. Import as Maven project
3. Let IDE download dependencies
4. Right-click on `ChatServer.java` → Run
5. Right-click on `ChatClient.java` → Run

### Option 3: Manual Compilation (Without Maven)
```bash
# Create output directory
mkdir -p bin

# Compile all Java files
javac -d bin -sourcepath src src/server/ChatServer.java src/client/ChatClient.java src/utils/EncryptionUtil.java

# Run server
java -cp bin server.ChatServer

# Run client (in another terminal)
java -cp bin client.ChatClient
```

---

## Running the Application

### Step 1: Start the Server

**Using Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.securechat.server.ChatServer"
```

**Using compiled JAR:**
```bash
java -cp target/RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer
```

**With custom port:**
```bash
java -cp target/RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer 9999
```

**Expected Output:**
```
[INFO] 🚀 Chat Server started on port 8888
[INFO] Waiting for client connections...
```

### Step 2: Start Client(s)

**Open a NEW terminal window for each client**

**Using Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.securechat.client.ChatClient"
```

**Using compiled JAR:**
```bash
java -cp target/RealTimeSecureChatApp-1.0.0-client.jar com.securechat.client.ChatClient
```

**Expected Output:**
```
╔════════════════════════════════════════╗
║   Real-Time Secure Chat Application   ║
║           Client v1.0.0                ║
╚════════════════════════════════════════╝

Enter your username: Alice
✅ Connected to server at localhost:8888

📝 Commands:
  - Type message and press Enter to send to group
  - /pm <recipientId> <message> - Send private message
  - /quit - Exit chat
```

### Step 3: Start More Clients
Repeat Step 2 in additional terminal windows to simulate multiple users.

---

## Usage Examples

### Example 1: Group Chat
```
Alice: Hello everyone!
Bob: Hi Alice!
Charlie: Hey team!
```

### Example 2: Private Message
```
Alice: /pm bob_id123 This is a private message
[PRIVATE to bob_id123]: This is a private message
```

### Example 3: Exit Chat
```
Alice: /quit
Disconnected from server
```

---

## Troubleshooting

### Problem: "Command not found: mvn"
**Solution:** 
- Install Maven or use IDE
- Or compile manually using `javac`

### Problem: "Port 8888 already in use"
**Solution:**
1. Stop existing server process
2. Or use different port: `ChatServer 9999`

### Problem: "Connection refused"
**Solution:**
- Make sure server is running first
- Check if firewall is blocking port 8888

### Problem: "ClassNotFoundException"
**Solution:**
- Ensure all dependencies are in classpath
- Run `mvn clean package` again

### Problem: Compilation errors about packages
**Solution:**
- Make sure you're using correct package structure
- Verify `src/main/java/com/securechat/...` folder structure exists
- Or use the old structure: `src/server/`, `src/client/`, `src/utils/`

---

## Testing Checklist

- [ ] Server starts without errors
- [ ] Client can connect to server
- [ ] Multiple clients can connect simultaneously
- [ ] Group messages are received by all clients
- [ ] Private messages work correctly
- [ ] Server logs connections/disconnections
- [ ] Message logs are created in `chat_history/` folder
- [ ] Encryption/decryption works (when implemented)
- [ ] Clients can disconnect gracefully

---

## Development Mode

### Running with Auto-reload (using Maven)
```bash
# Terminal 1: Server
mvn compile exec:java -Dexec.mainClass="com.securechat.server.ChatServer"

# Terminal 2: Client
mvn compile exec:java -Dexec.mainClass="com.securechat.client.ChatClient"
```

### Cleaning Build Artifacts
```bash
mvn clean
rm -rf target/
rm -rf bin/
rm -rf chat_history/
```

---

## Production Deployment

### Create Fat JAR (All dependencies included)
```bash
mvn assembly:single
```

### Run Fat JAR
```bash
# Server
java -jar target/RealTimeSecureChatApp-1.0.0-jar-with-dependencies.jar

# Note: You'll need to modify pom.xml to specify main class
```

---

## Configuration

Edit `config/application.properties` to change:
- Server port
- Max clients
- Encryption settings
- Logging settings

```properties
server.port=8888
server.max.clients=100
encryption.enabled=true
logging.enabled=true
```

---

## Additional Commands

### Run Tests (when implemented)
```bash
mvn test
```

### Generate Project Documentation
```bash
mvn javadoc:javadoc
```
Documentation will be in `target/site/apidocs/`

### Check Dependencies
```bash
mvn dependency:tree
```

---

**For help, refer to README.md or TEAM_GUIDE.md**
