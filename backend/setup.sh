#!/bin/bash

# FDMS Backend Setup Script for Linux/Mac
# This script sets up the entire backend environment

echo "========================================"
echo "FDMS Backend Setup Script"
echo "========================================"
echo ""

# Check Java
echo "[1/5] Checking Java Installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "✓ Java found: $JAVA_VERSION"
else
    echo "✗ Java not found. Please install JDK 17 or higher."
    exit 1
fi

# Check Maven
echo ""
echo "[2/5] Checking Maven Installation..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo "✓ Maven found: $MVN_VERSION"
else
    echo "✗ Maven not found. Please install Maven 3.6 or higher."
    exit 1
fi

# Check MySQL
echo ""
echo "[3/5] Checking MySQL Installation..."
if command -v mysql &> /dev/null; then
    MYSQL_VERSION=$(mysql --version)
    echo "✓ MySQL found: $MYSQL_VERSION"
else
    echo "✗ MySQL not found. Please install MySQL 8.0 or higher."
    exit 1
fi

# Build application
echo ""
echo "[4/5] Building Application..."
cd backend
echo "Running: mvn clean install"
mvn clean install
if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "✗ Build failed"
    exit 1
fi

# Setup database
echo ""
echo "[5/5] Setting Up Database..."
echo "Running: mysql -u root -p < database/init.sql"
echo "Please enter your MySQL password when prompted:"
mysql -u root -p < database/init.sql
if [ $? -eq 0 ]; then
    echo "✓ Database setup complete"
else
    echo "✗ Database setup failed"
    exit 1
fi

echo ""
echo "========================================"
echo "Setup Complete!"
echo "========================================"
echo ""
echo "To start the backend, run:"
echo "  cd backend"
echo "  mvn spring-boot:run"
echo ""
echo "Backend will be available at: http://localhost:8080"
echo ""
