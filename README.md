# Royal Caribs HTTP Forward Proxy

A lightweight HTTP forward proxy system built with Spring Boot and Docker.

## System Architecture

The system consists of two main components:

1. **Ship Proxy (Client)** - Receives proxy requests on port 8080
2. **Offshore Proxy (Server)** - Processes outbound HTTP requests

## Project Structure

royal-caribs-proxy/
├── docker-compose.yml
├── offshore-proxy/
│ ├── src/
│ ├── Dockerfile
│ ├── pom.xml
│ └── target/offshore-proxy-[version].jar
└── ship-proxy/
├── src/
├── Dockerfile
├── pom.xml
└── target/ship-proxy-[version].jar


## Installation and Setup

### Prerequisites

- Java 17 or later
- Docker and Docker Compose
- Maven

### Build Instructions

1. Build both applications:

```bash
# Build ship-proxy
cd ship-proxy
./mvnw clean package

# Build offshore-proxy
cd ../offshore-proxy
./mvnw clean package
cd ..
```

### Deployment
- Start the system using Docker Compose:

```bash
docker-compose up --build
```

## Usage

```bash
# Linux/macOS
curl -x http://localhost:8080 http://httpforever.com/

# Windows
curl.exe -x http://localhost:8080 http://httpforever.com/
```

## Configuration

### Ship Proxy
ship-proxy/src/main/resources/application.properties:

server.port=8080

offshore.proxy.url=http://offshore-proxy:9090

### Offshore Proxy

server.port=9090