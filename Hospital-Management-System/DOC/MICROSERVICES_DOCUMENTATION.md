# Hospital Management System - Microservices Documentation

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Individual Microservices](#individual-microservices)
4. [Service Interactions](#service-interactions)
5. [Technology Stack](#technology-stack)
6. [Data Flow](#data-flow)
7. [Deployment Architecture](#deployment-architecture)

---

## System Overview

The Hospital Management System is a comprehensive microservices-based application built with Spring Boot and Spring Cloud. It provides hospital staff and patients with tools for managing appointments, patient records, prescriptions, medications, and administrative functions.

**Key Characteristics:**
- **Architecture Pattern**: Microservices Architecture
- **API Gateway**: Spring Cloud Gateway for routing requests
- **Service Discovery**: Netflix Eureka for dynamic service registration and discovery
- **Authentication**: JWT (JSON Web Token) for secure communication
- **Database**: MySQL for persistent storage
- **Framework**: Spring Boot 3.3.5 with Spring Cloud 2023.0.3
- **Java Version**: Java 17

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          Client Applications                                     │
│            (Web Browsers, Mobile Apps, External Systems)                        │
└──────────────────────────────┬──────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Port: 8080)                                      │
│  ┌──────────────────────────────────────────────────────────────────────────┐   │
│  │  Spring Cloud Gateway                                                   │   │
│  │  - Routes requests to microservices                                    │   │
│  │  - Path-based routing rules                                           │   │
│  │  - Load balancing with Eureka                                         │   │
│  │  - Request/Response filtering                                         │   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
└──────────┬─────────────┬──────────────┬──────────────┬──────────────┬────────────┘
           │             │              │              │              │
           ▼             ▼              ▼              ▼              ▼
    ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
    │  USER    │  │  ADMIN   │  │ HOSPITAL │  │ EUREKA   │  │          │
    │ SERVICE  │  │ SERVICE  │  │ SERVICE  │  │ SERVER   │  │ Others   │
    │ (8082)   │  │ (8083)   │  │ (8081)   │  │ (8761)   │  │          │
    └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘
           │             │              │              │              │
           ▼             ▼              ▼              ▼              ▼
    ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
    │  MySQL   │  │  MySQL   │  │  MySQL   │  │   No DB  │  │   No DB  │
    │   DB     │  │   DB     │  │   DB     │  │ (Registry)  │          │
    │          │  │          │  │          │  │          │  │          │
    └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘
```

---

## Individual Microservices

### 1. **Eureka Server** (Service Registry)

#### Purpose
The Eureka Server acts as a central service registry for all microservices. It maintains a real-time inventory of all running service instances and their network locations.

#### Configuration Details
```yaml
Service Name: eureka-server
Port: 8761
Version: 0.0.1-SNAPSHOT
Database: None (In-memory registry)
```

#### Key Features
- **Service Registration**: Microservices auto-register when they start
- **Service Discovery**: Allows services to discover each other dynamically
- **Health Monitoring**: Heartbeat mechanism to detect dead instances
- **Instance Information**: Stores hostname, IP address, port, and metadata

#### Dependencies
- Spring Cloud Netflix Eureka Server
- Spring Boot Starter Test

#### How It Works
```
Startup Process:
1. Eureka Server starts on port 8761
2. Other microservices initialize
3. Services send registration requests to Eureka
4. Services receive service location information
5. Services cache the registry locally
6. Periodic heartbeats maintain the registry
```

#### API Endpoints (Administrative)
- `GET http://localhost:8761/eureka/` - UI Dashboard to view registered services
- `GET http://localhost:8761/eureka/apps` - List all registered applications
- `GET http://localhost:8761/eureka/apps/{app-name}` - Get specific app instances

#### Data Structure
```
Eureka Registry Structure:
{
  "applications": {
    "application": [
      {
        "name": "user-service",
        "instance": [
          {
            "instanceId": "user-service:8082",
            "hostName": "localhost",
            "ipAddr": "127.0.0.1",
            "port": { "$": 8082, "@enabled": true },
            "status": "UP"
          }
        ]
      }
    ]
  }
}
```

---

### 2. **API Gateway**

#### Purpose
The API Gateway serves as the single entry point for all client requests. It routes requests to appropriate microservices based on URL paths and provides centralized cross-cutting concerns.

#### Configuration Details
```yaml
Service Name: api-gateway
Port: 8080
Version: 0.0.1-SNAPSHOT
Database: None
```

#### Key Features
- **Request Routing**: Directs requests to appropriate services
- **Load Balancing**: Distributes load across service instances
- **Path-Based Routing**: Routes based on URL patterns
- **URL Rewriting**: Modifies request paths before forwarding
- **Service Discovery Integration**: Automatically discovers registered services

#### Routing Rules
```yaml
Routes Configuration:
1. Auth Routes       → /api/auth/**       → user-service
2. Admin Routes      → /api/admin/**      → admin-service
3. Appointments      → /api/appointments/**  → hospital-service
4. Doctors           → /api/doctors/**    → hospital-service
5. Patients          → /api/patients/**   → hospital-service
6. Prescriptions     → /api/prescriptions/**  → hospital-service
7. Medications       → /api/medications/** → hospital-service
```

#### Request Flow Diagram
```
Client Request
    │
    ▼
API Gateway (8080)
    │
    ├─ Path: /api/auth/**
    │   └─ → User Service (8082)
    │
    ├─ Path: /api/admin/**
    │   └─ → Admin Service (8083)
    │
    └─ Path: /api/patients/**, /api/doctors/**, /api/appointments/**, etc.
        └─ → Hospital Service (8081)
    
    All requests use Load Balancing (lb://) for failover
```

#### Dependencies
- Spring Cloud Starter Gateway
- Spring Cloud Starter Netflix Eureka Client
- Spring Boot Starter Test

#### Configuration Example
```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
```

---

### 3. **User Service** (Authentication Service)

#### Purpose
The User Service handles user authentication, registration, and user management for both patients and doctors in the hospital system.

#### Configuration Details
```yaml
Service Name: user-service
Port: 8082
Version: 0.0.1-SNAPSHOT
Database: MySQL (hospital_management)
JWT Expiration: 86400000 ms (24 hours)
```

#### Key Features
- **User Registration**: Registers patients and doctors
- **Authentication**: JWT-based login functionality
- **User Retrieval**: Get user details by ID or email
- **Security**: Spring Security with JWT token validation
- **Validation**: Input validation for registration and login
- **Password Encryption**: Secure password handling

#### Database Schema
```sql
users table:
├── id (Long, PK)
├── email (String, UNIQUE)
├── password (String, encrypted)
├── fullName (String)
├── userType (Enum: PATIENT, DOCTOR)
├── createdAt (Timestamp)
└── updatedAt (Timestamp)
```

#### Entities
```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    private String password;        // Encrypted
    private String fullName;
    @Enumerated(EnumType.STRING)
    private UserType userType;      // PATIENT or DOCTOR
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

enum UserType {
    PATIENT,
    DOCTOR
}
```

#### REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login - returns JWT token |
| POST | `/api/auth/register` | Register new user (patient/doctor) |
| GET | `/api/auth/user/{userId}` | Retrieve user by ID |
| GET | `/api/auth/user/email/{email}` | Retrieve user by email |
| GET | `/api/auth/health` | Health check endpoint |

#### Request/Response Examples

**Login Request:**
```json
POST /api/auth/login
{
  "email": "patient@example.com",
  "password": "securePassword123"
}
```

**Login Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "patient@example.com",
  "fullName": "John Doe",
  "userType": "PATIENT",
  "message": "Login successful"
}
```

**Registration Request:**
```json
POST /api/auth/register
{
  "email": "newdoctor@example.com",
  "password": "securePassword123",
  "fullName": "Dr. Jane Smith",
  "userType": "DOCTOR"
}
```

#### Security Implementation
```
Authentication Flow:
1. User provides email and password
2. Service validates credentials against database
3. Generates JWT token containing user info
4. Token includes expiration time
5. Client stores token and sends in Authorization header
6. Service validates token on each request
```

#### Dependencies
- Spring Boot Starter Web
- Spring Cloud Starter Netflix Eureka Client
- Spring Boot Starter Data JPA
- MySQL Connector J
- Spring Boot Starter Validation
- Spring Boot Starter Security
- JJWT (JWT Library) v0.12.3
- Project Lombok
- SpringDoc OpenAPI (Swagger UI)

---

### 4. **Admin Service**

#### Purpose
The Admin Service manages administrative functions including admin user authentication, creation, retrieval, and updates. It also handles authorization for system administrators.

#### Configuration Details
```yaml
Service Name: admin-service
Port: 8083
Version: 0.0.1-SNAPSHOT
Database: MySQL (hospital_management)
JWT Expiration: 86400000 ms (24 hours)
```

#### Key Features
- **Admin Authentication**: Secure admin login with JWT
- **Admin Management**: Create, retrieve, and update admin users
- **Role-Based Access**: Different admin roles and departments
- **Reports**: Generate hospital reports and statistics
- **Billing Management**: Handle billing operations
- **Staff Management**: Manage hospital staff information

#### Database Schema
```sql
admins table:
├── id (Long, PK)
├── email (String, UNIQUE)
├── password (String, encrypted)
├── fullName (String)
├── role (Enum: SUPER_ADMIN, ADMIN, MANAGER)
├── department (Enum: HR, FINANCE, OPERATIONS, IT)
├── createdAt (Timestamp)
└── updatedAt (Timestamp)
```

#### Entities
```java
@Entity
@Table(name = "admins")
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    private String password;           // Encrypted
    private String fullName;
    @Enumerated(EnumType.STRING)
    private AdminRole role;            // SUPER_ADMIN, ADMIN, MANAGER
    @Enumerated(EnumType.STRING)
    private Department department;     // HR, FINANCE, OPERATIONS, IT
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

enum AdminRole {
    SUPER_ADMIN,
    ADMIN,
    MANAGER
}

enum Department {
    HR,
    FINANCE,
    OPERATIONS,
    IT
}
```

#### REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/login` | Admin login - returns JWT token |
| POST | `/api/admin/create` | Create new admin user |
| GET | `/api/admin/{adminId}` | Retrieve admin by ID |
| GET | `/api/admin/email/{email}` | Retrieve admin by email |
| PUT | `/api/admin/{adminId}` | Update admin information |
| GET | `/api/admin/health` | Health check endpoint |

#### Request/Response Examples

**Admin Login Request:**
```json
POST /api/admin/login
{
  "email": "admin@hospital.com",
  "password": "adminPassword123"
}
```

**Admin Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "adminId": 1,
  "email": "admin@hospital.com",
  "fullName": "Admin User",
  "role": "SUPER_ADMIN",
  "department": "OPERATIONS",
  "message": "Admin login successful"
}
```

**Admin Creation Request:**
```json
POST /api/admin/create
{
  "email": "finance@hospital.com",
  "password": "securePassword123",
  "fullName": "Finance Manager",
  "role": "MANAGER",
  "department": "FINANCE"
}
```

#### Security Model
```
Admin Hierarchy:
SUPER_ADMIN
├── Full system access
├── Can manage all admins
├── Can access all reports
└── Can modify system settings

ADMIN
├── Department-level access
├── Can manage users in department
├── Can view reports
└── Limited system settings

MANAGER
├── Team-level access
├── Can manage team members
├── Can view team reports
└── No system settings
```

#### Dependencies
- Spring Boot Starter Web
- Spring Cloud Starter Netflix Eureka Client
- Spring Boot Starter Data JPA
- MySQL Connector J
- Spring Boot Starter Validation
- Spring Boot Starter Security
- JJWT (JWT Library) v0.12.3
- Project Lombok
- SpringDoc OpenAPI (Swagger UI)

---

### 5. **Hospital Service (System)** - Core Service

#### Purpose
The Hospital Service is the core microservice that handles all hospital operations including patient management, doctor management, appointments, prescriptions, and medications.

#### Configuration Details
```yaml
Service Name: hospital-service
Port: 8081
Version: 0.0.1-SNAPSHOT
Database: MySQL (hospital_management)
```

#### Key Features
- **Patient Management**: Register and manage patient records
- **Doctor Management**: Maintain doctor information and specialties
- **Appointments**: Schedule and manage medical appointments
- **Prescriptions**: Create and manage medical prescriptions
- **Medications**: Maintain medication inventory
- **Medical Records**: Store and retrieve patient medical history

#### Database Schema

**Patients Table:**
```sql
patients:
├── patient_id (Long, PK)
├── first_name (String)
├── last_name (String)
├── email (String, UNIQUE)
├── phone_number (String)
├── date_of_birth (Date)
├── gender (String)
├── address (String)
├── medical_history (Text)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**Doctors Table:**
```sql
doctors:
├── doctor_id (Long, PK)
├── first_name (String)
├── last_name (String)
├── email (String, UNIQUE)
├── phone_number (String)
├── specialization (String)
├── license_number (String)
├── experience_years (Integer)
├── available_time_slot (String)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**Appointments Table:**
```sql
appointments:
├── appointment_id (Long, PK)
├── patient_id (Long, FK)
├── doctor_id (Long, FK)
├── appointment_date (Date)
├── appointment_time (Time)
├── status (String: SCHEDULED, COMPLETED, CANCELLED)
├── reason_for_visit (String)
├── notes (Text)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**Prescriptions Table:**
```sql
prescriptions:
├── prescription_id (Long, PK)
├── patient_id (Long, FK)
├── doctor_id (Long, FK)
├── appointment_id (Long, FK)
├── prescription_date (Date)
├── instructions (Text)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**Medications Table:**
```sql
medications:
├── medication_id (Long, PK)
├── medication_name (String, UNIQUE)
├── description (String)
├── dosage (String)
├── manufacturer (String)
├── price (Decimal)
├── stock_quantity (Integer)
├── expiry_date (Date)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**Prescription Medications Table (Many-to-Many):**
```sql
prescription_medications:
├── prescription_medication_id (Long, PK)
├── prescription_id (Long, FK)
├── medication_id (Long, FK)
├── quantity (Integer)
├── frequency (String: ONCE_DAILY, TWICE_DAILY, THREE_TIMES_DAILY, etc.)
├── duration_days (Integer)
├── notes (String)
└── created_at (Timestamp)
```

#### Entities and Relationships

**Relationship Diagram:**
```
Doctor (1) ──────────┐
                     │
                     ├─→ (M) Appointment (M) ←─── (1) Patient
                     │
                     └─→ (M) Prescription
                              │
                              ├─→ (M) Patient
                              │
                              └─→ (M) Medication
                                     (through PrescriptionMedication)
```

#### REST API Endpoints

**Patient Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/patients` | Create new patient |
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/{id}` | Get patient by ID |
| PUT | `/api/patients/{id}` | Update patient info |
| DELETE | `/api/patients/{id}` | Delete patient |

**Doctor Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/doctors` | Add new doctor |
| GET | `/api/doctors` | Get all doctors |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| PUT | `/api/doctors/{id}` | Update doctor info |
| DELETE | `/api/doctors/{id}` | Delete doctor |

**Appointment Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Create appointment |
| GET | `/api/appointments` | Get all appointments |
| GET | `/api/appointments/{id}` | Get appointment by ID |
| PUT | `/api/appointments/{id}` | Update appointment |
| DELETE | `/api/appointments/{id}` | Cancel appointment |

**Prescription Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/prescriptions` | Create prescription |
| GET | `/api/prescriptions` | Get all prescriptions |
| GET | `/api/prescriptions/{id}` | Get prescription by ID |
| PUT | `/api/prescriptions/{id}` | Update prescription |
| DELETE | `/api/prescriptions/{id}` | Delete prescription |

**Medication Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/medications` | Add medication |
| GET | `/api/medications` | Get all medications |
| GET | `/api/medications/{id}` | Get medication by ID |
| PUT | `/api/medications/{id}` | Update medication |
| DELETE | `/api/medications/{id}` | Delete medication |

#### Request/Response Examples

**Create Patient:**
```json
POST /api/patients
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phoneNumber": "555-0123",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "address": "123 Main St, City, State"
}
```

**Create Appointment:**
```json
POST /api/appointments
{
  "patientId": 1,
  "doctorId": 1,
  "appointmentDate": "2024-04-20",
  "appointmentTime": "10:30",
  "reasonForVisit": "Regular checkup",
  "status": "SCHEDULED"
}
```

**Create Prescription:**
```json
POST /api/prescriptions
{
  "patientId": 1,
  "doctorId": 1,
  "appointmentId": 1,
  "instructions": "Take with meals",
  "medications": [
    {
      "medicationId": 1,
      "quantity": 30,
      "frequency": "TWICE_DAILY",
      "durationDays": 7
    }
  ]
}
```

#### Service Workflow

**Appointment Workflow:**
```
1. Patient books appointment
   │
   ├─ Validation: Check patient & doctor exist
   │
   ├─ Check availability
   │
   ├─ Create appointment record
   │
   └─ Confirm with patient
```

**Prescription Workflow:**
```
1. Doctor completes appointment
   │
   ├─ Review patient medical history
   │
   ├─ Select medications
   │
   ├─ Create prescription
   │
   ├─ Add medications to prescription
   │
   └─ Send to pharmacy
```

#### Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- MySQL Connector J
- Spring Boot Starter Validation
- Spring Cloud Starter Netflix Eureka Client
- Project Lombok
- SpringDoc OpenAPI (Swagger UI)

---

## Service Interactions

### Request Flow Diagram

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌──────────────────────────┐
│   API Gateway (8080)     │
│   - Route matching       │
│   - Load balancing       │
└───┬──────┬──────┬────────┘
    │      │      │
    ▼      ▼      ▼
┌────────────┐ ┌────────────┐ ┌──────────────┐
│  User Svc  │ │ Admin Svc  │ │ Hospital Svc │
│  (8082)    │ │  (8083)    │ │    (8081)    │
└────┬───────┘ └────┬───────┘ └───────┬──────┘
     │              │                 │
     ▼              ▼                 ▼
  MySQL DB      MySQL DB          MySQL DB
  Users         Admins            Patients
               Hospital Data       Doctors
                                  Appointments
                                  Prescriptions
```

### Service-to-Service Communication

```
Service Discovery Process:
1. Each service registers with Eureka on startup
   └─ Sends: {service-name, host, port, health-check-url}

2. API Gateway queries Eureka for service locations
   └─ Gets: List of available service instances

3. Gateway routes requests using service name (lb://service-name)
   └─ Eureka client-side load balancer selects instance

4. Request forwarded to service
   └─ Service processes and returns response
```

### Inter-Service Communication

While services are primarily accessed through the API Gateway, they can communicate directly if needed:

```
Example: Admin Service needs user information
┌──────────────────┐
│  Admin Service   │
└────────┬─────────┘
         │
         │ HTTP Request (Discovery via Eureka)
         │
         ▼
    ┌────────────┐
    │   Eureka   │
    │  Lookup:   │
    │user-service│
    └────┬───────┘
         │
         │ Returns: user-service@8082
         │
         ▼
    ┌────────────┐
    │ User Svc   │
    │ (8082)     │
    └────────────┘
```

---

## Technology Stack

### Backend Framework & Cloud
| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.3.5 | Application framework |
| Spring Cloud | 2023.0.3 | Microservices framework |
| Java | 17 | Programming language |

### Service Discovery & API Gateway
| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Cloud Netflix Eureka | 2023.0.3 | Service registry and discovery |
| Spring Cloud Gateway | 2023.0.3 | API Gateway and request routing |

### Security & Authentication
| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Security | 3.x | Application security |
| JJWT | 0.12.3 | JWT token generation and validation |

### Data Access & Persistence
| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Data JPA | 3.x | ORM and database access |
| Hibernate | 6.x | JPA implementation |
| MySQL Connector/J | Latest | MySQL database driver |

### Documentation & API
| Component | Version | Purpose |
|-----------|---------|---------|
| SpringDoc OpenAPI | 2.3.0 | API documentation (Swagger UI) |

### Development Tools
| Component | Version | Purpose |
|-----------|---------|---------|
| Project Lombok | Latest | Reduce boilerplate code |
| Maven | Latest | Build and dependency management |

### Database
| Component | Version | Purpose |
|-----------|---------|---------|
| MySQL | 5.7+ or 8.0 | Relational database |

---

## Data Flow

### User Authentication Flow

```
┌──────────┐
│  Client  │
└────┬─────┘
     │
     │ 1. POST /api/auth/login
     │    {email, password}
     │
     ▼
┌──────────────────────────────────┐
│   API Gateway (8080)             │
│   Routes: /api/auth/** → User Svc│
└────┬─────────────────────────────┘
     │
     │ 2. Forwards to User Service
     │
     ▼
┌──────────────────────────────────┐
│   User Service (8082)            │
│   1. Look up user by email       │
│   2. Verify password             │
│   3. Generate JWT token          │
└────┬─────────────────────────────┘
     │
     │ 3. Query MySQL Database
     │
     ▼
┌──────────────────────────────────┐
│   MySQL Database                 │
│   1. Find user record            │
│   2. Return encrypted password   │
└────┬─────────────────────────────┘
     │
     │ 4. Response from User Service
     │
     ▼
┌──────────────────────────────────┐
│   API Gateway                    │
│   Returns response to client     │
└────┬─────────────────────────────┘
     │
     │ 5. LoginResponse
     │    {token, userId, email, ...}
     │
     ▼
┌──────────┐
│  Client  │ ← Stores JWT token for future requests
└──────────┘
```

### Appointment Creation Flow

```
┌──────────────────────────────────┐
│   Client Request                 │
│   POST /api/appointments         │
│   {patientId, doctorId, date...} │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│   API Gateway (8080)             │
│   Routes: /api/appointments/**   │
│   → hospital-service             │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│   Hospital Service (8081)        │
│                                  │
│   AppointmentController:         │
│   1. Validate input              │
│   2. Check patient exists        │
│   3. Check doctor exists         │
│   4. Check availability          │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│   AppointmentService             │
│   1. Create Appointment entity   │
│   2. Save to database            │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│   MySQL Database                 │
│   INSERT into appointments table │
└────┬─────────────────────────────┘
     │
     │ Returns: Generated appointmentId
     │
     ▼
┌──────────────────────────────────┐
│   Response to Client             │
│   {appointmentId, status: 201}   │
└──────────────────────────────────┘
```

---

## Deployment Architecture

### Development Environment Setup

```
┌─────────────────────────────────────────────────────────┐
│          Deployment Architecture                         │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │   Client Layer (Port: 3000/8000 - Optional Frontend)│ │
│  │   ├─ Web Browser                                   │ │
│  │   ├─ Mobile App                                    │ │
│  │   └─ External Systems                              │ │
│  └────────────────────────────────────────────────────┘ │
│                           │                              │
│                           ▼                              │
│  ┌────────────────────────────────────────────────────┐ │
│  │   Gateway Layer (Port: 8080)                       │ │
│  │   ├─ API Gateway                                   │ │
│  │   ├─ Eureka Client                                 │ │
│  │   └─ Service Discovery                             │ │
│  └────────────────────────────────────────────────────┘ │
│         ▲                    │                    ▲      │
│         └────────────────────┼────────────────────┘      │
│              Registry Lookup & Load Balancing            │
│                           │                              │
│                           ▼                              │
│  ┌────────────────────────────────────────────────────┐ │
│  │   Service Discovery Layer (Port: 8761)             │ │
│  │   ├─ Eureka Server                                 │ │
│  │   ├─ Instance Registry                             │ │
│  │   └─ Health Monitoring                             │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  ┌──────────────────┬──────────────────┬─────────────┐  │
│  │                  │                  │             │  │
│  ▼                  ▼                  ▼             ▼  │
│ ┌──────────┐    ┌──────────┐    ┌──────────────┐   ┌──┐
│ │ User Svc │    │Admin Svc │    │ Hospital Svc │   │  │
│ │ (8082)   │    │ (8083)   │    │    (8081)    │   │..│
│ └──────────┘    └──────────┘    └──────────────┘   └──┘
│      │               │                  │
│      ▼               ▼                  ▼
│  ┌─────────────────────────────────────────────────┐  │
│  │          MySQL Database                         │  │
│  │     (Single Database Instance)                  │  │
│  │  ├─ users (User Service)                        │  │
│  │  ├─ admins (Admin Service)                      │  │
│  │  ├─ patients (Hospital Service)                 │  │
│  │  ├─ doctors (Hospital Service)                  │  │
│  │  ├─ appointments (Hospital Service)             │  │
│  │  ├─ prescriptions (Hospital Service)            │  │
│  │  ├─ medications (Hospital Service)              │  │
│  │  └─ prescription_medications (Hospital Svc)     │  │
│  └─────────────────────────────────────────────────┘  │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Database Connection Details

**All Services Connect To:**
```yaml
Database URL: jdbc:mysql://localhost:3306/hospital_management
Username: root
Password: root
Connection Pool: HikariCP (Default)
Driver: com.mysql.cj.jdbc.Driver
Timezone: UTC
SSL Mode: Disabled (Development only)
```

### Startup Sequence

```
Recommended Startup Order:

1. MySQL Database
   └─ Ensure database exists: hospital_management
      Verify tables are created by Hibernate (ddl-auto: update)

2. Eureka Server (Port: 8761)
   └─ java -jar eureka-server-0.0.1-SNAPSHOT.jar
   └─ Wait for: "Eureka Server started successfully"

3. Individual Microservices (Parallel)
   ├─ User Service (Port: 8082)
   │  └─ java -jar user-service-0.0.1-SNAPSHOT.jar
   │
   ├─ Admin Service (Port: 8083)
   │  └─ java -jar admin-service-0.0.1-SNAPSHOT.jar
   │
   ├─ Hospital Service (Port: 8081)
   │  └─ java -jar hospital-service-0.0.1-SNAPSHOT.jar
   │
   └─ Wait for: "Registered with Eureka"

4. API Gateway (Port: 8080)
   └─ java -jar api-gateway-0.0.1-SNAPSHOT.jar
   └─ Wait for: "Gateway started successfully"

5. Verify All Services
   └─ Visit: http://localhost:8761/eureka/
      Verify all 4 instances are registered (UP status)
```

### Health Check Endpoints

```
Service Health Checks:

Eureka Server Dashboard:
  GET http://localhost:8761/eureka/apps
  ├─ Shows all registered services
  ├─ Displays instance status (UP/DOWN)
  └─ Lists available instances

Individual Service Health:
  
  User Service:
    GET http://localhost:8082/api/auth/health
    Response: {"status":"UP","service":"user-service"}
  
  Admin Service:
    GET http://localhost:8083/api/admin/health
    Response: {"status":"UP","service":"admin-service"}
  
  Hospital Service:
    GET http://localhost:8081/api/health (if implemented)
    Response: {"status":"UP","service":"hospital-service"}
```

---

## API Testing Examples

### Using cURL

**1. Login User:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@example.com",
    "password": "password123"
  }'
```

**2. Register New Patient:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newpatient@example.com",
    "password": "password123",
    "fullName": "John Patient",
    "userType": "PATIENT"
  }'
```

**3. Create Appointment:**
```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT-TOKEN>" \
  -d '{
    "patientId": 1,
    "doctorId": 1,
    "appointmentDate": "2024-04-20",
    "appointmentTime": "10:30",
    "reasonForVisit": "Checkup",
    "status": "SCHEDULED"
  }'
```

**4. Get All Doctors:**
```bash
curl -X GET http://localhost:8080/api/doctors \
  -H "Authorization: Bearer <JWT-TOKEN>"
```

**5. Create Medication:**
```bash
curl -X POST http://localhost:8080/api/medications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT-TOKEN>" \
  -d '{
    "medicationName": "Aspirin",
    "description": "Pain reliever",
    "dosage": "500mg",
    "manufacturer": "Pharma Corp",
    "price": 5.99,
    "stockQuantity": 100,
    "expiryDate": "2025-12-31"
  }'
```

---

## Configuration Summary

### Application Properties

**User Service (application.yaml):**
```yaml
server.port: 8082
spring.application.name: user-service
spring.jpa.hibernate.ddl-auto: update
eureka.client.service-url.defaultZone: http://localhost:8761/eureka/
spring.security.jwt.secret: hospital-management-user-service-jwt-secret-key-please-change-in-production-environment-2024
spring.security.jwt.expiration: 86400000
```

**Admin Service (application.yaml):**
```yaml
server.port: 8083
spring.application.name: admin-service
spring.jpa.hibernate.ddl-auto: update
eureka.client.service-url.defaultZone: http://localhost:8761/eureka/
spring.security.jwt.secret: hospital-management-admin-service-jwt-secret-key-please-change-in-production-environment-2024
spring.security.jwt.expiration: 86400000
```

**Hospital Service (application.yaml):**
```yaml
server.port: 8081
spring.application.name: hospital-service
spring.jpa.hibernate.ddl-auto: update
eureka.client.service-url.defaultZone: http://localhost:8761/eureka/
```

**API Gateway (application.yaml):**
```yaml
server.port: 8080
spring.application.name: api-gateway
spring.cloud.gateway.discovery.locator.enabled: true
eureka.client.service-url.defaultZone: http://localhost:8761/eureka/
```

**Eureka Server (application.yaml):**
```yaml
server.port: 8761
spring.application.name: eureka-server
eureka.client.register-with-eureka: false
eureka.client.fetch-registry: false
```

---

## Summary

This Hospital Management System is a well-architected microservices application built on Spring Cloud technologies. Here's what each service does:

| Service | Purpose | Port | Database |
|---------|---------|------|----------|
| **Eureka Server** | Service Registry & Discovery | 8761 | In-Memory |
| **API Gateway** | Request Routing & Load Balancing | 8080 | None |
| **User Service** | Authentication & User Management | 8082 | MySQL |
| **Admin Service** | Admin Management & Authorization | 8083 | MySQL |
| **Hospital Service** | Core Hospital Operations | 8081 | MySQL |

The system demonstrates professional microservices patterns including:
- Service discovery with Eureka
- API Gateway for unified access
- JWT-based authentication
- Database per service principle (single DB shared for simplicity, can be separated)
- RESTful API design
- Spring Cloud integration
- Eureka-based load balancing

All services are discoverable, can communicate with each other, and provide OpenAPI documentation via Swagger UI.

