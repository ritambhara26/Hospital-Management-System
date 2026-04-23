# Hospital Management System - Architecture & Detailed Diagrams

## Table of Contents
1. [System Architecture Overview](#system-architecture-overview)
2. [Detailed Service Architecture](#detailed-service-architecture)
3. [Data Flow Diagrams](#data-flow-diagrams)
4. [Sequence Diagrams](#sequence-diagrams)
5. [Database Schema](#database-schema)
6. [Component Interactions](#component-interactions)

---

## System Architecture Overview

### High-Level Architecture

```
                              ┌─────────────────────┐
                              │  External Clients   │
                              │ (Web, Mobile, APIs) │
                              └──────────┬──────────┘
                                         │
                    ┌────────────────────┴────────────────────┐
                    │                                         │
                    ▼                                         │
        ┌──────────────────────────┐                 ┌───────────────┐
        │    API Gateway           │                 │  Eureka UI    │
        │    (Port: 8080)          │                 │ (Port: 8761)  │
        │  - Request Routing       │                 │   Dashboard   │
        │  - Path Matching         │                 └───────────────┘
        │  - Load Balancing        │                         ▲
        │  - URL Rewriting         │                         │
        └──────────────┬───────────┘                         │
                       │                                      │
        ┌──────────────┴──────────────────┬──────────────────┼──────────┐
        │                                 │                  │          │
        ▼                                 ▼                  ▼          ▼
    ┌──────────────┐         ┌──────────────┐      ┌──────────────┐
    │ User Service │         │ Admin Service│      │ Hospital Srv │
    │ (Port: 8082) │         │ (Port: 8083) │      │ (Port: 8081) │
    │              │         │              │      │              │
    │ - Login      │         │ - Admin Mgmt │      │ - Patients   │
    │ - Register   │         │ - Reports    │      │ - Doctors    │
    │ - JWT Auth   │         │ - Billing    │      │ - Appts      │
    └──────┬───────┘         └──────┬───────┘      │ - RX         │
           │                        │               │ - Meds       │
           └────────────┬───────────┴───────────────┤              │
                        │                           └──────┬───────┘
                        │   All Register with               │
                        │   Eureka on Startup              │
                        │                                   │
                        ▼                                   ▼
                    ┌──────────────────────────────────────────┐
                    │  Eureka Server (Port: 8761)              │
                    │  - Service Registry                      │
                    │  - Instance Management                   │
                    │  - Health Monitoring                     │
                    └──────────────────────────────────────────┘
                        
                        ▼
                    
                    ┌──────────────────────────────────────────┐
                    │  MySQL Database: hospital_management    │
                    │                                          │
                    │  ├─ users table (User Service)           │
                    │  ├─ admins table (Admin Service)         │
                    │  ├─ patients table (Hospital Service)    │
                    │  ├─ doctors table (Hospital Service)     │
                    │  ├─ appointments table                   │
                    │  ├─ prescriptions table                  │
                    │  ├─ medications table                    │
                    │  └─ prescription_medications table       │
                    └──────────────────────────────────────────┘
```

---

## Detailed Service Architecture

### 1. User Service Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER SERVICE (8082)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              REST Controllers                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  AuthController (/api/auth)                         │ │  │
│  │  │  - POST /login            → LoginResponse           │ │  │
│  │  │  - POST /register         → RegisterResponse        │ │  │
│  │  │  - GET /user/{id}         → User                    │ │  │
│  │  │  - GET /user/email/{email}→ User                    │ │  │
│  │  │  - GET /health            → HealthStatus           │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                      │
│                           ▼                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           Business Logic Layer (Services)               │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  UserService                                        │ │  │
│  │  │  - login(loginRequest): LoginResponse              │ │  │
│  │  │  - register(regRequest): User                      │ │  │
│  │  │  - getUserById(id): User                           │ │  │
│  │  │  - getUserByEmail(email): User                     │ │  │
│  │  │  - encodePassword(password): String                │ │  │
│  │  │  - validateEmail(email): boolean                   │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                          │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  JwtTokenProvider                                   │ │  │
│  │  │  - generateToken(user): String                     │ │  │
│  │  │  - validateToken(token): boolean                   │ │  │
│  │  │  - getEmailFromToken(token): String                │ │  │
│  │  │  - getExpirationFromToken(token): Long             │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                          │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  SecurityConfig                                    │ │  │
│  │  │  - Password encoder configuration                  │ │  │
│  │  │  - Security filter chain                           │ │  │
│  │  │  - CORS configuration                              │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                      │
│                           ▼                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │        Data Access Layer (Repositories)                 │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  UserRepository extends JpaRepository               │ │  │
│  │  │  - findByEmail(email): Optional<User>              │ │  │
│  │  │  - findById(id): Optional<User>                    │ │  │
│  │  │  - save(user): User                                │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                      │
│                           ▼                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Entity Models                              │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  @Entity User                                       │ │  │
│  │  │  - id: Long                                         │ │  │
│  │  │  - email: String                                    │ │  │
│  │  │  - password: String (encrypted)                     │ │  │
│  │  │  - fullName: String                                 │ │  │
│  │  │  - userType: UserType (PATIENT, DOCTOR)            │ │  │
│  │  │  - createdAt: LocalDateTime                         │ │  │
│  │  │  - updatedAt: LocalDateTime                         │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           DTOs (Data Transfer Objects)                  │  │
│  │  - LoginRequest                                         │  │
│  │  - LoginResponse                                        │  │
│  │  - UserRegistrationRequest                             │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
         │
         │ Database Connection (JDBC)
         │
         ▼
    ┌──────────────┐
    │  MySQL DB    │
    │  (localhost) │
    │              │
    │  users table │
    └──────────────┘
```

### 2. Admin Service Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         ADMIN SERVICE (8083)                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              REST Controllers                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  AdminController (/api/admin)                       │ │  │
│  │  │  - POST /login            → AdminLoginResponse      │ │  │
│  │  │  - POST /create           → Admin                   │ │  │
│  │  │  - GET /{id}              → Admin                   │ │  │
│  │  │  - GET /email/{email}     → Admin                   │ │  │
│  │  │  - PUT /{id}              → Admin (Updated)         │ │  │
│  │  │  - GET /health            → HealthStatus           │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                      │
│                           ▼                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           Business Logic Layer (Services)               │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  AdminService                                       │ │  │
│  │  │  - login(request): AdminLoginResponse              │ │  │
│  │  │  - createAdmin(request): Admin                     │ │  │
│  │  │  - getAdminById(id): Admin                         │ │  │
│  │  │  - getAdminByEmail(email): Admin                   │ │  │
│  │  │  - updateAdmin(id, request): Admin                 │ │  │
│  │  │  - generateReports(): List<Report>                 │ │  │
│  │  │  - manageBilling(): void                           │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                          │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  JwtTokenProvider                                   │ │  │
│  │  │  - generateToken(admin): String                    │ │  │
│  │  │  - validateToken(token): boolean                   │ │  │
│  │  │  - getEmailFromToken(token): String                │ │  │
│  │  │  - getExpirationFromToken(token): Long             │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                      │
│                           ▼                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │        Data Access Layer (Repositories)                 │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  AdminRepository extends JpaRepository              │ │  │
│  │  │  - findByEmail(email): Optional<Admin>             │ │  │
│  │  │  - findById(id): Optional<Admin>                   │ │  │
│  │  │  - save(admin): Admin                              │ │  │
│  │  │  - findByRole(role): List<Admin>                   │ │  │
│  │  │  - findByDepartment(dept): List<Admin>             │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                      │
│                           ▼                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Entity Models                              │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  @Entity Admin                                      │ │  │
│  │  │  - id: Long                                         │ │  │
│  │  │  - email: String                                    │ │  │
│  │  │  - password: String (encrypted)                     │ │  │
│  │  │  - fullName: String                                 │ │  │
│  │  │  - role: AdminRole (SUPER_ADMIN, ADMIN, MANAGER)   │ │  │
│  │  │  - department: Department (HR, FINANCE, OPS, IT)   │ │  │
│  │  │  - createdAt: LocalDateTime                         │ │  │
│  │  │  - updatedAt: LocalDateTime                         │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Enumerations                               │  │
│  │  - AdminRole: {SUPER_ADMIN, ADMIN, MANAGER}            │  │
│  │  - Department: {HR, FINANCE, OPERATIONS, IT}           │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
         │
         │ Database Connection
         │
         ▼
    ┌──────────────┐
    │  MySQL DB    │
    │              │
    │  admins table│
    └──────────────┘
```

### 3. Hospital Service (Core Service) Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                    HOSPITAL SERVICE (8081) - CORE SERVICE                 │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                    REST Controllers                               │ │
│  │  ┌─────────────────────────────────────────────────────────────┐ │ │
│  │  │  PatientController (/api/patients)                          │ │ │
│  │  │  - POST /             → Patient (Create)                    │ │ │
│  │  │  - GET /              → List<Patient> (All)                 │ │ │
│  │  │  - GET /{id}          → Patient                             │ │ │
│  │  │  - PUT /{id}          → Patient (Updated)                   │ │ │
│  │  │  - DELETE /{id}       → Void                                │ │ │
│  │  └─────────────────────────────────────────────────────────────┘ │ │
│  │  ┌─────────────────────────────────────────────────────────────┐ │ │
│  │  │  DoctorController (/api/doctors)                            │ │ │
│  │  │  - POST /             → Doctor                              │ │ │
│  │  │  - GET /              → List<Doctor>                        │ │ │
│  │  │  - GET /{id}          → Doctor                              │ │ │
│  │  │  - PUT /{id}          → Doctor                              │ │ │
│  │  │  - DELETE /{id}       → Void                                │ │ │
│  │  └─────────────────────────────────────────────────────────────┘ │ │
│  │  ┌─────────────────────────────────────────────────────────────┐ │ │
│  │  │  AppointmentController (/api/appointments)                  │ │ │
│  │  │  - POST /             → Appointment                         │ │ │
│  │  │  - GET /              → List<Appointment>                   │ │ │
│  │  │  - GET /{id}          → Appointment                         │ │ │
│  │  │  - PUT /{id}          → Appointment                         │ │ │
│  │  │  - DELETE /{id}       → Void (Cancel)                       │ │ │
│  │  └─────────────────────────────────────────────────────────────┘ │ │
│  │  ┌─────────────────────────────────────────────────────────────┐ │ │
│  │  │  PrescriptionController (/api/prescriptions)                │ │ │
│  │  │  - POST /             → Prescription                        │ │ │
│  │  │  - GET /              → List<Prescription>                  │ │ │
│  │  │  - GET /{id}          → Prescription                        │ │ │
│  │  │  - PUT /{id}          → Prescription                        │ │ │
│  │  │  - DELETE /{id}       → Void                                │ │ │
│  │  └─────────────────────────────────────────────────────────────┘ │ │
│  │  ┌─────────────────────────────────────────────────────────────┐ │ │
│  │  │  MedicationController (/api/medications)                    │ │ │
│  │  │  - POST /             → Medication                          │ │ │
│  │  │  - GET /              → List<Medication>                    │ │ │
│  │  │  - GET /{id}          → Medication                          │ │ │
│  │  │  - PUT /{id}          → Medication                          │ │ │
│  │  │  - DELETE /{id}       → Void                                │ │ │
│  │  └─────────────────────────────────────────────────────────────┘ │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │              Business Logic Layer (Services)                       │ │
│  │                                                                    │ │
│  │  ┌────────────────┐  ┌────────────────┐  ┌───────────────────┐  │ │
│  │  │ PatientService │  │ DoctorService  │  │AppointmentService │  │ │
│  │  │                │  │                │  │                   │  │ │
│  │  │ - save(patient)│  │ - save(doctor) │  │ - save(apt)       │  │ │
│  │  │ - getAll()     │  │ - getAll()     │  │ - getAll()        │  │ │
│  │  │ - getById(id)  │  │ - getById(id)  │  │ - getById(id)     │  │ │
│  │  │ - update(pt)   │  │ - update(doc)  │  │ - update(apt)     │  │ │
│  │  │ - delete(id)   │  │ - delete(id)   │  │ - delete(id)      │  │ │
│  │  └────────────────┘  └────────────────┘  └───────────────────┘  │ │
│  │                                                                    │ │
│  │  ┌──────────────────────┐  ┌──────────────────────────────────┐  │ │
│  │  │PrescriptionService   │  │ MedicationService                │  │ │
│  │  │                      │  │                                  │  │ │
│  │  │ - save(prescription) │  │ - save(medication)              │  │ │
│  │  │ - getAll()           │  │ - getAll()                      │  │ │
│  │  │ - getById(id)        │  │ - getById(id)                   │  │ │
│  │  │ - update(rx)         │  │ - update(med)                   │  │ │
│  │  │ - delete(id)         │  │ - delete(id)                    │  │ │
│  │  │ - addMedications()   │  │ - checkAvailability()           │  │ │
│  │  │ - generatePDF()      │  │ - updateStock()                 │  │ │
│  │  └──────────────────────┘  └──────────────────────────────────┘  │ │
│  │                                                                    │ │
│  │  ┌───────────────────────────────────────────────────────────┐   │ │
│  │  │ Exception Handlers & Custom Exceptions                   │   │ │
│  │  │ - GlobalExceptionHandler                                 │   │ │
│  │  │ - ResourceNotFoundException                              │   │ │
│  │  │ - ValidationException                                    │   │ │
│  │  └───────────────────────────────────────────────────────────┘   │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │         Data Access Layer (Repositories - JPA)                    │ │
│  │                                                                    │ │
│  │  ┌──────────────────────────────────────────────────────────┐    │ │
│  │  │ PatientRepository extends JpaRepository<Patient, Long>   │    │ │
│  │  │ - findByEmail(email): Optional<Patient>                 │    │ │
│  │  │ - findByPhoneNumber(phone): Optional<Patient>           │    │ │
│  │  │ - findAll(): List<Patient>                              │    │ │
│  │  └──────────────────────────────────────────────────────────┘    │ │
│  │                                                                    │ │
│  │  ┌──────────────────────────────────────────────────────────┐    │ │
│  │  │ DoctorRepository extends JpaRepository<Doctor, Long>     │    │ │
│  │  │ - findBySpecialization(spec): List<Doctor>              │    │ │
│  │  │ - findByEmail(email): Optional<Doctor>                  │    │ │
│  │  │ - findAll(): List<Doctor>                               │    │ │
│  │  └──────────────────────────────────────────────────────────┘    │ │
│  │                                                                    │ │
│  │  ┌──────────────────────────────────────────────────────────┐    │ │
│  │  │ AppointmentRepository extends JpaRepository              │    │ │
│  │  │ - findByPatientId(id): List<Appointment>                │    │ │
│  │  │ - findByDoctorId(id): List<Appointment>                 │    │ │
│  │  │ - findByStatus(status): List<Appointment>               │    │ │
│  │  └──────────────────────────────────────────────────────────┘    │ │
│  │                                                                    │ │
│  │  ┌──────────────────────────────────────────────────────────┐    │ │
│  │  │ PrescriptionRepository extends JpaRepository             │    │ │
│  │  │ - findByPatientId(id): List<Prescription>               │    │ │
│  │  │ - findByDoctorId(id): List<Prescription>                │    │ │
│  │  │ - findByAppointmentId(id): Prescription                 │    │ │
│  │  └──────────────────────────────────────────────────────────┘    │ │
│  │                                                                    │ │
│  │  ┌──────────────────────────────────────────────────────────┐    │ │
│  │  │ MedicationRepository extends JpaRepository               │    │ │
│  │  │ - findByName(name): Optional<Medication>                │    │ │
│  │  │ - findExpiredMedications(): List<Medication>            │    │ │
│  │  │ - findLowStockItems(): List<Medication>                 │    │ │
│  │  └──────────────────────────────────────────────────────────┘    │ │
│  │                                                                    │ │
│  │  ┌──────────────────────────────────────────────────────────┐    │ │
│  │  │ PrescriptionMedicationRepository                         │    │ │
│  │  │ - findByPrescriptionId(id): List<PrescriptionMed>       │    │ │
│  │  └──────────────────────────────────────────────────────────┘    │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                       Entity Models (JPA)                          │ │
│  │                                                                    │ │
│  │  @Entity Patient           @Entity Doctor        @Entity Patient  │ │
│  │  - patientId               - doctorId            - appointmentId  │ │
│  │  - firstName               - firstName           - patientId (FK) │ │
│  │  - lastName                - lastName            - doctorId (FK)  │ │
│  │  - email                   - email               - date           │ │
│  │  - phoneNumber             - phoneNumber         - time           │ │
│  │  - dateOfBirth             - specialization      - status         │ │
│  │  - gender                  - licenseNumber       - reason         │ │
│  │  - address                 - experienceYears     - notes          │ │
│  │  - medicalHistory          - availableTimeSlot   - createdAt      │ │
│  │  - createdAt               - createdAt           - updatedAt      │ │
│  │  - updatedAt               - updatedAt                            │ │
│  │                                                                    │ │
│  │  @Entity Prescription      @Entity Medication                     │ │
│  │  - prescriptionId          - medicationId                         │ │
│  │  - patientId (FK)          - medicationName                       │ │
│  │  - doctorId (FK)           - description                          │ │
│  │  - appointmentId (FK)      - dosage                               │ │
│  │  - prescriptionDate        - manufacturer                         │ │
│  │  - instructions            - price                                │ │
│  │  - createdAt               - stockQuantity                        │ │
│  │  - updatedAt               - expiryDate                           │ │
│  │                             - createdAt                           │ │
│  │  @Entity PrescriptionMedication - updatedAt                       │ │
│  │  - id                                                              │ │
│  │  - prescriptionId (FK)                                            │ │
│  │  - medicationId (FK)                                              │ │
│  │  - quantity                                                        │ │
│  │  - frequency                                                       │ │
│  │  - durationDays                                                    │ │
│  │  - notes                                                           │ │
│  │  - createdAt                                                       │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                           │
└──────────────────────────────────────────────────────────────────────────┘
         │
         │ Database Connection (JDBC)
         │
         ▼
    ┌──────────────────────────┐
    │    MySQL Database        │
    │  (hospital_management)   │
    │                          │
    │ ├─ patients              │
    │ ├─ doctors               │
    │ ├─ appointments          │
    │ ├─ prescriptions         │
    │ ├─ medications           │
    │ └─ prescription_meds     │
    └──────────────────────────┘
```

---

## Data Flow Diagrams

### 1. Complete User Registration & Login Flow

```
┌────────────────────────────────────────────────────────────────────┐
│                   USER REGISTRATION FLOW                           │
└────────────────────────────────────────────────────────────────────┘

Step 1: Client Sends Registration Request
┌──────────────────────────────────────────────────────────────┐
│ POST http://localhost:8080/api/auth/register                │
│ {                                                            │
│   "email": "patient@example.com",                           │
│   "password": "securePass123",                              │
│   "fullName": "John Doe",                                   │
│   "userType": "PATIENT"                                     │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 2: API Gateway Routing
┌──────────────────────────────────────────────────────────────┐
│ API Gateway (8080)                                           │
│ - Matches path /api/auth/**                                 │
│ - Looks up user-service via Eureka                          │
│ - Forwards to: lb://user-service                            │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 3: Request Reaches User Service
┌──────────────────────────────────────────────────────────────┐
│ User Service (8082) - AuthController                        │
│ register(UserRegistrationRequest request)                   │
│ - Validates input (email format, password strength)         │
│ - Checks if email already exists in database               │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 4: Password Encoding & Entity Creation
┌──────────────────────────────────────────────────────────────┐
│ UserService.register(request):                              │
│ 1. Validate input parameters                                │
│    - Email must be unique                                   │
│    - Password must meet security requirements               │
│ 2. Encode password using BCrypt                             │
│ 3. Create User entity:                                      │
│    - id: Auto-generated                                     │
│    - email: Lowercased                                      │
│    - password: Encrypted                                    │
│    - fullName: As provided                                  │
│    - userType: PATIENT or DOCTOR                            │
│    - createdAt: Current timestamp                           │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 5: Save to Database
┌──────────────────────────────────────────────────────────────┐
│ UserRepository.save(user)                                   │
│ SQL: INSERT INTO users                                      │
│      (email, password, full_name, user_type, created_at)    │
│      VALUES (?, ?, ?, ?, ?)                                 │
│                                                              │
│ MySQL returns: Generated ID = 1                             │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 6: Response to Client
┌──────────────────────────────────────────────────────────────┐
│ HTTP 201 Created                                             │
│ {                                                            │
│   "message": "User registered successfully",                │
│   "userId": 1,                                               │
│   "email": "patient@example.com",                           │
│   "fullName": "John Doe",                                    │
│   "userType": "PATIENT"                                     │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────────────┐
│                   USER LOGIN FLOW                                   │
└────────────────────────────────────────────────────────────────────┘

Step 1: Client Sends Login Request
┌──────────────────────────────────────────────────────────────┐
│ POST http://localhost:8080/api/auth/login                   │
│ {                                                            │
│   "email": "patient@example.com",                           │
│   "password": "securePass123"                               │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 2: Gateway Routing
┌──────────────────────────────────────────────────────────────┐
│ API Gateway: Routes to user-service (Port 8082)             │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 3: Credential Validation
┌──────────────────────────────────────────────────────────────┐
│ UserService.login(LoginRequest):                            │
│ 1. Find user by email                                       │
│    SELECT * FROM users WHERE email = ?                      │
│ 2. If not found: throw UnauthorizedException                │
│ 3. If found: Compare passwords using BCrypt                 │
│    - bcrypt.matches(providedPassword, storedHashedPwd)      │
│ 4. If mismatch: throw UnauthorizedException                 │
│ 5. If match: Proceed to token generation                    │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 4: JWT Token Generation
┌──────────────────────────────────────────────────────────────┐
│ JwtTokenProvider.generateToken(user):                       │
│ Creates JWT with:                                           │
│ - Header: { "alg": "HS256", "typ": "JWT" }                 │
│ - Payload (Claims):                                         │
│   {                                                          │
│     "sub": "patient@example.com",                           │
│     "userId": 1,                                             │
│     "userType": "PATIENT",                                   │
│     "iat": 1702000000 (issued at),                          │
│     "exp": 1702086400 (expires at - 24 hrs)                 │
│   }                                                          │
│ - Signature: HMAC256(header.payload, secret-key)            │
│                                                              │
│ Token Format:                                                │
│ eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ...       │
│                                                              │
│ Base64 Encoded and URL Safe                                 │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 5: Response to Client
┌──────────────────────────────────────────────────────────────┐
│ HTTP 200 OK                                                  │
│ {                                                            │
│   "message": "Login successful",                             │
│   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",      │
│   "userId": 1,                                               │
│   "email": "patient@example.com",                           │
│   "fullName": "John Doe",                                    │
│   "userType": "PATIENT",                                     │
│   "expiresIn": 86400000                                      │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 6: Client Stores Token
┌──────────────────────────────────────────────────────────────┐
│ Client (Browser/Mobile):                                     │
│ - Stores JWT in localStorage or sessionStorage              │
│ - For future requests, includes in header:                  │
│   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...    │
└──────────────────────────────────────────────────────────────┘
```

### 2. Appointment Creation Flow

```
┌────────────────────────────────────────────────────────────────────┐
│                 APPOINTMENT CREATION FLOW                           │
└────────────────────────────────────────────────────────────────────┘

Step 1: Request with JWT Token
┌──────────────────────────────────────────────────────────────┐
│ POST http://localhost:8080/api/appointments                 │
│ Headers:                                                     │
│   Content-Type: application/json                            │
│   Authorization: Bearer <JWT_TOKEN>                         │
│ Body:                                                        │
│ {                                                            │
│   "patientId": 1,                                            │
│   "doctorId": 1,                                             │
│   "appointmentDate": "2024-04-20",                           │
│   "appointmentTime": "10:30",                                │
│   "reasonForVisit": "Regular checkup",                       │
│   "status": "SCHEDULED"                                      │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 2: Gateway Routes to Hospital Service
┌──────────────────────────────────────────────────────────────┐
│ API Gateway (8080):                                          │
│ - Path: /api/appointments/**                                │
│ - Routes to: lb://hospital-service                          │
│ - Load balancer selects instance (8081)                     │
│ - Forwards complete request                                 │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 3: Controller Receives Request
┌──────────────────────────────────────────────────────────────┐
│ Hospital Service (8081)                                      │
│ AppointmentController.create(appointment)                   │
│ - Validates @Valid annotation on entity                     │
│ - Calls AppointmentService                                  │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 4: Business Logic Processing
┌──────────────────────────────────────────────────────────────┐
│ AppointmentService.save(appointment):                       │
│                                                              │
│ Validation Chain:                                            │
│ 1. Check if patient exists                                  │
│    Query: SELECT * FROM patients WHERE patient_id = 1      │
│    - If not found: throw ResourceNotFoundException           │
│                                                              │
│ 2. Check if doctor exists                                   │
│    Query: SELECT * FROM doctors WHERE doctor_id = 1        │
│    - If not found: throw ResourceNotFoundException           │
│                                                              │
│ 3. Validate appointment date/time                           │
│    - Date must be in future                                 │
│    - Time must be during operating hours                    │
│                                                              │
│ 4. Check doctor availability                                │
│    Query: SELECT * FROM appointments                        │
│            WHERE doctor_id = 1                              │
│            AND appointment_date = '2024-04-20'              │
│            AND appointment_time = '10:30'                   │
│    - If slot taken: throw AppointmentConflictException      │
│                                                              │
│ 5. Check for patient conflicts                              │
│    Query: SELECT * FROM appointments                        │
│            WHERE patient_id = 1                             │
│            AND appointment_date = '2024-04-20'              │
│            AND appointment_time = '10:30'                   │
│    - If already scheduled: throw ConflictException          │
│                                                              │
│ All validations passed: Create appointment                  │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 5: Persist to Database
┌──────────────────────────────────────────────────────────────┐
│ AppointmentRepository.save(appointment):                    │
│                                                              │
│ SQL Execution:                                               │
│ INSERT INTO appointments                                    │
│ (patient_id, doctor_id, appointment_date,                  │
│  appointment_time, status, reason_for_visit,               │
│  created_at, updated_at)                                    │
│ VALUES (1, 1, '2024-04-20', '10:30:00',                    │
│         'SCHEDULED', 'Regular checkup',                     │
│         NOW(), NOW())                                        │
│                                                              │
│ Database returns:                                            │
│ - Generated appointment_id = 1                              │
│ - Affected rows = 1                                         │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Step 6: Response to Client
┌──────────────────────────────────────────────────────────────┐
│ HTTP 200 OK (or 201 Created)                                │
│ {                                                            │
│   "appointmentId": 1,                                        │
│   "patientId": 1,                                            │
│   "doctorId": 1,                                             │
│   "appointmentDate": "2024-04-20",                           │
│   "appointmentTime": "10:30",                                │
│   "status": "SCHEDULED",                                     │
│   "reasonForVisit": "Regular checkup",                       │
│   "createdAt": "2024-04-19T15:30:00Z",                       │
│   "updatedAt": "2024-04-19T15:30:00Z"                        │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
Client receives confirmation
and can display appointment details
```

---

## Sequence Diagrams

### Service Startup Sequence

```
┌────────────────────────────────────────────────────────────────────┐
│           MICROSERVICES STARTUP SEQUENCE (BOOT ORDER)               │
└────────────────────────────────────────────────────────────────────┘

Time→

T0: System Start
│
├─ MySQL Database Ready
│  └─ hospital_management DB initialized
│     └─ All tables created (Hibernate DDL: update)
│
│
T1: Eureka Server Starts (Port 8761)
│  ┌─ Initializes Eureka Registry
│  ├─ Starts embedded Tomcat server
│  ├─ Binding to localhost:8761
│  ├─ Creates in-memory registry
│  └─ Ready to accept registrations
│     Event: "Eureka Server started successfully"
│
│
T2: User Service Starts (Port 8082)
│  ┌─ Loads application.yaml
│  ├─ Initializes Spring Boot Application
│  ├─ Connects to MySQL Database
│  ├─ Creates JPA entities
│  ├─ Starts embedded Tomcat (8082)
│  ├─ Enables Eureka Discovery Client
│  │  └─ Reads eureka.client.service-url.defaultZone
│  │     └─ eureka.instance.hostname = localhost:8082
│  ├─ Sends Registration Request to Eureka
│  │  POST /eureka/apps/USER-SERVICE
│  │  {
│  │    "instanceId": "user-service:8082",
│  │    "hostName": "localhost",
│  │    "port": 8082,
│  │    "status": "UP"
│  │  }
│  ├─ Starts Heartbeat Timer (30 sec intervals)
│  └─ Ready for requests
│     Event: "user-service registered with Eureka"
│
│
T3: Admin Service Starts (Port 8083)
│  ┌─ Similar to User Service
│  ├─ Registers as "ADMIN-SERVICE:8083"
│  ├─ Heartbeat timer starts
│  └─ Ready
│     Event: "admin-service registered with Eureka"
│
│
T4: Hospital Service Starts (Port 8081)
│  ┌─ Similar process
│  ├─ Registers as "HOSPITAL-SERVICE:8081"
│  ├─ Starts heartbeat
│  └─ Ready
│     Event: "hospital-service registered with Eureka"
│
│
T5: API Gateway Starts (Port 8080)
│  ┌─ Loads API Gateway configuration
│  ├─ Enables Spring Cloud Gateway
│  ├─ Enables Eureka Discovery Client
│  ├─ Queries Eureka for Service Locations
│  │  GET /eureka/apps
│  │  ├─ Receives: USER-SERVICE instances
│  │  ├─ Receives: ADMIN-SERVICE instances
│  │  ├─ Receives: HOSPITAL-SERVICE instances
│  │  └─ Creates load balancer for each service
│  ├─ Caches service registry locally
│  ├─ Starts Eureka Watcher (30 sec updates)
│  ├─ Starts embedded Tomcat (8080)
│  └─ Ready to route requests
│     Event: "API Gateway started successfully"
│
│
T6: System Ready for Traffic
│  All services UP and registered
│  └─ Eureka Dashboard shows:
│     ├─ user-service (1 instance) - UP
│     ├─ admin-service (1 instance) - UP
│     ├─ hospital-service (1 instance) - UP
│     └─ api-gateway (1 instance) - UP
│
│
T7+: Runtime Operations
   ├─ Continuous heartbeat from services (every 30 sec)
   ├─ API Gateway receiving requests
   ├─ Services processing business logic
   ├─ Database operations
   └─ Response back through gateway


HEARTBEAT MECHANISM:
┌────────────────────────────────────────────┐
│ Every 30 seconds (configurable)            │
├────────────────────────────────────────────┤
│ Service sends heartbeat to Eureka:         │
│ PUT /eureka/apps/SERVICE-NAME/instanceId   │
│                                            │
│ Eureka Checks:                             │
│ ├─ Is service still alive? YES             │
│ ├─ Update lastHeartbeat timestamp          │
│ ├─ Set status = UP                         │
│ └─ Return 200 OK                           │
│                                            │
│ If no heartbeat for 90 seconds:            │
│ └─ Service marked as DOWN                  │
│    └─ Removed from available instances     │
└────────────────────────────────────────────┘
```

### Request Processing Sequence

```
┌────────────────────────────────────────────────────────────────────┐
│         COMPLETE REQUEST PROCESSING SEQUENCE                        │
└────────────────────────────────────────────────────────────────────┘

Client
  │
  │ 1. HTTP Request
  │    POST /api/auth/login
  │    {email, password}
  │
  ▼
API Gateway (8080)
  │
  │ 2. Path Matching
  │    Checks: /api/auth/** matches?
  │    Route: user-service-auth
  │    URI: lb://user-service
  │
  │ 3. Service Discovery
  │    Query Eureka Cache:
  │    "Give me available instances of user-service"
  │    Eureka responds: {host: localhost, port: 8082}
  │
  │ 4. Load Balancing
  │    Select instance (only one in dev)
  │    Target: http://localhost:8082
  │
  │ 5. URL Rewriting
  │    Original: /api/auth/login
  │    Rewrite: /auth/login (remove /api prefix)
  │
  │ 6. Forward Request
  │    All headers, body forwarded
  │    New URL: http://localhost:8082/auth/login
  │
  ▼
User Service (8082)
  │
  │ 7. Request Processing
  │    Spring DispatcherServlet receives request
  │    Identifies handler: AuthController.login()
  │
  │ 8. Parameter Binding
  │    JSON body → LoginRequest object
  │    @RequestBody deserialization
  │
  │ 9. Validation
  │    @Valid annotation triggers:
  │    - email format check
  │    - password not null
  │    - other constraint checks
  │    If validation fails: return 400 Bad Request
  │
  │ 10. Authentication Logic
  │     UserService.login(LoginRequest)
  │     ├─ Query database: getUserByEmail(email)
  │     │  └─ If not found: throw UnauthorizedException
  │     │
  │     ├─ Password comparison: bcrypt.matches()
  │     │  └─ If mismatch: throw UnauthorizedException
  │     │
  │     └─ Generate JWT token
  │        └─ JwtTokenProvider.generateToken(user)
  │           └─ Create token with 24-hour expiration
  │
  │ 11. Response Creation
  │     LoginResponse object with:
  │     - token: JWT string
  │     - userId: user ID
  │     - email: user email
  │     - fullName: user name
  │     - userType: PATIENT/DOCTOR
  │
  ▼
API Gateway (8080)
  │
  │ 12. Response Interception
  │     Gateway receives response from service
  │     HTTP 200 OK with LoginResponse body
  │
  │ 13. Response Forwarding
  │     Apply response filters (if any)
  │     Add gateway headers
  │     Forward to client
  │
  ▼
Client (Browser/Mobile)
  │
  │ 14. Response Processing
  │     Receives HTTP 200 with JWT token
  │     JavaScript processes response
  │     Stores token in localStorage
  │
  │ 15. Future Requests
  │     Include Authorization header:
  │     Authorization: Bearer <JWT_TOKEN>
  │     └─ Services validate token
  │        └─ Extract user info from token


DETAILED USER SERVICE LOGIN FLOW:

UserService.login(LoginRequest request):
│
├─ 1. Find user by email
│  │   Query: SELECT * FROM users WHERE email = ?
│  │   └─ If ResultSet is empty → throw InvalidCredentialsException
│  │
│  └─ User foundUserEntity user = result
│
├─ 2. Password Verification
│  │   storedPassword = user.getPassword()    // BCrypt hash
│  │   providedPassword = request.getPassword()
│  │   
│  │   if (!passwordEncoder.matches(providedPassword, storedPassword)) {
│  │     throw new InvalidCredentialsException("Invalid password")
│  │   }
│  │
│  └─ Password matches!
│
├─ 3. Generate JWT Token
│  │   token = jwtTokenProvider.generateToken(user)
│  │   
│  │   Token creation steps:
│  │   ├─ Create Claims object
│  │   │  ├─ subject: user.email
│  │   │  ├─ userId: user.id
│  │   │  ├─ userType: user.userType
│  │   │  ├─ issuedAt: now()
│  │   │  └─ expiration: now() + 24 hours
│  │   │
│  │   ├─ Create SignatureAlgorithm: HS256
│  │   │
│  │   ├─ Build JwtBuilder
│  │   │  ├─ .setClaims(claims)
│  │   │  ├─ .signWith(jwtSecret, HS256)
│  │   │  └─ .compact()  // returns JWT string
│  │   │
│  │   └─ token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWI..."
│  │
│  └─ JWT generated
│
├─ 4. Create Response Object
│  │   LoginResponse response = new LoginResponse()
│  │   response.setToken(token)
│  │   response.setUserId(user.getId())
│  │   response.setEmail(user.getEmail())
│  │   response.setFullName(user.getFullName())
│  │   response.setUserType(user.getUserType())
│  │   response.setMessage("Login successful")
│  │
│  └─ Response prepared
│
└─ 5. Return response to controller
    └─ Controller returns to API Gateway
       └─ Gateway returns to client
```

---

## Database Schema

### Complete Database Diagram

```
hospital_management (MySQL Database)
│
├─ users (User Service)
│  ├─ id (BIGINT, PRIMARY KEY)
│  ├─ email (VARCHAR(255), UNIQUE, NOT NULL)
│  ├─ password (VARCHAR(255), NOT NULL)
│  ├─ full_name (VARCHAR(255), NOT NULL)
│  ├─ user_type (VARCHAR(50), ENUM: PATIENT, DOCTOR)
│  ├─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
│  └─ updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE)
│
├─ admins (Admin Service)
│  ├─ id (BIGINT, PRIMARY KEY)
│  ├─ email (VARCHAR(255), UNIQUE, NOT NULL)
│  ├─ password (VARCHAR(255), NOT NULL)
│  ├─ full_name (VARCHAR(255), NOT NULL)
│  ├─ role (VARCHAR(50), ENUM: SUPER_ADMIN, ADMIN, MANAGER)
│  ├─ department (VARCHAR(50), ENUM: HR, FINANCE, OPERATIONS, IT)
│  ├─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
│  └─ updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE)
│
├─ patients (Hospital Service)
│  ├─ patient_id (BIGINT, PRIMARY KEY)
│  ├─ first_name (VARCHAR(100), NOT NULL)
│  ├─ last_name (VARCHAR(100), NOT NULL)
│  ├─ email (VARCHAR(255), UNIQUE)
│  ├─ phone_number (VARCHAR(20))
│  ├─ date_of_birth (DATE)
│  ├─ gender (VARCHAR(10), ENUM: MALE, FEMALE, OTHER)
│  ├─ address (TEXT)
│  ├─ medical_history (LONGTEXT)
│  ├─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
│  └─ updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE)
│
├─ doctors (Hospital Service)
│  ├─ doctor_id (BIGINT, PRIMARY KEY)
│  ├─ first_name (VARCHAR(100), NOT NULL)
│  ├─ last_name (VARCHAR(100), NOT NULL)
│  ├─ email (VARCHAR(255), UNIQUE)
│  ├─ phone_number (VARCHAR(20))
│  ├─ specialization (VARCHAR(100))
│  ├─ license_number (VARCHAR(50), UNIQUE)
│  ├─ experience_years (INT)
│  ├─ available_time_slot (VARCHAR(255))
│  ├─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
│  └─ updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE)
│
├─ appointments (Hospital Service)
│  ├─ appointment_id (BIGINT, PRIMARY KEY)
│  ├─ patient_id (BIGINT, FOREIGN KEY → patients.patient_id)
│  ├─ doctor_id (BIGINT, FOREIGN KEY → doctors.doctor_id)
│  ├─ appointment_date (DATE, NOT NULL)
│  ├─ appointment_time (TIME, NOT NULL)
│  ├─ status (VARCHAR(50), ENUM: SCHEDULED, COMPLETED, CANCELLED)
│  ├─ reason_for_visit (TEXT)
│  ├─ notes (LONGTEXT)
│  ├─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
│  ├─ updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE)
│  └─ UNIQUE (patient_id, doctor_id, appointment_date, appointment_time)
│
├─ prescriptions (Hospital Service)
│  ├─ prescription_id (BIGINT, PRIMARY KEY)
│  ├─ patient_id (BIGINT, FOREIGN KEY → patients.patient_id)
│  ├─ doctor_id (BIGINT, FOREIGN KEY → doctors.doctor_id)
│  ├─ appointment_id (BIGINT, FOREIGN KEY → appointments.appointment_id)
│  ├─ prescription_date (DATE, NOT NULL)
│  ├─ instructions (LONGTEXT)
│  ├─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
│  └─ updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE)
│
├─ medications (Hospital Service)
│  ├─ medication_id (BIGINT, PRIMARY KEY)
│  ├─ medication_name (VARCHAR(255), UNIQUE, NOT NULL)
│  ├─ description (TEXT)
│  ├─ dosage (VARCHAR(100))
│  ├─ manufacturer (VARCHAR(255))
│  ├─ price (DECIMAL(10, 2))
│  ├─ stock_quantity (INT)
│  ├─ expiry_date (DATE)
│  ├─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
│  └─ updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE)
│
└─ prescription_medications (Hospital Service - Junction Table)
   ├─ prescription_medication_id (BIGINT, PRIMARY KEY)
   ├─ prescription_id (BIGINT, FOREIGN KEY → prescriptions.prescription_id)
   ├─ medication_id (BIGINT, FOREIGN KEY → medications.medication_id)
   ├─ quantity (INT)
   ├─ frequency (VARCHAR(50), ENUM: ONCE_DAILY, TWICE_DAILY, THREE_TIMES_DAILY, FOUR_TIMES_DAILY)
   ├─ duration_days (INT)
   ├─ notes (TEXT)
   └─ created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)


RELATIONSHIPS DIAGRAM:

      doctors ←──────────┐
        ▲                │
        │         appointment_id (FK)
        │                │
        │ doctor_id (FK) ▼
        │           appointments
        │                │
        │ patient_id (FK)│
        │                ▼
        │           patients (1)
        │                │
        │                └─ (1) ─ Many prescriptions via FK
        │
        └─ (1) ─ Many prescriptions via FK prescriptions (M)
                           │
                           ├─ patient_id (FK)
                           ├─ doctor_id (FK)
                           └─ appointment_id (FK)
                                  │
                                  ▼
                     prescription_medications (Junction)
                                  │
                                  ├─ prescription_id (FK)
                                  └─ medication_id (FK)
                                           │
                                           ▼
                                    medications (1)
```

---

## Component Interactions

### Service Mesh Communication

```
┌────────────────────────────────────────────────────────────────────┐
│                    SERVICE COMMUNICATION PATTERNS                   │
└────────────────────────────────────────────────────────────────────┘

1. CLIENT → GATEWAY → SERVICE (Most Common)
   
   Client
     │
     └─→ API Gateway (8080)
           │
           ├─ URL Matching
           ├─ Service Discovery (Eureka)
           ├─ Load Balancing
           ├─ URL Rewriting
           └─→ Service (8082/8083/8081)


2. SERVICE → SERVICE (Direct Communication, if needed)

   Service A (8083)
     │
     ├─ Discovers Service B via Eureka
     │  Query: "Where is service-b?"
     │  Response: "localhost:8081"
     │
     └─→ Service B (8081)
           │
           └─→ Execute operation
           └─→ Return result


3. SERVICE → DATABASE (All Services)

   Service
     │
     └─→ MySQL Connection Pool (HikariCP)
           │
           ├─ Create JDBC connection (if not in pool)
           ├─ Send SQL query
           ├─ Receive ResultSet
           ├─ Map to Entity/DTO
           └─→ Return to Service Layer


EXAMPLE: Admin Service needs User Information

  Admin Service                    Eureka Server                User Service
       │                               │                            │
       └─ Query: "Where is           ◀─┤ Looks up registry          │
         user-service?"                                              │
                                   ┌──┴────────────────────────────┘
                                   │
       ┌──────────────────────────→ user-service is at:
       │                            localhost:8082
       │
       └─ HTTP GET request to localhost:8082/api/auth/user/1
                                                    │
                                                    └─→ User Service
                                                        Returns User DTO
                                                    ◀─
       Receives User information
       Uses in Admin Service logic
```

---

## Conclusion

This document provides a comprehensive view of the Hospital Management System microservices architecture. 

**Key Takeaways:**

1. **5 Microservices**: Eureka Server, API Gateway, User Service, Admin Service, Hospital Service
2. **Service Discovery**: All services register with Eureka for dynamic discovery
3. **API Gateway**: Single entry point with intelligent routing and load balancing
4. **JWT Authentication**: Secure token-based authentication across services
5. **Shared Database**: Single MySQL database (can be separated per service in production)
6. **RESTful APIs**: All communication via HTTP/REST endpoints
7. **Spring Cloud Stack**: Leverages industry-standard microservices patterns


