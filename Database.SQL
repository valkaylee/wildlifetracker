-- uncomment the following line to reset datbase
-- DROP DATABASE IF EXISTS wildlife;

-- ===============================
-- Create Database
-- ===============================
CREATE DATABASE IF NOT EXISTS wildlife;
USE wildlife;

-- ===============================
-- Users Table
-- ===============================
-- Primary table for user accounts and authentication
-- Also stores profile information (denormalized for simplicity)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    
    -- Profile fields
    display_name VARCHAR(100),
    bio VARCHAR(500),
    profile_picture_url LONGTEXT,
    
    -- Statistics (calculated from sightings)
    total_animals_logged INT NOT NULL DEFAULT 0,
    unique_species_count INT NOT NULL DEFAULT 0,
    last_activity_date DATETIME,
    
    INDEX idx_username (username)
);

-- ===============================
-- Sightings Table
-- ===============================
-- Wildlife sightings reported by users
-- Species stored as VARCHAR (not normalized) for flexibility
-- Images stored in image_url field and displayed in user's photo gallery on profile page
CREATE TABLE sightings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    species VARCHAR(255),
    location VARCHAR(255),
    description TEXT,
    image_url LONGTEXT,  
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Map coordinates (pixel positions on campus map)
    pixel_x INT,
    pixel_y INT,
    
    -- Foreign key to user
    user_id BIGINT,
    
    CONSTRAINT fk_sightings_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE SET NULL,
    
    INDEX idx_species (species),
    INDEX idx_location (location),
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp)
);

-- ===============================
-- Species Table (Optional - for future use)
-- ===============================
-- Normalized species reference table
-- Currently not used by application, but available for future enhancement
CREATE TABLE species (
    species_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(100) NOT NULL,
    
    INDEX idx_name (name),
    INDEX idx_category (category)
);

-- ===============================
-- Profiles Table (Optional - for future use)
-- ===============================
-- Separate profile statistics table
-- Entity exists in code but currently profile data is stored in users table
CREATE TABLE profiles (
    profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    animals_logged BIGINT DEFAULT 0,
    species_seen BIGINT DEFAULT 0,
    assists BIGINT DEFAULT 0,
    most_frequent_area VARCHAR(255),

    CONSTRAINT fk_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

