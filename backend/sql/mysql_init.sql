-- MySQL initialization script for delivery applications
-- Run on your MySQL server to create the database and table

CREATE DATABASE IF NOT EXISTS madfood CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE madfood;

CREATE TABLE IF NOT EXISTS delivery_applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  phone VARCHAR(50) NOT NULL,
  source VARCHAR(255),
  photo_path VARCHAR(500),
  license_path VARCHAR(500),
  rc_path VARCHAR(500),
  aadhar_path VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
