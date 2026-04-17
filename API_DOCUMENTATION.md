# API Documentation - HR Attendance & Leave Management System

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints except `/auth/**` require JWT token in the header:
```
Authorization: Bearer <jwt_token>
```

---

## 1. Authentication APIs (Public)

### Register User
**Endpoint:** `POST /auth/register`
**Auth Required:** No
**Request Body:**
```json
{
  "username": "john.doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "fullName": "John Doe",
  "role": "EMPLOYEE"
}
```
**Response (201 Created):**
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

### Login User
**Endpoint:** `POST /auth/login`
**Auth Required:** No
**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```
**Response (200 OK):**
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

---

## 2. Employee APIs

### Check-In
**Endpoint:** `POST /employee/check-in`
**Auth Required:** Yes (EMPLOYEE/MANAGER/ADMIN)
**Request Body:** Empty
**Response (200 OK):**
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

### Check-Out
**Endpoint:** `POST /employee/check-out`
**Auth Required:** Yes (EMPLOYEE/MANAGER/ADMIN)
**Request Body:** Empty
**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "date": "2026-04-17",
  "checkInTime": "09:30:45",
  "checkOutTime": "18:15:30",
  "status": "PRESENT"
}
```

### Get Today's Attendance
**Endpoint:** `GET /employee/attendance/today`
**Auth Required:** Yes
**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "date": "2026-04-17",
  "checkInTime": "09:30:45",
  "checkOutTime": "18:15:30",
  "status": "PRESENT"
}
```

### Get User Attendance
**Endpoint:** `GET /employee/attendance`
**Auth Required:** Yes
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "date": "2026-04-17",
    "checkInTime": "09:30:45",
    "checkOutTime": "18:15:30",
    "status": "PRESENT"
  },
  {
    "id": 2,
    "userId": 1,
    "date": "2026-04-16",
    "checkInTime": "09:45:30",
    "checkOutTime": "18:00:00",
    "status": "LATE"
  }
]
```

### Apply for Leave
**Endpoint:** `POST /employee/leave`
**Auth Required:** Yes
**Request Body:**
```json
{
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "CASUAL",
  "reason": "Personal work"
}
```
**Response (201 Created):**
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

### Get User Leave Requests
**Endpoint:** `GET /employee/leaves`
**Auth Required:** Yes
**Response (200 OK):**
```json
[
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
]
```

### Get Leave By ID
**Endpoint:** `GET /employee/leave/{id}`
**Auth Required:** Yes
**Response (200 OK):**
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

## 3. Manager APIs

### Get Pending Leave Requests
**Endpoint:** `GET /manager/leaves`
**Auth Required:** Yes (MANAGER/ADMIN)
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 2,
    "startDate": "2026-05-01",
    "endDate": "2026-05-05",
    "leaveType": "CASUAL",
    "status": "PENDING",
    "reason": "Personal work",
    "approverName": null,
    "rejectionReason": null
  }
]
```

### Approve Leave Request
**Endpoint:** `PUT /manager/leave/{id}/approve`
**Auth Required:** Yes (MANAGER/ADMIN)
**Request Body:** Empty
**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 2,
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "CASUAL",
  "status": "APPROVED",
  "reason": "Personal work",
  "approverName": "Manager Name",
  "rejectionReason": null
}
```

### Reject Leave Request
**Endpoint:** `PUT /manager/leave/{id}/reject`
**Auth Required:** Yes (MANAGER/ADMIN)
**Request Body:**
```json
{
  "rejectionReason": "Insufficient staffing"
}
```
**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 2,
  "startDate": "2026-05-01",
  "endDate": "2026-05-05",
  "leaveType": "CASUAL",
  "status": "REJECTED",
  "reason": "Personal work",
  "approverName": "Manager Name",
  "rejectionReason": "Insufficient staffing"
}
```

### Get Team Attendance
**Endpoint:** `GET /manager/team-attendance`
**Auth Required:** Yes (MANAGER/ADMIN)
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 2,
    "date": "2026-04-17",
    "checkInTime": "09:30:45",
    "checkOutTime": "18:15:30",
    "status": "PRESENT"
  }
]
```

---

## 4. Admin APIs

### Create User
**Endpoint:** `POST /admin/users`
**Auth Required:** Yes (ADMIN)
**Request Body:**
```json
{
  "username": "jane.smith",
  "email": "jane@example.com",
  "password": "securePassword123",
  "fullName": "Jane Smith",
  "role": "MANAGER"
}
```
**Response (201 Created):**
```json
{
  "id": 2,
  "username": "jane.smith",
  "email": "jane@example.com",
  "fullName": "Jane Smith",
  "role": "MANAGER",
  "isActive": true
}
```

### Get All Users
**Endpoint:** `GET /admin/users`
**Auth Required:** Yes (ADMIN)
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "john.doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "role": "EMPLOYEE",
    "isActive": true
  },
  {
    "id": 2,
    "username": "jane.smith",
    "email": "jane@example.com",
    "fullName": "Jane Smith",
    "role": "MANAGER",
    "isActive": true
  }
]
```

### Get User By ID
**Endpoint:** `GET /admin/users/{id}`
**Auth Required:** Yes (ADMIN)
**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "EMPLOYEE",
  "isActive": true
}
```

### Get Users By Role
**Endpoint:** `GET /admin/users/role/{role}`
**Auth Required:** Yes (ADMIN)
**Response (200 OK):**
```json
[
  {
    "id": 2,
    "username": "jane.smith",
    "email": "jane@example.com",
    "fullName": "Jane Smith",
    "role": "MANAGER",
    "isActive": true
  }
]
```

### Update User
**Endpoint:** `PUT /admin/users/{id}`
**Auth Required:** Yes (ADMIN)
**Request Body:**
```json
{
  "fullName": "John Updated",
  "isActive": true
}
```
**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john.doe",
  "email": "john@example.com",
  "fullName": "John Updated",
  "role": "EMPLOYEE",
  "isActive": true
}
```

### Delete User (Deactivate)
**Endpoint:** `DELETE /admin/users/{id}`
**Auth Required:** Yes (ADMIN)
**Response (200 OK):**
```json
{
  "message": "User deactivated successfully"
}
```

### Create Leave Policy
**Endpoint:** `POST /admin/policies`
**Auth Required:** Yes (ADMIN)
**Request Body:**
```json
{
  "leaveType": "CASUAL",
  "maxDays": 12,
  "rules": "Casual leave must be taken within the same financial year"
}
```
**Response (201 Created):**
```json
{
  "id": 1,
  "leaveType": "CASUAL",
  "maxDays": 12,
  "rules": "Casual leave must be taken within the same financial year",
  "isActive": true
}
```

### Get All Leave Policies
**Endpoint:** `GET /admin/policies`
**Auth Required:** Yes (ADMIN)
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "leaveType": "CASUAL",
    "maxDays": 12,
    "rules": "Casual leave must be taken within the same financial year",
    "isActive": true
  },
  {
    "id": 2,
    "leaveType": "SICK",
    "maxDays": 6,
    "rules": "Medical certificate required for more than 2 days",
    "isActive": true
  }
]
```

### Update Leave Policy
**Endpoint:** `PUT /admin/policies/{id}`
**Auth Required:** Yes (ADMIN)
**Request Body:**
```json
{
  "maxDays": 15,
  "rules": "Updated rule",
  "isActive": true
}
```
**Response (200 OK):**
```json
{
  "id": 1,
  "leaveType": "CASUAL",
  "maxDays": 15,
  "rules": "Updated rule",
  "isActive": true
}
```

### Get Attendance Reports
**Endpoint:** `GET /admin/reports/attendance`
**Auth Required:** Yes (ADMIN)
**Query Params (Optional):**
- `startDate`: YYYY-MM-DD
- `endDate`: YYYY-MM-DD
- `userId`: User ID

**Response (200 OK):**
```json
{
  "totalRecords": 150,
  "presentCount": 142,
  "absentCount": 5,
  "lateCount": 3,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "date": "2026-04-17",
      "checkInTime": "09:30:45",
      "checkOutTime": "18:15:30",
      "status": "PRESENT"
    }
  ]
}
```

### Get Leave Reports
**Endpoint:** `GET /admin/reports/leaves`
**Auth Required:** Yes (ADMIN)
**Query Params (Optional):**
- `startDate`: YYYY-MM-DD
- `endDate`: YYYY-MM-DD
- `status`: PENDING/APPROVED/REJECTED

**Response (200 OK):**
```json
{
  "totalRequests": 50,
  "approvedCount": 35,
  "rejectedCount": 10,
  "pendingCount": 5,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "startDate": "2026-05-01",
      "endDate": "2026-05-05",
      "leaveType": "CASUAL",
      "status": "APPROVED",
      "reason": "Personal work",
      "approverName": "Manager Name",
      "rejectionReason": null
    }
  ]
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Validation failed: email must be valid",
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
  "message": "User not found with id: 999",
  "timestamp": "2026-04-17T10:30:00",
  "path": "/api/admin/users/999"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "An unexpected error occurred",
  "timestamp": "2026-04-17T10:30:00",
  "path": "/api/employee/check-in"
}
```

---

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Request succeeded |
| 201 | Created - Resource created |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Invalid credentials or missing token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error - Server error |

---

## Role-Based Access

| Endpoint | ADMIN | MANAGER | EMPLOYEE |
|----------|-------|---------|----------|
| /auth/** | ✓ | ✓ | ✓ |
| /admin/** | ✓ | ✗ | ✗ |
| /manager/** | ✓ | ✓ | ✗ |
| /employee/** | ✓ | ✓ | ✓ |

---
