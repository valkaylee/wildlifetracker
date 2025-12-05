/*
uncomment to reset database if needed
*/
/*
DROP DATABASE IF EXISTS wildlife;
CREATE DATABASE wildlife;
USE wildlife;
*/

-- ===============================
-- Create Database
-- ===============================
CREATE DATABASE IF NOT EXISTS wildlife;
USE wildlife;

-- ===============================
-- Users Table
-- Aligned with User.java JPA entity
-- ===============================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    bio VARCHAR(500),
    profile_picture_url VARCHAR(500),
    total_animals_logged INT NOT NULL DEFAULT 0,
    unique_species_count INT NOT NULL DEFAULT 0,
    last_activity_date DATETIME
);

-- ===============================
-- Species Table
-- Aligned with Species.java JPA entity
-- ===============================
CREATE TABLE species (
    species_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(100) NOT NULL
);

-- ===============================
-- Sightings Table
-- Aligned with Sighting.java JPA entity
-- ===============================
CREATE TABLE sightings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    species VARCHAR(255),
    location VARCHAR(255),
    description TEXT,
    image_url VARCHAR(255),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,

    CONSTRAINT fk_sightings_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

-- ===============================
-- Reports Table
-- Aligned with Report.java JPA entity
-- ===============================
CREATE TABLE reports (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sighting_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reports_sighting
        FOREIGN KEY (sighting_id) REFERENCES sightings(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reports_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

-- ===============================
-- Profiles Table
-- Aligned with Profile.java JPA entity
-- ===============================
CREATE TABLE profiles (
    profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    animals_logged BIGINT NOT NULL DEFAULT 0,
    species_seen BIGINT NOT NULL DEFAULT 0,
    assists BIGINT NOT NULL DEFAULT 0,
    most_frequent_area VARCHAR(255),

    CONSTRAINT fk_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

-- ===============================
-- Notifications Table
-- Aligned with Notification.java JPA entity
-- ===============================
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    user_id BIGINT,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);
