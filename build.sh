#!/bin/bash

echo "=================================="
echo "Building Real-Time Chat Application"
echo "=================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null
then
    echo "❌ Maven is not installed!"
    echo "Please install Maven from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check Java version
echo "Checking Java version..."
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F'.' '{print $1}')

if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required!"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    echo "Download from: https://adoptium.net/temurin/releases/?version=17"
    exit 1
fi

echo "✅ Java version OK"

# Clean previous builds
echo "Cleaning previous builds..."
mvn clean

# Compile and package
echo "Compiling and packaging..."
mvn package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "=================================="
    echo "✅ Build successful!"
    echo "=================================="
    echo ""
    echo "To run the server:"
    echo "  java -cp target/RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer"
    echo ""
    echo "To run the client:"
    echo "  java -cp target/RealTimeSecureChatApp-1.0.0-client.jar com.securechat.client.ChatClient"
    echo ""
else
    echo ""
    echo "=================================="
    echo "❌ Build failed!"
    echo "=================================="
    exit 1
fi
