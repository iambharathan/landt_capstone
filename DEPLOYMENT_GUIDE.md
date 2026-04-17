# Deployment Guide

## Local Development Deployment

### Prerequisites Check
```bash
# Verify Java 17
java -version
# Expected: openjdk version "17.x.x"

# Verify Maven
mvn -version
# Expected: Maven 3.8.x

# Verify MySQL
mysql --version
# Expected: mysql Ver 8.0.x
```

### Development Environment Setup

**Step 1: Create Development Database**
```bash
mysql -u root -p

# Create database
CREATE DATABASE attendance_db;

# Create test user (optional)
CREATE USER 'att_dev'@'localhost' IDENTIFIED BY 'dev_password';
GRANT ALL PRIVILEGES ON attendance_db.* TO 'att_dev'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

**Step 2: Configure Spring Boot**
File: `src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/attendance_db
    username: root  # or att_dev
    password: root  # your password
  jpa:
    hibernate:
      ddl-auto: update  # auto-creates tables
```

**Step 3: Build Project**
```bash
cd /Users/bharathank/pjs/l&t_capstone/attendance-backend
mvn clean install
```

**Step 4: Run Application**
```bash
mvn spring-boot:run
```

**Step 5: Verify Running**
```bash
# In another terminal
curl http://localhost:8080/api/auth/login
# Expected: 400 Bad Request (not Connection Refused)
```

---

## Docker Deployment (Optional)

### Create Dockerfile

File: `attendance-backend/Dockerfile`
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/attendance-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Create Docker Compose

File: `docker-compose.yml`
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: attendance_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  backend:
    build: ./attendance-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/attendance_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    ports:
      - "8080:8080"
    depends_on:
      - mysql

volumes:
  mysql_data:
```

### Build & Run with Docker
```bash
# Build images
docker-compose build

# Run containers
docker-compose up

# Stop containers
docker-compose down
```

---

## Production Deployment

### Pre-Deployment Checklist

**Security:**
- [ ] Change default JWT secret in `application.yml`
- [ ] Set strong MySQL password
- [ ] Enable HTTPS/SSL
- [ ] Configure firewall rules
- [ ] Setup rate limiting
- [ ] Enable audit logging

**Configuration:**
- [ ] Update CORS origins for frontend domain
- [ ] Set appropriate JWT expiration
- [ ] Configure logging levels
- [ ] Setup error monitoring (e.g., Sentry)
- [ ] Configure backups

**Performance:**
- [ ] Enable database connection pooling
- [ ] Configure JVM heap size
- [ ] Enable caching (Redis if needed)
- [ ] Setup CDN for static assets
- [ ] Configure load balancer

### Production Build

**Step 1: Update Configuration**
File: `src/main/resources/application-prod.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db-host:3306/attendance_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate  # Don't auto-update
    show-sql: false

  jackson:
    serialization:
      write-dates-as-timestamps: false

server:
  port: 8080
  ssl:
    key-store: /path/to/keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12

jwt:
  secret: ${JWT_SECRET}  # Use environment variable
  expiration: 86400000

logging:
  level:
    root: INFO
    com.edutech.attendance: INFO
```

**Step 2: Build Production JAR**
```bash
cd attendance-backend

# Build with production profile
mvn clean package -DskipTests -P prod

# Result: target/attendance-backend-1.0.0.jar
```

**Step 3: Deploy to Server**
```bash
# Copy JAR to server
scp target/attendance-backend-1.0.0.jar user@server:/opt/app/

# SSH to server
ssh user@server

# Run application
java -jar /opt/app/attendance-backend-1.0.0.jar \
  --spring.profiles.active=prod \
  --server.port=8080 \
  --spring.datasource.url=jdbc:mysql://localhost:3306/attendance_db \
  --spring.datasource.username=db_user \
  --spring.datasource.password=db_password \
  --jwt.secret=your_production_secret_key
```

### Running as Service (Linux)

**Create Systemd Service**
File: `/etc/systemd/system/attendance-backend.service`
```ini
[Unit]
Description=HR Attendance Backend Service
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/app
Environment="SPRING_PROFILES_ACTIVE=prod"
ExecStart=/usr/bin/java -jar /opt/app/attendance-backend-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Enable & Start Service**
```bash
sudo systemctl daemon-reload
sudo systemctl enable attendance-backend
sudo systemctl start attendance-backend

# Check status
sudo systemctl status attendance-backend

# View logs
sudo journalctl -u attendance-backend -f
```

---

## Cloud Deployment Options

### AWS Deployment

**Using Elastic Beanstalk:**
```bash
# Install EB CLI
brew install aws-elasticbeanstalk

# Initialize
eb init -p java-17 attendance-backend

# Create environment
eb create attendance-prod

# Deploy
eb deploy

# Open app
eb open
```

**Using EC2:**
1. Launch EC2 instance (Java 17 AMI)
2. Install MySQL (or use RDS)
3. Copy JAR file
4. Run application
5. Setup security groups
6. Configure domain/SSL

### Azure Deployment

**Using App Service:**
```bash
# Create resource group
az group create --name AttendanceRG --location eastus

# Create app service plan
az appservice plan create \
  --name AttendancePlan \
  --resource-group AttendanceRG \
  --sku B2

# Create web app
az webapp create \
  --resource-group AttendanceRG \
  --plan AttendancePlan \
  --name attendance-api \
  --runtime "JAVA|17-java17"

# Deploy JAR
az webapp up \
  --resource-group AttendanceRG \
  --name attendance-api \
  --runtime "JAVA|17"
```

### Google Cloud Deployment

**Using Cloud Run:**
```bash
# Build container image
gcloud builds submit --tag gcr.io/PROJECT_ID/attendance-backend

# Deploy to Cloud Run
gcloud run deploy attendance-backend \
  --image gcr.io/PROJECT_ID/attendance-backend \
  --platform managed \
  --region us-central1
```

---

## Database Migration for Production

### Initial Setup
```sql
-- Run once for production database
USE attendance_db;

-- Verify tables created
SHOW TABLES;
DESC users;
DESC attendance;
DESC leave_requests;
DESC policies;

-- Create indexes for performance
ALTER TABLE attendance ADD INDEX idx_user_date (user_id, date);
ALTER TABLE leave_requests ADD INDEX idx_status (status);
ALTER TABLE leave_requests ADD INDEX idx_user_id (user_id);
```

### Backup Strategy

**Daily Backup**
```bash
#!/bin/bash
# backup.sh
DATE=$(date +%Y%m%d)
BACKUP_DIR="/backups/attendance"

mysqldump -u root -p$MYSQL_PASSWORD attendance_db > \
  $BACKUP_DIR/attendance_db_$DATE.sql

# Keep last 30 days
find $BACKUP_DIR -mtime +30 -delete
```

**Restore from Backup**
```bash
mysql -u root -p attendance_db < attendance_db_20260417.sql
```

---

## Monitoring & Logging

### Application Monitoring

**Health Check Endpoint**
```bash
curl http://localhost:8080/actuator/health
```

**Performance Metrics**
```bash
# Enable actuator in application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### Log Aggregation

**ELK Stack (Elasticsearch, Logstash, Kibana)**
```yaml
# Configure in application.yml
logging:
  level:
    root: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/attendance/app.log
    max-size: 10MB
    max-history: 30
```

---

## Performance Tuning

### JVM Configuration

**Memory Settings**
```bash
java -Xms512m -Xmx2048m -jar attendance-backend-1.0.0.jar
```

**Garbage Collection**
```bash
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -jar attendance-backend-1.0.0.jar
```

### Database Optimization

**Connection Pool**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      max-lifetime: 1800000
      idle-timeout: 600000
```

**Query Optimization**
```java
// Use JPQL with specific fields
@Query("SELECT new com.edutech.attendance.dto.UserDTO(u.id, u.email, u.fullName) FROM User u")
List<UserDTO> getActiveUsers();
```

---

## Rollback Plan

### Version Control Strategy
```bash
# Tag each release
git tag -a v1.0.0 -m "Production release"
git push origin v1.0.0

# Checkout previous version
git checkout v0.9.0
mvn clean package
```

### Quick Rollback
```bash
# Stop current application
sudo systemctl stop attendance-backend

# Backup current JAR
cp attendance-backend-1.0.0.jar \
   attendance-backend-1.0.0.jar.backup

# Restore previous version
cp attendance-backend-0.9.0.jar \
   attendance-backend-1.0.0.jar

# Start application
sudo systemctl start attendance-backend
```

---

## Troubleshooting Production Issues

### Application Won't Start
```bash
# Check logs
tail -f /var/log/attendance/app.log

# Common issues:
# 1. Port already in use: lsof -i :8080
# 2. Database connection: mysql -u user -p
# 3. Memory issue: Check JVM settings
# 4. Permission: Check file ownership
```

### High Memory Usage
```bash
# Check heap size
jmap -heap <PID>

# Increase if needed
java -Xmx4096m -jar app.jar

# Or modify systemd service file
```

### Database Locked
```bash
# Check active connections
SHOW PROCESSLIST;

# Kill specific connection
KILL <process_id>;

# Check table locks
SHOW OPEN TABLES WHERE In_use > 0;
```

---

## Security Checklist for Production

- [ ] Change default passwords
- [ ] Enable HTTPS/SSL certificates
- [ ] Configure firewall
- [ ] Setup rate limiting
- [ ] Enable request logging
- [ ] Configure CORS properly
- [ ] Use environment variables for secrets
- [ ] Enable database backups
- [ ] Setup monitoring alerts
- [ ] Enable audit logging
- [ ] Implement WAF rules
- [ ] Setup DDoS protection
- [ ] Regular security updates
- [ ] Penetration testing

---

## Support & Troubleshooting

For deployment issues, check:
1. Application logs
2. MySQL error logs
3. System logs (journalctl)
4. BACKEND_TESTING_GUIDE.md
5. Architecture diagrams in ARCHITECTURE.md

---

**Last Updated:** April 17, 2026
**Version:** 1.0.0
