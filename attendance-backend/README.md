# HR Attendance & Leave Management System - Backend

## Project Overview
A scalable enterprise-grade Spring Boot application for managing workforce attendance tracking and leave lifecycle management.

## Technology Stack
- **Framework**: Spring Boot 3.2.x
- **Language**: Java 17
- **Database**: MySQL
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven

## Project Structure

```
com.edutech.attendance/
├── config/              # Security & application configuration
├── controller/          # REST API endpoints
├── dto/                 # Data Transfer Objects
├── entity/              # Database entities
├── jwt/                 # JWT token handling
├── repository/          # Database access layer
├── service/             # Business logic layer
└── exception/           # Exception handling
```

## Core Entities

### User
- Manages user authentication and roles
- Roles: ADMIN, MANAGER, EMPLOYEE
- Fields: id, username, password (encrypted), email, fullName, role, isActive

### Attendance
- Tracks employee check-in/check-out
- Fields: id, userId, date, checkInTime, checkOutTime, status (PRESENT/ABSENT/LATE)

### LeaveRequest
- Manages leave applications and approvals
- Fields: id, userId, startDate, endDate, leaveType, status (PENDING/APPROVED/REJECTED), reason, approvedBy

### Policy
- Leave policy configuration
- Fields: id, leaveType, maxDays, rules, isActive

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Admin APIs
- `POST /api/admin/users` - Create user
- `GET /api/admin/reports` - View reports

### Manager APIs
- `GET /api/manager/leaves` - View pending leaves
- `PUT /api/manager/leave/{id}` - Approve/reject leave

### Employee APIs
- `POST /api/employee/check-in` - Mark attendance
- `POST /api/employee/leave` - Apply for leave

## Security Features
- JWT token-based authentication
- Role-Based Access Control (RBAC)
- Stateless authentication
- CORS configuration
- Password encryption using BCrypt

## Setup & Installation

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Database Setup
```sql
CREATE DATABASE attendance_db;
```

### Update Database Configuration
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/attendance_db
    username: root
    password: your_password
```

### Build & Run
```bash
# Build
mvn clean build

# Run
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Dependencies

### Core
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Security
- MySQL Connector

### Security
- JJWT (JWT)
- Spring Security

### Validation
- Spring Boot Starter Validation

### Development
- Lombok
- Spring Boot DevTools

## Notes
- Entities will be auto-created in MySQL on first run (ddl-auto: update)
- JWT token expiration: 24 hours (configurable)
- CORS is configured for localhost:4200 (Angular frontend)
