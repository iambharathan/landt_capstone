# API Documentation - HR Attendance & Leave Management System

## Base URL
```
http://localhost:8080/api
```

---

## Authentication Endpoints

### 1. Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "Password123",
  "fullName": "John Doe",
  "role": "EMPLOYEE"
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "john@example.com",
  "username": "john_doe",
  "fullName": "John Doe",
  "role": "EMPLOYEE"
}
```

### 2. Login User
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "Password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "john@example.com",
  "username": "john_doe",
  "fullName": "John Doe",
  "role": "EMPLOYEE"
}
```

---

## Employee Endpoints
**Auth Required**: YES | **Role**: EMPLOYEE

### 1. Check-In
```http
POST /employee/check-in
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "userId": 1,
  "date": "2026-04-17",
  "checkInTime": "09:15:30",
  "checkOutTime": null,
  "status": "PRESENT"
}
```

### 2. Check-Out
```http
POST /employee/check-out
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "userId": 1,
  "date": "2026-04-17",
  "checkInTime": "09:15:30",
  "checkOutTime": "17:45:15",
  "status": "PRESENT"
}
```

### 3. Get My Attendance
```http
GET /employee/attendance
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": 1,
    "userId": 1,
    "date": "2026-04-17",
    "checkInTime": "09:15:30",
    "checkOutTime": "17:45:15",
    "status": "PRESENT"
  },
  ...
]
```

### 4. Get Attendance by Date Range
```http
GET /employee/attendance/date-range?startDate=2026-04-01&endDate=2026-04-30
Authorization: Bearer {token}

Response: 200 OK
[...]
```

### 5. Apply for Leave
```http
POST /employee/leave
Authorization: Bearer {token}
Content-Type: application/json

{
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "SICK",
  "reason": "Medical appointment"
}

Response: 201 Created
{
  "id": 1,
  "userId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "SICK",
  "status": "PENDING",
  "reason": "Medical appointment",
  "approverName": null,
  "rejectionReason": null
}
```

### 6. Get My Leaves
```http
GET /employee/leave
Authorization: Bearer {token}

Response: 200 OK
[...]
```

### 7. Get Leave Details
```http
GET /employee/leave/{id}
Authorization: Bearer {token}

Response: 200 OK
{...}
```

---

## Manager Endpoints
**Auth Required**: YES | **Role**: MANAGER

### 1. Get Pending Leaves
```http
GET /manager/leaves
Authorization: Bearer {token}

Response: 200 OK
[...]
```

### 2. Approve Leave
```http
PUT /manager/leave/{id}/approve
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "userId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "SICK",
  "status": "APPROVED",
  "reason": "Medical appointment",
  "approverName": "Manager Name",
  "rejectionReason": null
}
```

### 3. Reject Leave
```http
PUT /manager/leave/{id}/reject
Authorization: Bearer {token}
Content-Type: application/json

{
  "reason": "Not aligned with team schedule"
}

Response: 200 OK
{
  "id": 1,
  "userId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "SICK",
  "status": "REJECTED",
  "reason": "Medical appointment",
  "approverName": "Manager Name",
  "rejectionReason": "Not aligned with team schedule"
}
```

### 4. Get Leave Details
```http
GET /manager/leave/{id}
Authorization: Bearer {token}

Response: 200 OK
{...}
```

---

## Admin Endpoints
**Auth Required**: YES | **Role**: ADMIN

### User Management

#### 1. Create User
```http
POST /admin/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "jane_manager",
  "email": "jane@example.com",
  "password": "Password123",
  "fullName": "Jane Manager",
  "role": "MANAGER"
}

Response: 201 Created
{...}
```

#### 2. Get All Users
```http
GET /admin/users
Authorization: Bearer {token}

Response: 200 OK
[...]
```

#### 3. Get User by ID
```http
GET /admin/users/{id}
Authorization: Bearer {token}

Response: 200 OK
{...}
```

#### 4. Get Users by Role
```http
GET /admin/users/role/{role}
Authorization: Bearer {token}

Response: 200 OK
[...]
```

#### 5. Update User
```http
PUT /admin/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Updated Name",
  "isActive": true
}

Response: 200 OK
{...}
```

#### 6. Delete User
```http
DELETE /admin/users/{id}
Authorization: Bearer {token}

Response: 204 No Content
```

### Policy Management

#### 1. Create Policy
```http
POST /admin/policies
Authorization: Bearer {token}
Content-Type: application/json

{
  "leaveType": "SICK",
  "maxDays": 10,
  "rules": "Medical certificate required for more than 2 days"
}

Response: 201 Created
{...}
```

#### 2. Get All Policies
```http
GET /admin/policies
Authorization: Bearer {token}

Response: 200 OK
[...]
```

#### 3. Get Policy by ID
```http
GET /admin/policies/{id}
Authorization: Bearer {token}

Response: 200 OK
{...}
```

#### 4. Update Policy
```http
PUT /admin/policies/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "maxDays": 12,
  "rules": "Updated rules"
}

Response: 200 OK
{...}
```

#### 5. Delete Policy
```http
DELETE /admin/policies/{id}
Authorization: Bearer {token}

Response: 204 No Content
```

### Reports

#### 1. Get Comprehensive Report
```http
GET /admin/reports
Authorization: Bearer {token}

Response: 200 OK
{
  "attendanceSummary": {...},
  "leaveSummary": {...},
  "totalAttendanceRecords": 150,
  "totalLeaveRequests": 25
}
```

#### 2. Get Attendance Summary for User
```http
GET /admin/reports/attendance/{userId}
Authorization: Bearer {token}

Response: 200 OK
{
  "userId": 1,
  "totalRecords": 20,
  "presentDays": 18,
  "absentDays": 1,
  "lateDays": 1
}
```

#### 3. Get Leave Summary
```http
GET /admin/reports/leaves
Authorization: Bearer {token}

Response: 200 OK
{
  "totalLeaveRequests": 25,
  "pendingLeaves": 3,
  "approvedLeaves": 18,
  "rejectedLeaves": 4
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Field validation error",
  "timestamp": "2026-04-17T10:30:00",
  "path": "/api/auth/register"
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Invalid email or password",
  "timestamp": "2026-04-17T10:30:00",
  "path": "/api/auth/login"
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "message": "Access denied",
  "timestamp": "2026-04-17T10:30:00",
  "path": "/api/admin/users"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "User not found",
  "timestamp": "2026-04-17T10:30:00",
  "path": "/api/admin/users/999"
}
```

### 500 Server Error
```json
{
  "status": 500,
  "message": "An unexpected error occurred",
  "timestamp": "2026-04-17T10:30:00",
  "path": "/api/employee/check-in"
}
```

---

## Headers Required for All Requests (except /auth/*)

```
Authorization: Bearer {jwt_token}
Content-Type: application/json (for POST/PUT requests)
```

---

## JWT Token Structure
The JWT token received from login/register contains:
- **Subject (sub)**: User email
- **Role (role)**: User role (ADMIN, MANAGER, EMPLOYEE)
- **Issued At (iat)**: Token creation time
- **Expiration (exp)**: Token expiration time (24 hours from issue)

---
