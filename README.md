# Medical Appointment System

Medical Appointments System is a full-stack web application for managing medical appointments. It provides functionality for patients, doctors, and administrators through a secure REST API and a modern web interface.

The project focuses on clean architecture, maintainability, and scalability.

## Tech Stack

### Server

- Java 21
- Spring Boot 4.1.0
- Spring Security
- Spring Data JPA (Hibernate)
- PostgreSQL
- JWT Authentication
- Maven
- SpringDoc OpenAPI

### Client

- React
- TypeScript
- Vite
- PrimeReact

### Infrastructure

- Docker
- Docker Compose

## Project Structure

```text
.
├── server
├── client
└── docker-compose.yml
```

### Server Architecture

The server follows a hybrid architecture:

- Layered architecture for controllers, configuration, security, and exception handling.
- Domain-oriented organization for business logic.

Example:

```text
controller/
└── UserController.java

user/
├── User.java
├── UserService.java
├── UserRepository.java
├── mapper/
│   └── UserMapper.java
└── dto/
```

## Running the Project

### Prerequisites

Choose one of the following options:

#### Option 1 — Docker (recommended)

- Docker
- Docker Compose

#### Option 2 — Local Development

- Java 21
- Maven
- Node.js

### Clone the repository

```bash
git clone https://github.com/BOGI5/medical-appointment-system.git
cd medical-appointment-system
```

### Configure Environment Variables

Create `.env` files in both the `server` and `client` directories by copying the provided `.env.example` files.

### Start with Docker

```bash
docker-compose up --build
```

### Run Locally

Start PostgreSQL (or use Docker only for the database), then run:

```bash
cd server/appointment-system
./mvnw spring-boot:run
```

```bash
cd client
npm install
npm run dev
```

## API Documentation

After the server starts, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```