-- ===============================
-- Create Database
-- ===============================
CREATE DATABASE IF NOT EXISTS wildlife;
USE wildlife;

-- ===============================
-- Users Table
-- ===============================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    notif_pref ENUM('EMAIL', 'SMS', 'NONE') DEFAULT 'EMAIL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- Species Table
-- ===============================
CREATE TABLE species (
    species_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(100) NOT NULL
);

-- ===============================
-- Sightings Table
-- ===============================
CREATE TABLE sightings (
    sighting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    species_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    img_url VARCHAR(255),
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    detection_radius FLOAT,
    status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_sightings_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_sightings_species
        FOREIGN KEY (species_id) REFERENCES species(species_id)
        ON DELETE CASCADE
);

-- ===============================
-- Reports Table
-- ===============================
CREATE TABLE reports (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sighting_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reports_sighting
        FOREIGN KEY (sighting_id) REFERENCES sightings(sighting_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reports_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- ===============================
-- Profiles Table
-- ===============================
CREATE TABLE profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    animals_logged BIGINT DEFAULT 0,
    species_seen BIGINT DEFAULT 0,
    assists BIGINT DEFAULT 0,
    most_frequent_area VARCHAR(255),

    CONSTRAINT fk_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- ===============================
-- Notifications Table
-- ===============================
CREATE TABLE notifications (
    notif_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    delivery_method ENUM('EMAIL', 'SMS', 'PUSH') NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    sighting_id BIGINT,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_notifications_sighting
        FOREIGN KEY (sighting_id) REFERENCES sightings(sighting_id)
        ON DELETE SET NULL
);
