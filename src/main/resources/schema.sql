-- ============================================================
-- 🩸 Blood Bank Management System — MySQL Database Schema
-- ============================================================
-- Run this if you want to create tables manually
-- OR just run the Spring Boot app — JPA auto-creates them
-- ============================================================

CREATE DATABASE IF NOT EXISTS bloodbank_db;
USE bloodbank_db;

-- ============================================================
-- 1. USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    full_name       VARCHAR(150),
    phone_number    VARCHAR(15),
    role            ENUM('ROLE_ADMIN','ROLE_HOSPITAL','ROLE_USER','ROLE_DONOR') NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    fcm_token       VARCHAR(500),
    latitude        DOUBLE,
    longitude       DOUBLE,
    city            VARCHAR(100),
    state           VARCHAR(100),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- 2. HOSPITALS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS hospitals (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_name       VARCHAR(200) NOT NULL,
    registration_number VARCHAR(100) UNIQUE,
    email               VARCHAR(150) NOT NULL UNIQUE,
    phone_number        VARCHAR(15),
    address             VARCHAR(500),
    city                VARCHAR(100),
    state               VARCHAR(100),
    pincode             VARCHAR(10),
    latitude            DOUBLE,
    longitude           DOUBLE,
    is_active           BOOLEAN DEFAULT TRUE,
    user_id             BIGINT UNIQUE,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- 3. BLOOD_STOCKS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS blood_stocks (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_id     BIGINT NOT NULL,
    blood_group     ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE',
                         'O_POSITIVE','O_NEGATIVE','AB_POSITIVE','AB_NEGATIVE') NOT NULL,
    quantity        INT NOT NULL DEFAULT 0,
    units_available INT,
    last_updated    DATETIME,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_hospital_blood (hospital_id, blood_group),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE
);

-- ============================================================
-- 4. DONORS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS donors (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE,
    blood_group         ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE',
                             'O_POSITIVE','O_NEGATIVE','AB_POSITIVE','AB_NEGATIVE') NOT NULL,
    age                 INT,
    weight_kg           DOUBLE,
    last_donation_date  DATE,
    total_donations     INT DEFAULT 0,
    status              ENUM('AVAILABLE','DONATED_RECENTLY','NOT_AVAILABLE') DEFAULT 'AVAILABLE',
    latitude            DOUBLE,
    longitude           DOUBLE,
    city                VARCHAR(100),
    is_notify_enabled   BOOLEAN DEFAULT TRUE,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- 5. BLOOD_REQUESTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS blood_requests (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    requested_by        BIGINT NOT NULL,
    hospital_id         BIGINT,
    blood_group         ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE',
                             'O_POSITIVE','O_NEGATIVE','AB_POSITIVE','AB_NEGATIVE') NOT NULL,
    units_required      INT NOT NULL,
    patient_name        VARCHAR(150),
    patient_age         INT,
    reason              VARCHAR(500),
    is_emergency        BOOLEAN DEFAULT FALSE,
    status              ENUM('PENDING','APPROVED','REJECTED','FULFILLED','CANCELLED') DEFAULT 'PENDING',
    patient_latitude    DOUBLE,
    patient_longitude   DOUBLE,
    patient_address     VARCHAR(500),
    required_by         DATETIME,
    fulfilled_at        DATETIME,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requested_by) REFERENCES users(id),
    FOREIGN KEY (hospital_id)  REFERENCES hospitals(id) ON DELETE SET NULL
);

-- ============================================================
-- 6. NOTIFICATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    title               VARCHAR(200) NOT NULL,
    message             VARCHAR(1000) NOT NULL,
    is_read             BOOLEAN DEFAULT FALSE,
    notification_type   VARCHAR(50),
    reference_id        BIGINT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- SAMPLE DATA (Optional — for testing)
-- ============================================================

-- Admin user (password: admin123 — BCrypt encoded)
INSERT IGNORE INTO users (username, email, password, full_name, role, city, state)
VALUES (
    'admin',
    'admin@bloodbank.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh9S',
    'System Admin',
    'ROLE_ADMIN',
    'Chennai',
    'Tamil Nadu'
);

-- ============================================================
-- 7. AMBULANCES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS ambulances (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number              VARCHAR(20) NOT NULL UNIQUE,
    driver_name                 VARCHAR(100) NOT NULL,
    driver_phone                VARCHAR(15) NOT NULL,
    emergency_contact_name      VARCHAR(100),
    emergency_contact_phone     VARCHAR(15),
    hospital_id                 BIGINT,
    status                      ENUM('AVAILABLE','ON_DUTY','RETURNING','MAINTENANCE','OUT_OF_SERVICE') DEFAULT 'AVAILABLE',
    current_latitude            DOUBLE,
    current_longitude           DOUBLE,
    location_updated_at         DATETIME,
    assigned_request_id         BIGINT,
    is_active                   BOOLEAN DEFAULT TRUE,
    created_at                  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at                  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hospital_id)         REFERENCES hospitals(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_request_id) REFERENCES blood_requests(id) ON DELETE SET NULL
);

-- ============================================================
-- 8. LIVE_LOCATION_TRACKING TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS live_location_tracking (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT,
    ambulance_id     BIGINT,
    blood_request_id BIGINT,
    latitude         DOUBLE NOT NULL,
    longitude        DOUBLE NOT NULL,
    speed_kmh        DOUBLE,
    heading_degrees  DOUBLE,
    accuracy_meters  DOUBLE,
    address          VARCHAR(500),
    tracking_type    VARCHAR(50),
    recorded_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)          REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (ambulance_id)     REFERENCES ambulances(id) ON DELETE SET NULL,
    FOREIGN KEY (blood_request_id) REFERENCES blood_requests(id) ON DELETE SET NULL
);

-- ============================================================
-- 9. EMERGENCY_CONTACTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS emergency_contacts (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT NOT NULL,
    contact_name         VARCHAR(100) NOT NULL,
    contact_phone        VARCHAR(15) NOT NULL,
    relationship         VARCHAR(50),
    is_primary           BOOLEAN DEFAULT FALSE,
    notify_on_emergency  BOOLEAN DEFAULT TRUE,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- 10. BLOOD_EXPIRY TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS blood_expiry (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_id     BIGINT NOT NULL,
    blood_group     ENUM('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE',
                         'O_POSITIVE','O_NEGATIVE','AB_POSITIVE','AB_NEGATIVE') NOT NULL,
    units           INT NOT NULL,
    collection_date DATE,
    expiry_date     DATE NOT NULL,
    bag_number      VARCHAR(50),
    donor_name      VARCHAR(100),
    is_expired      BOOLEAN DEFAULT FALSE,
    is_discarded    BOOLEAN DEFAULT FALSE,
    discarded_at    DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE
);
