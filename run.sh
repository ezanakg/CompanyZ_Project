#!/bin/bash

# CompanyZ Project - Build and Run Script

PROJECT_DIR="/Users/jack/Documents/Fall 2025/Software Development/CompanyZ_Project"

echo "=================================="
echo "CompanyZ Employee Management System"
echo "=================================="
echo ""

# Check if we're in the right directory
cd "$PROJECT_DIR" || exit 1

echo "📦 Compiling project..."
echo ""

# Compile all Java files
find src -name "*.java" -type f | xargs javac -d bin 2>&1

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "🚀 Running application..."
    echo ""
    echo "Demo Login Credentials:"
    echo "  Admin:    username='admin',    password='admin123'"
    echo "  Employee: username='employee', password='emp123'"
    echo ""
    
    # Run the application
    java -cp bin src.AppLauncher
else
    echo "❌ Compilation failed!"
    exit 1
fi
