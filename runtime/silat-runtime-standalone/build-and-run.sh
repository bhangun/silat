#!/bin/bash

# Build and run script for Silat Standalone Runtime

set -e

echo "Building Silat Standalone Runtime..."

# Build the project
./mvnw clean package -DskipTests

echo "Building Docker image..."
docker build -t silat-standalone .

echo "Starting Silat Standalone Runtime..."
docker run -p 8080:8080 -p 9090:9090 --name silat-standalone-container silat-standalone

echo "Silat Standalone Runtime is now running!"
echo "Access the API at: http://localhost:8080"
echo "Access gRPC at: localhost:9090"