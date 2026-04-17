# System Architecture & Technical Design

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                                 │
│                    Angular 14 Frontend                               │
│    (Login, Dashboard, Forms, Reports) → Port 4200                   │
└────────────────────────────┬──────────────────────────────────────┘
                             │ HTTP/REST (HTTPS in Production)
                             │ JWT Token in Headers
                             ↓
┌─────────────────────────────────────────────────────────────────────┐
│                    API GATEWAY / CORS FILTER                         │
│              (Spring Security + JWT Filter)                          │
│                    Port 8080                                         │
└────────────────────────────┬──────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ↓                    ↓                    ↓
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  AuthController  │ │ EmployeeCtrl     │ │ ManagerCtrl      │
│  /auth/**        │ │ /employee/**     │ │ /manager/**      │
└────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     ↓                     │
        │            ┌──────────────────┐          │
        │            │ AdminController  │          │
        │            │ /admin/**        │          │
        │            └──────────────────┘          │
        │                    │                     │
        └────────────────────┼─────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ↓                    ↓                    ↓
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│ AuthService      │ │ UserService      │ │ AttendanceService│
│ LeaveService     │ │ PolicyService    │ │ ReportService    │
│                  │ │                  │ │                  │
│ Business Logic   │ │ Business Logic   │ │ Business Logic   │
└────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ↓                    ↓                    ↓
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│ UserRepository   │ │AttendanceRepo    │ │ LeaveRepository  │
│                  │ │ PolicyRepository │ │                  │
│ JPA Queries      │ │                  │ │ JPQL Queries     │
│ (CRUD)           │ │ (Custom Queries) │ │ (Complex Filters)│
└────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ↓                   ↓
              ┌──────────────┐  ┌──────────────┐
              │   MySQL DB   │  │  ORM Layer   │
              │ (Database)   │  │ (Hibernate)  │
              └──────────────┘  └──────────────┘
```

---

## Request Flow Diagram

### Authentication Flow
```
User Input (Email, Password)
          │
          ↓
    LoginRequest DTO
          │
          ↓
    AuthController.login()
          │
          ↓
    AuthService.login()
          ├─→ Find user by email
          ├─→ Verify password (BCrypt)
          ├─→ Validate user is active
          │
          ↓
    JwtTokenProvider.generateToken()
          ├─→ Create JWT with email & role
          ├─→ Sign with secret key
          ├─→ Set 24hr expiration
          │
          ↓
    LoginResponse (Token + User Info)
          │
          ↓
    Client (Store token in localStorage)
```

### Protected Request Flow
```
Client Request (with JWT Token)
          │
          ↓
    JwtAuthenticationFilter
          ├─→ Extract token from header
          ├─→ Validate token signature
          ├─→ Extract email & role from claims
          │
          ↓
    SecurityContextHolder
          ├─→ Create authentication object
          ├─→ Add authorities (ROLE_*)
          │
          ↓
    SecurityContext (Thread-local storage)
          │
          ↓
    Controller Method
          ├─→ Check @PreAuthorize annotations
          ├─→ Verify user role/authority
          │
          ↓
    Service Layer
          ├─→ Get current user (if needed)
          ├─→ Execute business logic
          ├─→ Access repository
          │
          ↓
    Database Operation
          │
          ↓
    Response (with data or error)
```

---

## Database Relationships

```
┌─────────────────────┐
│      User           │
├─────────────────────┤
│ id (PK)             │
│ username (UNIQUE)   │
│ email (UNIQUE)      │
│ password (encrypted)│
│ fullName            │
│ role (ENUM)         │
│ isActive            │
└──────────┬──────────┘
           │ 1:N
           │
    ┌──────┴──────┬──────────┐
    │             │          │
    ↓             ↓          ↓
┌──────────┐  ┌─────────┐  ┌────────────┐
│Attendance│  │ Leave   │  │ Approved   │
│          │  │Request  │  │LeaveRequest│
│(FK:user) │  │(FK:user)│  │(FK:user)   │
└──────────┘  │(FK:appr)│  └────────────┘
              └─────────┘
              
Policy (Independent)
```

### Database Schema Relationships

```sql
-- User ← Attendance (1:N)
ALTER TABLE attendance 
ADD CONSTRAINT fk_att_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- User ← LeaveRequest (1:N)
ALTER TABLE leave_requests 
ADD CONSTRAINT fk_leave_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- User ← LeaveRequest.approved_by (1:N)
ALTER TABLE leave_requests 
ADD CONSTRAINT fk_leave_approver 
FOREIGN KEY (approved_by) REFERENCES users(id);
```

---

## Security Architecture

```
┌─────────────────────────────────────────────┐
│          Incoming HTTP Request              │
└──────────────┬──────────────────────────────┘
               │
               ↓
        ┌──────────────────┐
        │ CORS Filter      │
        │ (Allowed Origins)│
        └────────┬─────────┘
                 │
                 ↓
        ┌──────────────────────────────┐
        │ JWT Authentication Filter     │
        │ (JwtAuthenticationFilter)     │
        │ ├─ Extract token from header │
        │ ├─ Validate signature        │
        │ ├─ Extract claims            │
        │ └─ Set SecurityContext       │
        └────────┬─────────────────────┘
                 │
                 ↓
        ┌──────────────────────────────┐
        │ Spring Security Authorization │
        │ ├─ Check @PreAuthorize       │
        │ ├─ Check role permissions    │
        │ └─ Check method security     │
        └────────┬─────────────────────┘
                 │
              YES│ Access Granted
                 ↓
        ┌──────────────────┐
        │ Controller Method│
        │ (Process Request)│
        └────────┬─────────┘
                 │
                 ↓
        ┌──────────────────┐
        │ Return Response  │
        └──────────────────┘

                 │
              NO │ Access Denied
                 ↓
        ┌──────────────────────────────┐
        │ Return 403 Forbidden Error   │
        │ or 401 Unauthorized Error    │
        └──────────────────────────────┘
```

### Encryption & Security Layers

```
Password Flow:
User Input → BCrypt Hashing → DB Storage → BCrypt Verify on Login

Token Flow:
Claims (email, role) 
    → HMAC-SHA512 Signing 
    → JWT Token 
    → Base64 Encoding 
    → Client Storage 
    → Send in Headers 
    → Signature Verification 
    → Claims Extraction

Session:
Stateless (No server-side sessions)
All info in JWT token claims
Token expires after 24 hours
```

---

## Component Interaction Diagram

```
┌──────────────┐
│   Client     │
│  (Angular)   │
└──────┬───────┘
       │ HTTP Request + JWT
       │
       ↓
┌─────────────────────────────────┐
│      AuthFilter                 │
│ Validates token & sets security │
└──────────────┬──────────────────┘
               │
               ↓
┌─────────────────────────────────┐
│    @RestController              │
│  - AuthController               │
│  - EmployeeController           │
│  - ManagerController            │
│  - AdminController              │
└──────────────┬──────────────────┘
               │
               ↓
┌─────────────────────────────────┐
│    Service Layer                │
│  - AuthService                  │
│  - UserService                  │
│  - AttendanceService            │
│  - LeaveService                 │
│  - PolicyService                │
│  - ReportService                │
└──────────────┬──────────────────┘
               │
               ↓
┌─────────────────────────────────┐
│    Repository Layer (JPA)       │
│  - UserRepository               │
│  - AttendanceRepository         │
│  - LeaveRepository              │
│  - PolicyRepository             │
└──────────────┬──────────────────┘
               │
               ↓
┌─────────────────────────────────┐
│    ORM (Hibernate)              │
│  - Entity mapping               │
│  - JPQL query generation        │
│  - SQL preparation              │
└──────────────┬──────────────────┘
               │
               ↓
┌─────────────────────────────────┐
│    MySQL Database Driver        │
└──────────────┬──────────────────┘
               │
               ↓
┌─────────────────────────────────┐
│    MySQL Database               │
│  - users table                  │
│  - attendance table             │
│  - leave_requests table         │
│  - policies table               │
└─────────────────────────────────┘
```

---

## API Endpoint Hierarchy

```
/api
├── /auth                          (Public)
│   ├── POST /register            (No Auth)
│   └── POST /login               (No Auth)
│
├── /admin                         (ADMIN only)
│   ├── /users
│   │   ├── POST                  (Create)
│   │   ├── GET                   (List all)
│   │   ├── GET /{id}             (Get one)
│   │   ├── GET /role/{role}      (Filter)
│   │   ├── PUT /{id}             (Update)
│   │   └── DELETE /{id}          (Deactivate)
│   ├── /policies
│   │   ├── POST                  (Create)
│   │   ├── GET                   (List)
│   │   ├── PUT /{id}             (Update)
│   │   └── DELETE /{id}          (Deactivate)
│   └── /reports
│       ├── /attendance           (Attendance report)
│       └── /leaves               (Leave report)
│
├── /manager                       (MANAGER + ADMIN)
│   ├── /leaves                   (Get pending)
│   ├── /leave/{id}/approve       (Approve)
│   ├── /leave/{id}/reject        (Reject)
│   └── /team-attendance          (Team view)
│
└── /employee                      (EMPLOYEE + MANAGER + ADMIN)
    ├── /check-in                 (POST)
    ├── /check-out                (POST)
    ├── /attendance
    │   └── /today                (GET)
    ├── /attendance               (GET all)
    ├── /leave                    (POST - apply)
    ├── /leaves                   (GET - my leaves)
    └── /leave/{id}               (GET - specific)
```

---

## Error Handling Flow

```
Request Processing
        │
        ├─→ Validation Error
        │        │
        │        ↓ Exception thrown
        │   GlobalExceptionHandler
        │        │
        │        ├─→ MethodArgumentNotValidException
        │        │   → 400 Bad Request
        │        │
        │        └─→ Response with error details
        │
        ├─→ Authentication Error
        │        │
        │        ↓
        │   InvalidCredentialsException
        │        │
        │        └─→ 401 Unauthorized
        │
        ├─→ Authorization Error
        │        │
        │        ↓
        │   AccessDeniedException
        │        │
        │        └─→ 403 Forbidden
        │
        ├─→ Resource Not Found
        │        │
        │        ↓
        │   ResourceNotFoundException
        │        │
        │        └─→ 404 Not Found
        │
        └─→ Unexpected Error
                 │
                 ↓
            Exception (Any)
                 │
                 └─→ 500 Internal Server Error
```

---

## Data Flow - Complete Workflow

```
EMPLOYEE CHECK-IN WORKFLOW:

Employee App                Backend                    Database
    │                         │                            │
    ├─→ Click Check-In ──────→ POST /employee/check-in
    │                         │
    │                    SecurityFilter
    │                    Validates JWT
    │                         │
    │                    EmployeeController
    │                         │
    │                    AttendanceService
    │                    ├─ Get current user
    │                    ├─ Check if already checked in
    │                    ├─ Create Attendance object
    │                    │
    │                    └─→ AttendanceRepository
    │                         │
    │                         ├─→ Hibernate (ORM)
    │                         │
    │                         └──→ INSERT attendance
    │                               VALUES (user_id, today, now(), null, PRESENT)
    │                                   │
    │                                   ↓ Query executed
    │                              New record created
    │                         │
    │                    AttendanceService
    │                    Convert to DTO
    │                         │
    │    ←─────── 200 OK ─────┤
    │  AttendanceDTO
    │  {
    │    "id": 1,
    │    "date": "2026-04-17",
    │    "checkInTime": "09:30:45",
    │    "status": "PRESENT"
    │  }
    │
    ├─→ Show Success Message
```

---

## Performance Optimization Points

```
Optimization Areas:

1. Database Queries
   ├─ Custom JPQL queries in repositories
   ├─ Indexes on frequently queried columns (user_id, date, status)
   ├─ Eager/Lazy loading optimization
   └─ Connection pooling (HikariCP)

2. API Response
   ├─ DTOs reduce payload size
   ├─ Only required fields returned
   ├─ JSON serialization optimized
   └─ Gzip compression

3. Security
   ├─ JWT validation is fast (no DB lookup)
   ├─ Password hashing uses efficient BCrypt
   ├─ Token caching in SecurityContext
   └─ Stateless reduces server memory

4. Caching Potential
   ├─ Policies (rarely change)
   ├─ User roles (cached in JWT)
   ├─ Report aggregations
   └─ Session data (if implemented)
```

---

## Deployment Architecture (Future)

```
Production Environment:

┌──────────────────────────────────────────────────┐
│            Load Balancer (HAProxy/Nginx)         │
└──────────────┬──────────────────────────────────┘
               │
    ┌──────────┼──────────┐
    ↓          ↓          ↓
┌────────┐ ┌────────┐ ┌────────┐
│Backend │ │Backend │ │Backend │
│ App 1  │ │ App 2  │ │ App 3  │
│(Java)  │ │(Java)  │ │(Java)  │
└────┬───┘ └────┬───┘ └────┬───┘
     │          │          │
     └──────────┼──────────┘
                │
    ┌───────────┴───────────┐
    ↓                       ↓
┌─────────────┐     ┌─────────────────┐
│ Master DB   │     │ Slave DB        │
│ (MySQL)     │←───→│ (MySQL Read)    │
└─────────────┘     └─────────────────┘

+ CDN for static assets
+ Redis for session/cache
+ Monitoring & Logging (ELK/Prometheus)
+ CI/CD Pipeline (GitHub Actions)
```

---

Last Updated: April 17, 2026
