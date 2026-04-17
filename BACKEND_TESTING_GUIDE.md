# Backend Testing Guide

## Installation Requirements

Before testing the backend, you need to install the following:

### 1. Java 17
**macOS:**
```bash
brew install openjdk@17
# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Verify Installation:**
```bash
java -version
javac -version
```

### 2. Maven
**macOS:**
```bash
brew install maven
```

**Verify Installation:**
```bash
mvn -version
```

### 3. MySQL 8.0+
**macOS:**
```bash
brew install mysql
# Start MySQL service
brew services start mysql
```

**Verify Installation:**
```bash
mysql --version
mysql -u root
```

### 4. Postman (Optional but Recommended)
Download from: https://www.postman.com/downloads/

---

## Database Setup

### Create Database
```bash
mysql -u root -p
```

```sql
CREATE DATABASE attendance_db;
USE attendance_db;

-- Verify tables are created (they will be auto-created by Hibernate)
SHOW TABLES;
```

### Verify Connection
```bash
mysql -u root -p attendance_db
```

---

## Build & Run the Backend

### 1. Navigate to Project
```bash
cd /Users/bharathank/pjs/l&t_capstone/attendance-backend
```

### 2. Build the Project
```bash
mvn clean install
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

**Expected Output:**
```
[INFO] Attaching debugger to run at localhost:5005
[main] c.e.a.AttendanceApplication : Started AttendanceApplication in X.XXX seconds
[main] o.s.b.a.e.web.TomcatWebServer : Tomcat started on port(s): 8080 (http)
```

The application will be running at: **http://localhost:8080**

---

## API Testing

### Using Postman

#### 1. Create Postman Collection
1. Open Postman
2. Create new Collection: "HR Attendance API"
3. Add requests according to API documentation

#### 2. Register a User
**Request:**
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john.doe",
  "email": "john@example.com",
  "password": "Password123",
  "fullName": "John Doe",
  "role": "EMPLOYEE"
}
```

**Expected Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "john@example.com",
  "username": "john.doe",
  "fullName": "John Doe",
  "role": "EMPLOYEE"
}
```

#### 3. Login
**Request:**
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "Password123"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "john@example.com",
  "username": "john.doe",
  "fullName": "John Doe",
  "role": "EMPLOYEE"
}
```

**Save the token for next requests!**

#### 4. Check-In
**Request:**
```
POST http://localhost:8080/api/employee/check-in
Authorization: Bearer <your_token_here>
Content-Type: application/json
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "date": "2026-04-17",
  "checkInTime": "09:30:45",
  "checkOutTime": null,
  "status": "PRESENT"
}
```

#### 5. Get Today's Attendance
**Request:**
```
GET http://localhost:8080/api/employee/attendance/today
Authorization: Bearer <your_token_here>
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "date": "2026-04-17",
  "checkInTime": "09:30:45",
  "checkOutTime": null,
  "status": "PRESENT"
}
```

#### 6. Apply Leave
**Request:**
```
POST http://localhost:8080/api/employee/leave
Authorization: Bearer <your_token_here>
Content-Type: application/json

{
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "CASUAL",
  "reason": "Personal work"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "CASUAL",
  "status": "PENDING",
  "reason": "Personal work",
  "approverName": null,
  "rejectionReason": null
}
```

---

## Using cURL (Command Line)

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com",
    "password": "Password123",
    "fullName": "John Doe",
    "role": "EMPLOYEE"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "Password123"
  }'
```

### Check-In (with token)
```bash
curl -X POST http://localhost:8080/api/employee/check-in \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json"
```

---

## Testing Scenarios

### Scenario 1: Employee Workflow
1. **Register** as EMPLOYEE
2. **Login** and save token
3. **Check-In** for the day
4. **Apply Leave** request
5. **Get Leaves** to view status
6. **Check-Out** at end of day

### Scenario 2: Manager Workflow
1. **Register** as MANAGER
2. **Login**
3. **Get Pending Leaves** to view applications
4. **Approve** or **Reject** leave request
5. **View Team Attendance**

### Scenario 3: Admin Workflow
1. **Register** as ADMIN
2. **Login**
3. **Create Users** (EMPLOYEE, MANAGER)
4. **Create Leave Policies**
5. **View Reports** (Attendance & Leaves)
6. **Manage Users** (Update/Delete)

---

## Common Issues & Solutions

### Issue 1: MySQL Connection Failed
**Error:** `java.sql.SQLException: Access denied for user 'root'@'localhost'`

**Solution:**
```bash
# Check MySQL is running
brew services list

# Verify database exists
mysql -u root -p
> SHOW DATABASES;
> USE attendance_db;
```

### Issue 2: Port 8080 Already in Use
**Error:** `Address already in use`

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Issue 3: JWT Token Invalid
**Error:** `Cannot set user authentication: ...`

**Solution:**
- Ensure token is passed correctly in header: `Authorization: Bearer <token>`
- Token has expiration (24 hours by default)
- Check application.yml for jwt.secret configuration

### Issue 4: CORS Error
**Error:** `Access to XMLHttpRequest blocked by CORS policy`

**Solution:**
- Already configured for `http://localhost:4200`
- Update `SecurityConfig.java` if frontend URL is different

---

## Logging & Debugging

### Check Logs
```bash
# Application logs will show in console during mvn spring-boot:run
# Look for [INFO] or [ERROR] messages
```

### Enable Debug Logging
Edit `src/main/resources/application.yml`:
```yaml
logging:
  level:
    root: DEBUG
    com.edutech.attendance: DEBUG
```

### View Database Queries
Edit `src/main/resources/application.yml`:
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## Performance Testing (Optional)

### Using Apache JMeter
1. Download JMeter from: https://jmeter.apache.org/
2. Create test plan for API endpoints
3. Run concurrent user tests

### Example: Load Test Check-In
- Concurrent Users: 10
- Ramp-up Time: 5 seconds
- Loop Count: 100
- Expected Response: < 500ms

---

## Deployment Checklist

Before moving to frontend, verify:
- ✅ All endpoints are working
- ✅ Authentication/JWT is functional
- ✅ Role-based access control working
- ✅ Database operations working
- ✅ Error handling returning proper status codes
- ✅ CORS configured correctly
- ✅ Application builds without errors
- ✅ No security vulnerabilities

---

## Next Steps: Frontend Development

Once backend is tested and working:
1. Install Angular 14
2. Setup Angular project
3. Configure HTTP interceptor for JWT
4. Create route guards for RBAC
5. Build UI components
6. Test API integration

---
