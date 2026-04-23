# Hospital Management System - API Reference & Quick Start Guide

## Quick Start Guide

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 5.7+ or 8.0
- Git (for version control)
- Postman or cURL (for API testing)

### System Requirements
- RAM: Minimum 4GB (Recommended 8GB)
- Disk Space: Minimum 2GB
- Network: localhost connectivity

---

## Installation & Startup

### Step 1: Database Setup

```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE hospital_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Select database
USE hospital_management;

# Exit MySQL
EXIT;
```

### Step 2: Start Eureka Server

```bash
# Navigate to eureka-server directory
cd "C:\Users\ritam\OneDrive\Documents\Application\Hospital Management\eureka-server"

# Build project
mvn clean package

# Run the server
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar

# Expected output:
# Eureka Server started successfully on port 8761
# Visit: http://localhost:8761/eureka/
```

### Step 3: Start User Service

```bash
# In a new terminal, navigate to user-service
cd "C:\Users\ritam\OneDrive\Documents\Application\Hospital Management\user-service"

# Build project
mvn clean package

# Run the service
java -jar target/user-service-0.0.1-SNAPSHOT.jar

# Expected output:
# user-service registered with Eureka
# Server started on port 8082
```

### Step 4: Start Admin Service

```bash
# In a new terminal, navigate to admin-service
cd "C:\Users\ritam\OneDrive\Documents\Application\Hospital Management\admin-service"

# Build project
mvn clean package

# Run the service
java -jar target/admin-service-0.0.1-SNAPSHOT.jar

# Expected output:
# admin-service registered with Eureka
# Server started on port 8083
```

### Step 5: Start Hospital Service

```bash
# In a new terminal, navigate to system (Hospital Service)
cd "C:\Users\ritam\OneDrive\Documents\Application\Hospital Management\system"

# Build project
mvn clean package

# Run the service
java -jar target/system-0.0.1-SNAPSHOT.jar

# Expected output:
# hospital-service registered with Eureka
# Server started on port 8081
```

### Step 6: Start API Gateway

```bash
# In a new terminal, navigate to api-gateway
cd "C:\Users\ritam\OneDrive\Documents\Application\Hospital Management\api-gateway"

# Build project
mvn clean package

# Run the service
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar

# Expected output:
# API Gateway started successfully
# Server started on port 8080
```

### Verification

```bash
# Check Eureka Dashboard
# Open browser: http://localhost:8761/eureka/

# You should see all 5 services with UP status:
# - eureka-server
# - user-service
# - admin-service
# - hospital-service
# - api-gateway
```

---

## Complete API Reference

### Authentication Endpoints (User Service)

#### 1. User Registration

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "patient@example.com",
  "password": "SecurePass123!",
  "fullName": "John Doe",
  "userType": "PATIENT"
}
```

**Response (201 Created):**
```json
{
  "message": "User registered successfully",
  "userId": 1,
  "email": "patient@example.com",
  "fullName": "John Doe",
  "userType": "PATIENT"
}
```

**Error Responses:**
```json
// 400 Bad Request - Invalid input
{
  "error": "Email already exists"
}

// 400 Bad Request - Validation failed
{
  "error": "Password must contain at least 8 characters"
}
```

---

#### 2. User Login

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "patient@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwYXRpZW50QGV4YW1wbGUuY29tIiwiZXhwIjoxNzAyMDg2NDAwLCJpYXQiOjE3MDIwMDAwMDB9.SIGNATURE",
  "userId": 1,
  "email": "patient@example.com",
  "fullName": "John Doe",
  "userType": "PATIENT",
  "expiresIn": 86400000
}
```

**Error Responses:**
```json
// 401 Unauthorized - Invalid credentials
{
  "error": "Invalid email or password"
}

// 401 Unauthorized - User not found
{
  "error": "User not found"
}
```

---

#### 3. Get User by ID

```http
GET http://localhost:8080/api/auth/user/1
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "patient@example.com",
  "fullName": "John Doe",
  "userType": "PATIENT",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T10:30:00Z"
}
```

---

#### 4. Get User by Email

```http
GET http://localhost:8080/api/auth/user/email/patient@example.com
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "patient@example.com",
  "fullName": "John Doe",
  "userType": "PATIENT",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T10:30:00Z"
}
```

---

#### 5. User Service Health Check

```http
GET http://localhost:8080/api/auth/health
```

**Response (200 OK):**
```json
{
  "status": "UP",
  "service": "user-service"
}
```

---

### Admin Endpoints (Admin Service)

#### 1. Admin Login

```http
POST http://localhost:8080/api/admin/login
Content-Type: application/json

{
  "email": "admin@hospital.com",
  "password": "AdminPass123!"
}
```

**Response (200 OK):**
```json
{
  "message": "Admin login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "adminId": 1,
  "email": "admin@hospital.com",
  "fullName": "System Administrator",
  "role": "SUPER_ADMIN",
  "department": "OPERATIONS",
  "expiresIn": 86400000
}
```

---

#### 2. Create Admin

```http
POST http://localhost:8080/api/admin/create
Content-Type: application/json
Authorization: Bearer <ADMIN_JWT_TOKEN>

{
  "email": "financeadmin@hospital.com",
  "password": "FinancePass123!",
  "fullName": "Finance Manager",
  "role": "MANAGER",
  "department": "FINANCE"
}
```

**Response (201 Created):**
```json
{
  "message": "Admin created successfully",
  "adminId": 2,
  "email": "financeadmin@hospital.com",
  "fullName": "Finance Manager",
  "role": "MANAGER",
  "department": "FINANCE"
}
```

---

#### 3. Get Admin by ID

```http
GET http://localhost:8080/api/admin/1
Authorization: Bearer <ADMIN_JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "admin@hospital.com",
  "fullName": "System Administrator",
  "role": "SUPER_ADMIN",
  "department": "OPERATIONS",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T10:30:00Z"
}
```

---

#### 4. Get Admin by Email

```http
GET http://localhost:8080/api/admin/email/admin@hospital.com
Authorization: Bearer <ADMIN_JWT_TOKEN>
```

---

#### 5. Update Admin

```http
PUT http://localhost:8080/api/admin/1
Content-Type: application/json
Authorization: Bearer <ADMIN_JWT_TOKEN>

{
  "email": "admin@hospital.com",
  "fullName": "Updated Admin Name",
  "role": "ADMIN",
  "department": "OPERATIONS"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "admin@hospital.com",
  "fullName": "Updated Admin Name",
  "role": "ADMIN",
  "department": "OPERATIONS",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T11:30:00Z"
}
```

---

#### 6. Admin Service Health Check

```http
GET http://localhost:8080/api/admin/health
```

---

### Patient Endpoints (Hospital Service)

#### 1. Create Patient

```http
POST http://localhost:8080/api/patients
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "johnpatient@example.com",
  "phoneNumber": "555-0123",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "address": "123 Main Street, City, State 12345",
  "medicalHistory": "No known allergies"
}
```

**Response (200/201 OK):**
```json
{
  "patientId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "johnpatient@example.com",
  "phoneNumber": "555-0123",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "address": "123 Main Street, City, State 12345",
  "medicalHistory": "No known allergies",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T10:30:00Z"
}
```

---

#### 2. Get All Patients

```http
GET http://localhost:8080/api/patients
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
[
  {
    "patientId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "johnpatient@example.com",
    ...
  },
  {
    "patientId": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "janepatient@example.com",
    ...
  }
]
```

---

#### 3. Get Patient by ID

```http
GET http://localhost:8080/api/patients/1
Authorization: Bearer <JWT_TOKEN>
```

---

#### 4. Update Patient

```http
PUT http://localhost:8080/api/patients/1
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "johnpatient@example.com",
  "phoneNumber": "555-0456",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "address": "456 Oak Avenue, City, State 12345",
  "medicalHistory": "No known allergies, history of hypertension"
}
```

---

#### 5. Delete Patient

```http
DELETE http://localhost:8080/api/patients/1
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content)**

---

### Doctor Endpoints (Hospital Service)

#### 1. Create Doctor

```http
POST http://localhost:8080/api/doctors
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "doctor@hospital.com",
  "phoneNumber": "555-9876",
  "specialization": "Cardiology",
  "licenseNumber": "MD123456",
  "experienceYears": 10,
  "availableTimeSlot": "Monday-Friday 9AM-5PM"
}
```

**Response (200/201 OK):**
```json
{
  "doctorId": 1,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "doctor@hospital.com",
  "phoneNumber": "555-9876",
  "specialization": "Cardiology",
  "licenseNumber": "MD123456",
  "experienceYears": 10,
  "availableTimeSlot": "Monday-Friday 9AM-5PM",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T10:30:00Z"
}
```

---

#### 2. Get All Doctors

```http
GET http://localhost:8080/api/doctors
Authorization: Bearer <JWT_TOKEN>
```

---

#### 3. Get Doctor by ID

```http
GET http://localhost:8080/api/doctors/1
Authorization: Bearer <JWT_TOKEN>
```

---

#### 4. Update Doctor

```http
PUT http://localhost:8080/api/doctors/1
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "doctor@hospital.com",
  "phoneNumber": "555-9876",
  "specialization": "Cardiology",
  "licenseNumber": "MD123456",
  "experienceYears": 11,
  "availableTimeSlot": "Monday-Friday 9AM-5PM, Saturday 10AM-2PM"
}
```

---

#### 5. Delete Doctor

```http
DELETE http://localhost:8080/api/doctors/1
Authorization: Bearer <JWT_TOKEN>
```

---

### Appointment Endpoints (Hospital Service)

#### 1. Create Appointment

```http
POST http://localhost:8080/api/appointments
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "patientId": 1,
  "doctorId": 1,
  "appointmentDate": "2024-04-25",
  "appointmentTime": "10:30",
  "status": "SCHEDULED",
  "reasonForVisit": "Regular cardiac checkup",
  "notes": "Patient has history of hypertension"
}
```

**Response (200/201 OK):**
```json
{
  "appointmentId": 1,
  "patientId": 1,
  "doctorId": 1,
  "appointmentDate": "2024-04-25",
  "appointmentTime": "10:30",
  "status": "SCHEDULED",
  "reasonForVisit": "Regular cardiac checkup",
  "notes": "Patient has history of hypertension",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T10:30:00Z"
}
```

---

#### 2. Get All Appointments

```http
GET http://localhost:8080/api/appointments
Authorization: Bearer <JWT_TOKEN>
```

---

#### 3. Get Appointment by ID

```http
GET http://localhost:8080/api/appointments/1
Authorization: Bearer <JWT_TOKEN>
```

---

#### 4. Update Appointment

```http
PUT http://localhost:8080/api/appointments/1
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "patientId": 1,
  "doctorId": 1,
  "appointmentDate": "2024-04-26",
  "appointmentTime": "14:00",
  "status": "SCHEDULED",
  "reasonForVisit": "Regular cardiac checkup",
  "notes": "Rescheduled due to doctor availability"
}
```

---

#### 5. Cancel Appointment

```http
DELETE http://localhost:8080/api/appointments/1
Authorization: Bearer <JWT_TOKEN>
```

---

### Prescription Endpoints (Hospital Service)

#### 1. Create Prescription

```http
POST http://localhost:8080/api/prescriptions
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "patientId": 1,
  "doctorId": 1,
  "appointmentId": 1,
  "prescriptionDate": "2024-04-20",
  "instructions": "Take with meals. Continue for 7 days.",
  "medications": [
    {
      "medicationId": 1,
      "quantity": 30,
      "frequency": "TWICE_DAILY",
      "durationDays": 7,
      "notes": "With breakfast and dinner"
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "prescriptionId": 1,
  "patientId": 1,
  "doctorId": 1,
  "appointmentId": 1,
  "prescriptionDate": "2024-04-20",
  "instructions": "Take with meals. Continue for 7 days.",
  "medications": [
    {
      "prescriptionMedicationId": 1,
      "medicationId": 1,
      "medicationName": "Aspirin",
      "quantity": 30,
      "frequency": "TWICE_DAILY",
      "durationDays": 7,
      "notes": "With breakfast and dinner"
    }
  ],
  "createdAt": "2024-04-20T10:30:00Z",
  "updatedAt": "2024-04-20T10:30:00Z"
}
```

---

#### 2. Get All Prescriptions

```http
GET http://localhost:8080/api/prescriptions
Authorization: Bearer <JWT_TOKEN>
```

---

#### 3. Get Prescription by ID

```http
GET http://localhost:8080/api/prescriptions/1
Authorization: Bearer <JWT_TOKEN>
```

---

#### 4. Update Prescription

```http
PUT http://localhost:8080/api/prescriptions/1
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "patientId": 1,
  "doctorId": 1,
  "appointmentId": 1,
  "prescriptionDate": "2024-04-20",
  "instructions": "Take with meals. Continue for 10 days.",
  "medications": [
    {
      "medicationId": 1,
      "quantity": 40,
      "frequency": "TWICE_DAILY",
      "durationDays": 10,
      "notes": "Updated dosage with breakfast and dinner"
    }
  ]
}
```

---

#### 5. Delete Prescription

```http
DELETE http://localhost:8080/api/prescriptions/1
Authorization: Bearer <JWT_TOKEN>
```

---

### Medication Endpoints (Hospital Service)

#### 1. Create Medication

```http
POST http://localhost:8080/api/medications
Content-Type: application/json
Authorization: Bearer <ADMIN_JWT_TOKEN>

{
  "medicationName": "Aspirin",
  "description": "Pain reliever and fever reducer",
  "dosage": "500mg",
  "manufacturer": "Pharma Corp",
  "price": 5.99,
  "stockQuantity": 500,
  "expiryDate": "2025-12-31"
}
```

**Response (201 Created):**
```json
{
  "medicationId": 1,
  "medicationName": "Aspirin",
  "description": "Pain reliever and fever reducer",
  "dosage": "500mg",
  "manufacturer": "Pharma Corp",
  "price": 5.99,
  "stockQuantity": 500,
  "expiryDate": "2025-12-31",
  "createdAt": "2024-04-19T10:30:00Z",
  "updatedAt": "2024-04-19T10:30:00Z"
}
```

---

#### 2. Get All Medications

```http
GET http://localhost:8080/api/medications
Authorization: Bearer <JWT_TOKEN>
```

---

#### 3. Get Medication by ID

```http
GET http://localhost:8080/api/medications/1
Authorization: Bearer <JWT_TOKEN>
```

---

#### 4. Update Medication

```http
PUT http://localhost:8080/api/medications/1
Content-Type: application/json
Authorization: Bearer <ADMIN_JWT_TOKEN>

{
  "medicationName": "Aspirin",
  "description": "Pain reliever and fever reducer",
  "dosage": "500mg",
  "manufacturer": "Pharma Corp",
  "price": 6.49,
  "stockQuantity": 450,
  "expiryDate": "2025-12-31"
}
```

---

#### 5. Delete Medication

```http
DELETE http://localhost:8080/api/medications/1
Authorization: Bearer <ADMIN_JWT_TOKEN>
```

---

## Common API Patterns

### Error Handling

All APIs follow consistent error response format:

```json
// 400 Bad Request
{
  "error": "Descriptive error message",
  "timestamp": "2024-04-19T10:30:00Z",
  "status": 400
}

// 401 Unauthorized
{
  "error": "Authentication required. Token not provided or expired.",
  "timestamp": "2024-04-19T10:30:00Z",
  "status": 401
}

// 403 Forbidden
{
  "error": "You do not have permission to access this resource",
  "timestamp": "2024-04-19T10:30:00Z",
  "status": 403
}

// 404 Not Found
{
  "error": "Resource not found",
  "timestamp": "2024-04-19T10:30:00Z",
  "status": 404
}

// 500 Internal Server Error
{
  "error": "Internal server error",
  "timestamp": "2024-04-19T10:30:00Z",
  "status": 500
}
```

### HTTP Status Codes

| Code | Meaning | Use Case |
|------|---------|----------|
| 200 | OK | Successful GET, PUT, DELETE |
| 201 | Created | Successful POST (resource created) |
| 204 | No Content | Successful DELETE (no body) |
| 400 | Bad Request | Invalid input, validation failed |
| 401 | Unauthorized | Missing/invalid JWT token |
| 403 | Forbidden | User lacks permission |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Internal server error |

### Pagination (Future Enhancement)

```http
GET http://localhost:8080/api/patients?page=0&size=10&sort=createdAt,desc
```

### Filtering (Future Enhancement)

```http
GET http://localhost:8080/api/appointments?status=SCHEDULED&doctorId=1
```

---

## JWT Token Usage

### Token Structure

```
Header.Payload.Signature

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJwYXRpZW50QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6IlBBVElFTlQiLCJleHAiOjE3MDIwODY0MDAsImlhdCI6MTcwMjAwMDAwMH0.
HMAC256_SIGNATURE
```

### Using Token in Requests

```bash
# cURL example
curl -X GET http://localhost:8080/api/patients/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# JavaScript/Fetch example
fetch('http://localhost:8080/api/patients/1', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer <JWT_TOKEN>'
  }
})

# Postman
# 1. Go to Authorization tab
# 2. Select "Bearer Token" type
# 3. Paste JWT token in the token field
```

### Token Validation

- Tokens expire after 24 hours (86400000 milliseconds)
- Expired tokens return 401 Unauthorized
- Invalid signatures return 401 Unauthorized
- Missing token returns 401 Unauthorized

---

## Testing the API

### Using cURL

```bash
# 1. Register a patient
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testpatient@example.com",
    "password": "TestPass123!",
    "fullName": "Test Patient",
    "userType": "PATIENT"
  }'

# 2. Login to get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testpatient@example.com",
    "password": "TestPass123!"
  }'

# 3. Save token and use in subsequent calls
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 4. Get user info
curl -X GET http://localhost:8080/api/auth/user/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Using Postman

1. **Create Collection**: "Hospital Management System"
2. **Create Environment Variables**:
   - `baseUrl`: http://localhost:8080
   - `token`: (will be updated after login)

3. **Create Requests**:
   - POST /api/auth/login
   - GET /api/auth/user/{id}
   - POST /api/patients
   - GET /api/patients
   - POST /api/appointments
   - etc.

4. **Pre-request Script** (for login):
```javascript
// Save token after login
pm.environment.set("token", pm.response.json().token);
```

---

## Monitoring & Debugging

### Check Service Status

```bash
# Eureka Dashboard (UI)
http://localhost:8761/eureka/

# Service Health Endpoints
curl http://localhost:8082/api/auth/health      # User Service
curl http://localhost:8083/api/admin/health     # Admin Service
curl http://localhost:8081/health               # Hospital Service (if implemented)
```

### View Logs

```bash
# Logs are output to console by default
# For persistent logging, redirect output:

java -jar user-service-0.0.1-SNAPSHOT.jar > user-service.log 2>&1

# View logs
tail -f user-service.log

# Search logs
grep "ERROR" user-service.log
```

### Database Verification

```bash
# Connect to MySQL
mysql -u root -p hospital_management

# Check tables
SHOW TABLES;

# View users
SELECT * FROM users;

# View patients
SELECT * FROM patients;

# Count appointments
SELECT COUNT(*) FROM appointments;

# Check indexes
SHOW INDEXES FROM patients;

# Exit
EXIT;
```

---

## Production Deployment Checklist

- [ ] Change JWT secret keys (production environment)
- [ ] Update database credentials
- [ ] Enable HTTPS/SSL
- [ ] Set up load balancer
- [ ] Configure distributed logging (ELK Stack)
- [ ] Implement monitoring (Prometheus, Grafana)
- [ ] Set up CI/CD pipeline
- [ ] Enable request rate limiting
- [ ] Implement CORS policy
- [ ] Set up backup strategy for database
- [ ] Configure health checks
- [ ] Implement circuit breakers
- [ ] Set up distributed tracing (Zipkin)
- [ ] Enable security headers
- [ ] Implement API versioning

---

## Troubleshooting

### Port Already in Use

```bash
# Check which process is using the port
netstat -ano | findstr :8080

# Kill the process (Windows)
taskkill /PID <PID> /F

# Or use different ports in application.yaml
server:
  port: 8090
```

### Database Connection Error

```bash
# Check MySQL service
# Windows: Services app or
net start MySQL80

# Verify connection
mysql -u root -p -h localhost
```

### Service Not Registering with Eureka

- Check eureka.client.service-url.defaultZone in application.yaml
- Verify Eureka Server is running
- Check network connectivity
- Review service logs for errors

### JWT Token Expired

- Tokens expire after 24 hours
- User needs to login again to get a new token
- Or increase expiration time in application.yaml

---

## Quick Reference

```
┌─────────────────────────────────────────┐
│   Service Ports & Default URLs          │
├─────────────────────────────────────────┤
│ Eureka Dashboard    │ :8761/eureka/     │
│ API Gateway         │ :8080              │
│ Hospital Service    │ :8081              │
│ User Service        │ :8082              │
│ Admin Service       │ :8083              │
└─────────────────────────────────────────┘

Useful Endpoints:
GET  http://localhost:8761/eureka/apps           (All services)
GET  http://localhost:8761/eureka/apps/user-service    (User service instances)
POST http://localhost:8080/api/auth/login        (Login)
POST http://localhost:8080/api/auth/register     (Register)
GET  http://localhost:8080/api/patients          (All patients)
GET  http://localhost:8080/api/doctors           (All doctors)
GET  http://localhost:8080/api/appointments      (All appointments)
```


