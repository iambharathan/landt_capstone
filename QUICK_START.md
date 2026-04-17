# Quick Start Guide - HR Attendance Backend

## 5-Minute Setup

### Step 1: Install Java 17 (if not installed)
```bash
brew install openjdk@17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Step 2: Install Maven (if not installed)
```bash
brew install maven
```

### Step 3: Install MySQL (if not installed)
```bash
brew install mysql
brew services start mysql
```

### Step 4: Create Database
```bash
mysql -u root -p
CREATE DATABASE attendance_db;
EXIT;
```

### Step 5: Build & Run
```bash
cd /Users/bharathank/pjs/l&t_capstone/attendance-backend
mvn clean install
mvn spring-boot:run
```

**Expected:** Application starts on `http://localhost:8080`

---

## Quick API Test (Using cURL)

### 1. Register Admin User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "Admin@123",
    "fullName": "Admin User",
    "role": "ADMIN"
  }'
```

### 2. Login & Copy Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "Admin@123"
  }'
```

**Save the `token` from response**

### 3. Create Leave Policy (with token)
```bash
curl -X POST http://localhost:8080/api/admin/policies \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveType": "CASUAL",
    "maxDays": 12,
    "rules": "Casual leave annual limit"
  }'
```

### 4. Register Employee
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "emp1",
    "email": "emp1@example.com",
    "password": "Emp@123",
    "fullName": "Employee One",
    "role": "EMPLOYEE"
  }'
```

### 5. Employee Check-In (use employee token)
```bash
curl -X POST http://localhost:8080/api/employee/check-in \
  -H "Authorization: Bearer EMPLOYEE_TOKEN_HERE" \
  -H "Content-Type: application/json"
```

---

## Using Postman

1. **Import Collection:**
   - Create new collection "HR Attendance"
   - Import endpoints from API_DOCUMENTATION.md

2. **Set Variables:**
   - `base_url`: http://localhost:8080/api
   - `token`: <paste your JWT token>

3. **Test Flow:**
   ```
   Register → Login (save token) → Create Policy → 
   Check-In → Apply Leave → Get Leaves
   ```

---

## File Structure

```
attendance-backend/
├── pom.xml                    # Maven config
├── README.md                  # Project info
├── src/main/java/...          # Source code
│   ├── entity/                # Database models
│   ├── dto/                   # Data transfer objects
│   ├── repository/            # Database access
│   ├── service/               # Business logic
│   ├── controller/            # REST endpoints
│   ├── jwt/                   # Authentication
│   ├── config/                # Security config
│   └── exception/             # Error handling
└── src/main/resources/
    └── application.yml        # Spring config
```

---

## Configuration

### Database Connection (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/attendance_db
    username: root
    password: root  # Change if needed
```

### JWT Settings
```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation123456789
  expiration: 86400000  # 24 hours
```

### Server Port
```yaml
server:
  port: 8080
```

---

## API Endpoints (Quick Reference)

### Authentication
```
POST /auth/register     - Register user
POST /auth/login        - Login user
```

### Employee
```
POST /employee/check-in        - Mark attendance
POST /employee/check-out       - End of day
GET /employee/attendance/today - Today's record
POST /employee/leave          - Apply for leave
GET /employee/leaves          - View leaves
```

### Manager
```
GET /manager/leaves                - Pending leaves
PUT /manager/leave/{id}/approve    - Approve leave
PUT /manager/leave/{id}/reject     - Reject leave
```

### Admin
```
POST /admin/users            - Create user
GET /admin/users             - List all users
POST /admin/policies         - Create policy
GET /admin/policies          - List policies
GET /admin/reports/attendance - Attendance report
GET /admin/reports/leaves     - Leave report
```

---

## Common Commands

### Stop Application
```bash
# Ctrl + C in terminal where mvn spring-boot:run is running
```

### Check Logs
```bash
# Logs appear in console during runtime
# Look for [INFO], [WARN], [ERROR] messages
```

### Rebuild
```bash
mvn clean build
```

### Test Connection
```bash
# Check if app is running
curl http://localhost:8080/api/auth/login

# Expected: 400 (Bad Request) or 401 (Unauthorized)
# NOT: Connection refused
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8080 in use | `lsof -i :8080` then `kill -9 <PID>` |
| MySQL connection failed | `brew services start mysql` |
| Build fails | `mvn clean install -DskipTests` |
| Invalid JWT token | Ensure token is copied exactly with spaces |
| CORS error | Check Angular runs on http://localhost:4200 |

---

## Testing Checklist

- [ ] Application starts without errors
- [ ] Register user works
- [ ] Login returns token
- [ ] Check-in works with valid token
- [ ] Check-out works
- [ ] Apply leave works
- [ ] Get leaves returns data
- [ ] Create policy works (admin only)
- [ ] Invalid role returns 403
- [ ] Invalid token returns 401

---

## Next Steps

1. ✅ Backend working?
2. ✅ APIs returning correct responses?
3. ✅ JWT authentication functional?
4. 👉 **Start Frontend (Angular 14)**

---

## Documentation

- 📖 **API_DOCUMENTATION.md** - Detailed API reference
- 📖 **BACKEND_TESTING_GUIDE.md** - Complete testing guide
- 📖 **PROJECT_SUMMARY.md** - Full project overview
- 📖 **README.md** - Backend specific info

---

Last Updated: April 17, 2026
