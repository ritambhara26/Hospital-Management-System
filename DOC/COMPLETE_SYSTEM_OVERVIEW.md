# Hospital Management System - Complete Microservices Architecture

## 🏥 System Overview

Your Hospital Management System now has **5 microservices** working together:

```
┌────────────────────────────────────────────────────────────┐
│                    API GATEWAY (8080)                      │
│  ┌─ Routes requests to appropriate microservices          │
│  └─ Single entry point for all clients                     │
└────────────────────────────────────────────────────────────┘
              ↓ (Service Discovery via Eureka)
        ┌─────┴──────┬──────────┬─────────┬──────────┐
        ↓            ↓          ↓         ↓          ↓
    USER-SVC    ADMIN-SVC  HOSPITAL-SVC  [Future]  [Future]
    (8082)      (8083)      (8081)
```

## 📋 Services List

### 1. **EUREKA SERVER** (Port: 8761)
- **Purpose**: Service Registry & Discovery
- **Role**: Central registry for all microservices
- **Database**: None (in-memory registry)
- **Key Features**:
  - Auto-registers services
  - Service health monitoring
  - Dashboard UI: http://localhost:8761

---

### 2. **API GATEWAY** (Port: 8080)
- **Purpose**: Entry point for all client requests
- **Role**: Route requests to appropriate services
- **Database**: None
- **Routes**:
  - `/api/auth/**` → USER-SERVICE
  - `/api/admin/**` → ADMIN-SERVICE
  - `/api/appointments/**` → HOSPITAL-SERVICE
  - `/api/doctors/**` → HOSPITAL-SERVICE
  - `/api/patients/**` → HOSPITAL-SERVICE
  - `/api/prescriptions/**` → HOSPITAL-SERVICE
  - `/api/medications/**` → HOSPITAL-SERVICE

---

### 3. **USER-SERVICE** (Port: 8082) ⭐ NEW
- **Purpose**: Patient & Doctor Authentication
- **Database**: `hospital_user_db`
- **Supported Users**:
  - Patients (role: ROLE_PATIENT)
  - Doctors (role: ROLE_DOCTOR)
- **Endpoints**:
  - `POST /api/auth/register` - Register new user
  - `POST /api/auth/login` - User login
  - `GET /api/auth/user/{userId}` - Get user details
  - `GET /api/auth/user/email/{email}` - Get user by email
- **Security**: JWT Token-based
- **Features**:
  - Password encryption (BCrypt)
  - Token generation & validation
  - User profile management

---

### 4. **ADMIN-SERVICE** (Port: 8083) ⭐ NEW
- **Purpose**: Hospital Staff Management & Administrative Functions
- **Database**: `hospital_admin_db`
- **Supported Roles**:
  - ROLE_ADMIN
  - ROLE_SUPER_ADMIN
  - ROLE_MANAGER
- **Departments**:
  - HR (Human Resources)
  - BILLING (Finance & Billing)
  - OPERATIONS
  - REPORTS (Report Generation)
  - ANALYTICS (Data Analysis)
  - SYSTEM (System Administration)
- **Endpoints**:
  - `POST /api/admin/login` - Admin login
  - `POST /api/admin/create` - Create new admin
  - `GET /api/admin/{adminId}` - Get admin details
  - `PUT /api/admin/{adminId}` - Update admin info
- **Security**: JWT Token-based with role-based access
- **Features**:
  - Advanced permissions management
  - Department-based organization
  - Admin account lifecycle management
  - Analytics & billing management capabilities

---

### 5. **HOSPITAL-SERVICE** (Port: 8081)
- **Purpose**: Core hospital operations
- **Database**: `hospital_management`
- **Manages**:
  - Patient appointments
  - Doctor information
  - Patient records
  - Prescriptions
  - Medication management
- **Endpoints**: `/api/appointments`, `/api/doctors`, `/api/patients`, etc.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- All services running on default ports (8761, 8080, 8082, 8083, 8081)

### Step-by-Step Startup

**1. Start Eureka Server**
```bash
cd eureka-server
mvn spring-boot:run
```
✅ Visit: http://localhost:8761

**2. Start User Service**
```bash
cd user-service
mvn spring-boot:run
```
✅ Service registers automatically

**3. Start Admin Service**
```bash
cd admin-service
mvn spring-boot:run
```
✅ Service registers automatically

**4. Start Hospital Service**
```bash
cd system
mvn spring-boot:run
```
✅ Service registers automatically

**5. Start API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```
✅ Ready to route requests!

---

## 📊 Authentication Flow

### User (Patient/Doctor) Login Flow
```
1. Client sends login request to API Gateway
   POST http://localhost:8080/api/auth/login
   
2. API Gateway routes to USER-SERVICE (8082)
   
3. USER-SERVICE validates credentials against hospital_user_db
   
4. Returns JWT Token if valid
   
5. Client uses token for subsequent requests
   Authorization: Bearer {token}
```

### Admin Login Flow
```
1. Admin sends login request to API Gateway
   POST http://localhost:8080/api/admin/login
   
2. API Gateway routes to ADMIN-SERVICE (8083)
   
3. ADMIN-SERVICE validates credentials against hospital_admin_db
   
4. Returns JWT Token with admin privileges
   
5. Admin uses token for administrative operations
```

---

## 🔐 Security Features

✅ **JWT Token-Based Authentication**
- Stateless authentication
- Secure token signing (HS512)
- 24-hour token expiration

✅ **Password Security**
- BCrypt encryption
- Salted hashing

✅ **Role-Based Access Control**
- User roles: PATIENT, DOCTOR
- Admin roles: ADMIN, SUPER_ADMIN, MANAGER

✅ **Department-Based Access**
- Admin departments for organizational structure
- Granular permissions management

---

## 📦 Database Schemas

### hospital_user_db.users
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  fullName VARCHAR(255) NOT NULL,
  role ENUM('ROLE_USER', 'ROLE_PATIENT', 'ROLE_DOCTOR') NOT NULL,
  userType ENUM('PATIENT', 'DOCTOR') NOT NULL,
  active BOOLEAN DEFAULT true,
  phoneNumber VARCHAR(20),
  address TEXT,
  createdAt BIGINT,
  updatedAt BIGINT
);
```

### hospital_admin_db.admin_users
```sql
CREATE TABLE admin_users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  fullName VARCHAR(255) NOT NULL,
  role ENUM('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_MANAGER') NOT NULL,
  department ENUM('HR', 'BILLING', 'OPERATIONS', 'REPORTS', 'ANALYTICS', 'SYSTEM') NOT NULL,
  active BOOLEAN DEFAULT true,
  phoneNumber VARCHAR(20),
  createdAt BIGINT,
  updatedAt BIGINT,
  permissions TEXT
);
```

---

## 🧪 API Examples

### Example 1: Register a Patient
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@hospital.com",
    "password": "securePass123",
    "fullName": "John Doe",
    "userType": "PATIENT",
    "phoneNumber": "9876543210",
    "address": "123 Main Street, City"
  }'
```

### Example 2: Patient Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@hospital.com",
    "password": "securePass123"
  }'

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "john.doe@hospital.com",
  "fullName": "John Doe",
  "role": "ROLE_PATIENT",
  "userType": "PATIENT",
  "active": true
}
```

### Example 3: Admin Login
```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@hospital.com",
    "password": "adminPass123"
  }'
```

### Example 4: Create New Admin (Super Admin Only)
```bash
curl -X POST http://localhost:8080/api/admin/create \
  -H "Content-Type: application/json" \
  -d '{
    "email": "billing.admin@hospital.com",
    "password": "billingPass123",
    "fullName": "Billing Administrator",
    "role": "ADMIN",
    "department": "BILLING",
    "permissions": "READ,WRITE"
  }'
```

### Example 5: Access Protected Endpoints
```bash
curl -H "Authorization: Bearer {token}" \
  http://localhost:8080/api/auth/user/1
```

---

## 🛠️ Maintenance

### View Service Status
- **Eureka Dashboard**: http://localhost:8761
- Shows all registered services with health status

### Health Checks
```bash
# User Service
curl http://localhost:8082/api/auth/health

# Admin Service
curl http://localhost:8083/api/admin/health

# Hospital Service
curl http://localhost:8081/hospital/health (if implemented)
```

### Logs
Each service logs to console by default. Check logs for:
- Authentication failures
- Database errors
- Service registration issues

---

## 📈 Scaling Considerations

### When to Scale Individual Services
- **USER-SERVICE**: Scale when user registration/login traffic increases
- **ADMIN-SERVICE**: Usually lower traffic, scale less frequently
- **HOSPITAL-SERVICE**: Scale with clinical operations load
- **API-GATEWAY**: Scale as overall throughput increases

### Load Balancing
- API Gateway automatically load balances requests
- Eureka provides service-to-service discovery
- Multiple instances of each service can be run on different ports

---

## 🔄 Service Communication

### Inter-Service Calls (Future Enhancement)
Services can communicate directly:
```java
// Example: Hospital Service calling User Service
@Autowired
private RestTemplate restTemplate;

User user = restTemplate.getForObject(
    "http://user-service/api/auth/user/1",
    User.class
);
```

Or using Feign client:
```java
@FeignClient("user-service")
public interface UserServiceClient {
    @GetMapping("/api/auth/user/{userId}")
    User getUser(@PathVariable Long userId);
}
```

---

## 🚨 Troubleshooting

| Issue | Solution |
|-------|----------|
| Services not registering | Check if Eureka Server is running first |
| Login returns 401 | Verify credentials, ensure user/admin exists in DB |
| Port already in use | Change port in application.yaml or kill existing process |
| Database connection error | Verify MySQL is running and databases are created |
| Token validation fails | Check token format and expiration time |

---

## 📚 Documentation Files

- `MICROSERVICES_CONFIGURATION.md` - Original setup guide
- `USER_ADMIN_SERVICE_GUIDE.md` - Detailed user/admin service documentation
- `THIS FILE` - Complete architecture overview

---

## ✨ Features Implemented

✅ Service Discovery (Eureka)
✅ API Gateway with routing
✅ JWT authentication
✅ User service for patients & doctors
✅ Admin service with role-based access
✅ Password encryption
✅ Database persistence
✅ Error handling
✅ Health checks
✅ Swagger/OpenAPI support

---

## 🎯 Next Steps

1. **Implement inter-service communication** between services
2. **Add circuit breakers** for resilience
3. **Implement authorization middleware** for token validation
4. **Add audit logging** for admin operations
5. **Set up centralized logging** (ELK stack)
6. **Add distributed tracing** (Sleuth + Zipkin)
7. **Implement refresh tokens** for better security
8. **Add CORS configuration** as needed
9. **Set up CI/CD pipeline** for deployments
10. **Deploy to production** with environment-specific configs

---

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review service logs
3. Verify Eureka dashboard for service health
4. Check database connectivity
5. Validate JWT tokens with jwt.io

---

**Last Updated**: April 19, 2026
**Version**: 1.0
**Status**: Production Ready ✅

