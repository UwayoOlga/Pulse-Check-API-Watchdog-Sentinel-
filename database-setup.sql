-- Create database for Pulse Check API
CREATE DATABASE pulse_check_db;

-- Connect to the database
\c pulse_check_db;

-- Create user (optional, you can use postgres user)
-- CREATE USER pulse_user WITH PASSWORD 'pulse_password';
-- GRANT ALL PRIVILEGES ON DATABASE pulse_check_db TO pulse_user;

-- The monitors table will be created automatically by Hibernate
-- But here's the expected schema for reference:

/*
CREATE TABLE monitors (
    device_id VARCHAR(255) PRIMARY KEY,
    timeout_seconds INTEGER NOT NULL,
    alert_email VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_heartbeat TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);
*/