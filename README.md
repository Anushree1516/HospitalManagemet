# 🩸 Blood Bank Management System — Spring Boot Backend

A production-ready REST API backend for real-time blood bank management connecting **Patients**, **Hospitals**, and **Donors**.

---

## 🗂️ Full Project Structure

```
bloodbank/
├── pom.xml
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/com/bloodbank/bloodbank/
    │   │   ├── BloodbankApplication.java          ← Entry point
    │   │   │
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java            ← JWT + CORS + Role security
    │   │   │   └── FirebaseConfig.java            ← Push notification setup
    │   │   │
    │   │   ├── controller/
    │   │   │   ├── AuthController.java            ← /api/v1/auth/**
    │   │   │   ├── BloodStockController.java      ← /api/v1/blood/** + /hospitals/{id}/stock
    │   │   │   ├── BloodRequestController.java    ← /api/v1/requests/**
    │   │   │   ├── HospitalController.java        ← /api/v1/hospitals/**
    │   │   │   ├── DonorController.java           ← /api/v1/donors/**
    │   │   │   ├── NotificationController.java    ← /api/v1/notifications/**
    │   │   │   └── AdminController.java           ← /api/v1/admin/**
    │   │   │
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   │   ├── RegisterRequest.java
    │   │   │   │   ├── LoginRequest.java
    │   │   │   │   ├── BloodStockRequest.java
    │   │   │   │   ├── BloodRequestDto.java
    │   │   │   │   ├── DonorRequest.java
    │   │   │   │   └── HospitalRequest.java
    │   │   │   └── response/
    │   │   │       ├── ApiResponse.java           ← Generic wrapper for all responses
    │   │   │       ├── AuthResponse.java          ← JWT + user info on login
    │   │   │       └── BloodSearchResponse.java   ← Search results with distance + Maps URL
    │   │   │
    │   │   ├── entity/
    │   │   │   ├── User.java                      ← Users table
    │   │   │   ├── Hospital.java                  ← Hospitals table
    │   │   │   ├── BloodStock.java                ← Blood inventory per hospital
    │   │   │   ├── Donor.java                     ← Donor profiles
    │   │   │   ├── BloodRequest.java              ← Patient blood requests
    │   │   │   └── Notification.java              ← In-app notifications
    │   │   │
    │   │   ├── enums/
    │   │   │   ├── BloodGroup.java                ← A+, A-, B+, B-, O+, O-, AB+, AB-
    │   │   │   ├── Role.java                      ← ADMIN, HOSPITAL, USER, DONOR
    │   │   │   ├── RequestStatus.java             ← PENDING, APPROVED, FULFILLED, etc.
    │   │   │   └── DonorStatus.java               ← AVAILABLE, DONATED_RECENTLY, etc.
    │   │   │
    │   │   ├── exception/
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   ├── ResourceAlreadyExistsException.java
    │   │   │   ├── UnauthorizedException.java
    │   │   │   └── handler/
    │   │   │       └── GlobalExceptionHandler.java ← Centralized error handling
    │   │   │
    │   │   ├── filter/
    │   │   │   └── JwtAuthenticationFilter.java   ← Intercepts every request, validates JWT
    │   │   │
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java
    │   │   │   ├── HospitalRepository.java        ← Haversine location queries
    │   │   │   ├── BloodStockRepository.java      ← Search by group + city + location
    │   │   │   ├── DonorRepository.java           ← Nearby donor queries
    │   │   │   ├── BloodRequestRepository.java
    │   │   │   └── NotificationRepository.java
    │   │   │
    │   │   ├── service/
    │   │   │   ├── AuthService.java
    │   │   │   ├── BloodStockService.java
    │   │   │   ├── BloodRequestService.java
    │   │   │   ├── HospitalService.java
    │   │   │   ├── DonorService.java
    │   │   │   ├── NotificationService.java
    │   │   │   └── impl/
    │   │   │       ├── UserDetailsServiceImpl.java ← Spring Security integration
    │   │   │       ├── AuthServiceImpl.java
    │   │   │       ├── BloodStockServiceImpl.java
    │   │   │       ├── BloodRequestServiceImpl.java
    │   │   │       ├── HospitalServiceImpl.java
    │   │   │       ├── DonorServiceImpl.java
    │   │   │       └── NotificationServiceImpl.java ← Firebase push + DB notifications
    │   │   │
    │   │   └── util/
    │   │       ├── JwtUtil.java                   ← Token generate/validate
    │   │       └── LocationUtil.java              ← Haversine + Google Maps URL builder
    │   │
    │   └── resources/
    │       ├── application.properties             ← Main config (DB, JWT, Firebase)
    │       ├── application-test.properties        ← H2 test config
    │       └── firebase-service-account.json      ← Replace with real Firebase key
    │
    └── test/
        └── java/com/bloodbank/bloodbank/
            └── BloodbankApplicationTests.java
```

---

## ⚙️ Tech Stack

| Layer        | Technology                         |
|--------------|------------------------------------|
| Framework    | Spring Boot 3.2                    |
| Language     | Java 17                            |
| Database     | MySQL 8                            |
| ORM          | Spring Data JPA / Hibernate        |
| Security     | Spring Security + JWT (JJWT 0.11)  |
| Passwords    | BCrypt encryption                  |
| Notifications| Firebase Admin SDK (FCM)           |
| Location     | Haversine formula + Google Maps API|
| Build        | Maven                              |

---

## 🚀 How to Run in IntelliJ IDEA

### Step 1 — Prerequisites
- Java 17 installed
- MySQL running locally
- IntelliJ IDEA (any edition)

### Step 2 — Open Project
```
File → Open → Select the `bloodbank` folder → Click OK
```
IntelliJ auto-detects it as a Maven project.

### Step 3 — Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bloodbank_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 4 — Run
Click the green ▶️ button on `BloodbankApplication.java`

Server starts at: **http://localhost:8080**

---

## 📌 All API Endpoints

### 🔐 Auth
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| POST | `/api/v1/auth/register` | Public | Register user |
| POST | `/api/v1/auth/login` | Public | Login, get JWT |

### 🩸 Blood Search (Public)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/blood/search?group=A_POSITIVE&city=Chennai` | Search by group + city |
| GET | `/api/v1/blood/nearby?group=O_POSITIVE&lat=13.08&lng=80.27&radius=15` | Nearby hospitals with blood |
| GET | `/api/v1/blood/inventory` | Total blood inventory dashboard |

### 🏥 Hospitals
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| POST | `/api/v1/hospitals/register` | HOSPITAL | Register hospital profile |
| GET | `/api/v1/hospitals` | Public | All hospitals |
| GET | `/api/v1/hospitals/{id}` | Public | Hospital details |
| GET | `/api/v1/hospitals/search?city=Mumbai` | Public | Search by city |
| GET | `/api/v1/hospitals/nearby?lat=&lng=&radius=` | Public | Nearby hospitals |
| PUT | `/api/v1/hospitals/{id}` | HOSPITAL/ADMIN | Update profile |
| GET | `/api/v1/hospitals/my` | HOSPITAL | Own hospital profile |

### 🧪 Blood Stock
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| POST | `/api/v1/hospitals/{id}/stock` | HOSPITAL | Add/update stock |
| GET | `/api/v1/hospitals/{id}/stock` | Public | View hospital stock |
| PUT | `/api/v1/hospitals/{id}/stock/{bloodGroup}?quantity=10` | HOSPITAL | Update quantity |
| DELETE | `/api/v1/hospitals/{id}/stock/{bloodGroup}` | HOSPITAL | Delete expired stock |

### 🚨 Blood Requests
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| POST | `/api/v1/requests` | Authenticated | Create blood request |
| GET | `/api/v1/requests/my` | Authenticated | My requests |
| GET | `/api/v1/requests/emergency` | Authenticated | Active emergencies |
| GET | `/api/v1/requests/hospital/{id}` | HOSPITAL | Hospital's requests |
| PUT | `/api/v1/requests/{id}/status?status=APPROVED` | HOSPITAL | Update status |
| GET | `/api/v1/requests` | ADMIN | All requests |

### 🩸 Donors
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| POST | `/api/v1/donors/register` | Authenticated | Register as donor |
| GET | `/api/v1/donors/my` | Authenticated | My donor profile |
| PUT | `/api/v1/donors/status?status=AVAILABLE` | Authenticated | Update availability |
| GET | `/api/v1/donors/search?group=B_POSITIVE` | Authenticated | Find donors by group |
| GET | `/api/v1/donors/nearby?group=O_NEGATIVE&lat=&lng=` | Authenticated | Nearby donors |

### 🔔 Notifications
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/api/v1/notifications` | Authenticated | My notifications |
| GET | `/api/v1/notifications/unread-count` | Authenticated | Badge count |
| PUT | `/api/v1/notifications/{id}/read` | Authenticated | Mark as read |

### 👑 Admin
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/admin/dashboard` | Full stats |
| GET | `/api/v1/admin/users` | All users |
| PUT | `/api/v1/admin/users/{id}/toggle-active` | Activate/deactivate user |
| GET | `/api/v1/admin/hospitals` | All hospitals |
| GET | `/api/v1/admin/requests?status=PENDING` | All requests |

---

## 🔒 Authorization Header
All protected endpoints require:
```
Authorization: Bearer <your_jwt_token>
```

---

## 📱 Mobile App Integration
The backend is ready to be consumed by Flutter/React Native/Android:
- All responses follow `ApiResponse<T>` wrapper
- JWT tokens work seamlessly with mobile HTTP clients
- FCM token accepted at login for push notifications
- Location-based APIs work with device GPS coordinates
- Emergency blood requests auto-notify nearby donors via Firebase

---

## 🔔 Firebase Setup (Push Notifications)
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create project → Project Settings → Service Accounts
3. Click **Generate new private key**
4. Replace `src/main/resources/firebase-service-account.json` with downloaded file
5. ⚠️ Add to `.gitignore` — never commit this file!
