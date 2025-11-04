# Project Setup Checklist

## Initial Setup (All Team Members)

### 1. Install Required Software
- [ ] Java JDK 17 or higher installed
  - Verify: `java -version` should show version 17 or higher
- [ ] Maven 3.6+ installed (optional, can use IDE)
  - Verify: `mvn -version`
- [ ] Git installed
  - Verify: `git --version`
- [ ] IDE installed (IntelliJ IDEA / Eclipse / VS Code)

### 2. Clone Repository
```bash
git clone https://github.com/IDMendis/RealTimeSecureChatApp.git
cd RealTimeSecureChatApp
```

### 3. Build Project
**Windows:**
```bash
build.bat
```

**Linux/Mac:**
```bash
chmod +x build.sh
./build.sh
```

**Or using Maven:**
```bash
mvn clean package
```

### 4. Verify Build
- [ ] Build completes without errors
- [ ] JAR files created in `target/` directory
- [ ] No compilation warnings

---

## Development Setup

### 5. IDE Configuration

#### IntelliJ IDEA:
1. Open → Select project folder
2. File → Project Structure
3. Set Project SDK to Java 17
4. Set language level to 17
5. File → Settings → Build → Build Tools → Maven
6. Enable "Import Maven projects automatically"

#### VS Code:
1. Install Extension Pack for Java
2. Open project folder
3. Trust workspace
4. Java: Configure Java Runtime → Select JDK 17

#### Eclipse:
1. File → Import → Existing Maven Project
2. Select project folder
3. Window → Preferences → Java → Installed JREs
4. Add JDK 17 if not present

### 6. Create Your Branch
```bash
# Replace X with your module number (1-5)
git checkout -b feature/module-X-yourname
```

### 7. Verify Your Module Files

#### Team Member 1 (Server):
- [ ] `src/server/ChatServer.java` exists
- [ ] `src/server/ClientHandler.java` exists
- [ ] Can compile without errors

#### Team Member 2 (Client):
- [ ] `src/client/ChatClient.java` exists
- [ ] Can compile without errors

#### Team Member 3 (Encryption):
- [ ] `src/utils/EncryptionUtil.java` exists
- [ ] Can compile without errors

#### Team Member 4 (Private Chat):
- [ ] `src/messaging/PrivateMessageHandler.java` exists
- [ ] Can see integration points in server code

#### Team Member 5 (Logging):
- [ ] `src/messaging/MessageLogger.java` exists
- [ ] Can compile without errors

---

## Testing Setup

### 8. Test Server Startup
```bash
# Start server
mvn exec:java -Dexec.mainClass="com.securechat.server.ChatServer"
```
**Expected:** Server starts on port 8888

### 9. Test Client Connection
```bash
# In a new terminal
mvn exec:java -Dexec.mainClass="com.securechat.client.ChatClient"
```
**Expected:** Client connects to server

### 10. Test Basic Communication
- [ ] Type message in client
- [ ] Message appears in server logs
- [ ] Open second client
- [ ] Messages between clients work

---

## Module-Specific Setup

### Module 1 (Server Setup):
- [ ] Read `TEAM_GUIDE.md` Module 1 section
- [ ] Understand ServerSocket and multithreading
- [ ] Review `ClientHandler.java` structure
- [ ] Plan thread pool implementation

### Module 2 (Client App):
- [ ] Read `TEAM_GUIDE.md` Module 2 section
- [ ] Understand Socket programming
- [ ] Plan user input handling
- [ ] Design console UI

### Module 3 (Encryption):
- [ ] Read `TEAM_GUIDE.md` Module 3 section
- [ ] Study AES encryption
- [ ] Review javax.crypto documentation
- [ ] Plan key generation

### Module 4 (Private Chat):
- [ ] Read `TEAM_GUIDE.md` Module 4 section
- [ ] Understand message routing
- [ ] Plan user ID management
- [ ] Design private message format

### Module 5 (Logging):
- [ ] Read `TEAM_GUIDE.md` Module 5 section
- [ ] Study File I/O in Java
- [ ] Plan log file structure
- [ ] Design log format

---

## Team Collaboration Setup

### 11. Set Up Communication
- [ ] Join team chat/Discord/Slack
- [ ] Share GitHub usernames
- [ ] Schedule daily standups
- [ ] Create shared document for notes

### 12. Git Workflow
- [ ] Understand branching strategy
- [ ] Practice creating feature branches
- [ ] Practice creating pull requests
- [ ] Set up code review process

---

## Documentation Review

### 13. Read Documentation
- [ ] `README.md` - Project overview
- [ ] `TEAM_GUIDE.md` - Your module details
- [ ] `BUILD_GUIDE.md` - Build instructions
- [ ] `SETUP_CHECKLIST.md` - This file

### 14. Understand Architecture
- [ ] Client-Server architecture
- [ ] Message flow
- [ ] Class relationships
- [ ] Integration points

---

## Ready to Code!

Once all items are checked:
1. Start implementing your module
2. Commit regularly to your branch
3. Test frequently
4. Communicate with team
5. Ask for help when stuck
6. Have fun coding! 🚀

---

## Troubleshooting

### Issue: Maven not found
**Solution:** Install Maven or use IDE's built-in Maven

### Issue: Wrong Java version
**Solution:** 
1. Download JDK 17 from https://adoptium.net/
2. Set JAVA_HOME environment variable
3. Update PATH

### Issue: Cannot compile
**Solution:**
1. Run `mvn clean`
2. Delete `target/` folder
3. Run `mvn compile` again

### Issue: Git conflicts
**Solution:**
1. Pull latest changes: `git pull origin main`
2. Resolve conflicts in IDE
3. Commit resolved files

---

## Next Steps

After setup:
1. Read your module section in `TEAM_GUIDE.md`
2. Plan your implementation
3. Start coding!
4. Test your code
5. Create pull request
6. Help teammates

**Good luck! 💪**
