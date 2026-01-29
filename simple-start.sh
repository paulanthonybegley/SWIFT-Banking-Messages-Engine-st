#!/bin/bash

# SWIFT Banking Messages - Simple Startup Script
# Resolves timing issues during Vaadin initialization

echo "🏦 SWIFT Banking Messages - Simple Mode"
echo "=================================="

# Start Java with Vaadin application
java -jar target/banking-swift-messages-0.0.0-SNAPSHOT.jar &

# Wait for server to be ready (give Vaadin time to initialize)
echo "⏳ Waiting for Vaadin server to initialize..."

# Wait up to 15 seconds
for i in {1..15}; do
    sleep 1
    if ! pgrep -f "java" > /dev/null; then
        echo "✅ Server is ready! Opening browser..."
        break
    fi
done

# Check if server is responding
if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo "🌐 Server is responding correctly!"
    echo "📖 Opening browser at: http://localhost:8080"
    
    # Try different browser
    if command -v open > /dev/null 2>&1; then
        open http://localhost:8080
    else
        echo "💻 To access manually, open: http://localhost:8080"
    fi
else
    echo "❌ Server not responding after 15 seconds"
    echo "🔧 Try running './start.sh run' manually for debugging"
    exit 1
fi

# Cleanup
pkill -f "java" || true