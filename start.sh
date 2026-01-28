#!/bin/bash

# SWIFT Banking Messages - Startup Script
# This script helps build and run the Vaadin web application

echo "🏦 SWIFT Banking Messages - Professional Web Interface"
echo "======================================================"

# Use Maven wrapper for consistent builds
if [ ! -f "./mvnw" ]; then
    echo "❌ Maven wrapper (mvnw) not found"
    echo "Please ensure the script is run from the project root directory"
    exit 1
fi

# Check Java version
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    echo "Please install Java 17+ and add it to your PATH"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version $JAVA_VERSION is too old"
    echo "Please install Java 17 or higher"
    exit 1
fi

echo "✅ Environment check passed"
echo "   Java: $(java -version 2>&1 | head -n 1)"
echo "   Maven: $(mvn -version | head -n 1)"
echo ""

# Build options
case "${1:-build}" in
    "build")
        echo "🔨 Building application..."
        ./mvnw clean package
        ;;
    "run")
        echo "🚀 Starting the web application..."
        echo "🌐 Application will be available at: http://localhost:8080"
        echo "⏹️  Press Ctrl+C to stop the server"
        echo ""
        mvn jetty:run
        ;;
    "test")
        echo "🧪 Running tests..."
        mvn test
        ;;
    "clean")
        echo "🧹 Cleaning build artifacts..."
        mvn clean
        ;;
    "dev")
        echo "🛠️  Starting development server with auto-reload..."
        echo "🌐 Application will be available at: http://localhost:8080"
        echo "⏹️  Press Ctrl+C to stop the server"
        echo ""
        mvn jetty:run -Djetty.scanIntervalSeconds=2
        ;;
    "help"|"-h"|"--help")
        echo "Usage: $0 [command]"
        echo ""
        echo "Commands:"
        echo "  build   - Build the application (default)"
        echo "  run     - Build and run the web application"
        echo "  test    - Run all tests"
        echo "  clean   - Clean build artifacts"
        echo "  dev     - Start development server with auto-reload"
        echo "  help    - Show this help message"
        echo ""
        echo "Examples:"
        echo "  $0 build    # Build the application"
        echo "  $0 run      # Build and run the server"
        echo "  $0 dev      # Start development server"
        ;;
    *)
        echo "❌ Unknown command: $1"
        echo "Run '$0 help' for usage information"
        exit 1
        ;;
esac