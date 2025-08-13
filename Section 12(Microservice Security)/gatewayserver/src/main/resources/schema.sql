CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    keycloak_id VARCHAR(255) NOT NULL,

    username VARCHAR(100) NOT NULL UNIQUE,
--    password VARCHAR(255), -- optional if relying fully on Keycloak

    email VARCHAR(150) NOT NULL UNIQUE,
    mobile_number VARCHAR(20),
    aadhaar_number VARCHAR(12) UNIQUE,
    pan_number VARCHAR(10) UNIQUE,

    first_name VARCHAR(100),
    last_name VARCHAR(100),
    gender VARCHAR(10),
    date_of_birth DATE,

    address_line1 VARCHAR(255),
--    address_line2 VARCHAR(255),
--    city VARCHAR(100),
--    state VARCHAR(100),
--    pincode VARCHAR(10),
--    country VARCHAR(100) DEFAULT 'India',

--    occupation VARCHAR(100),
--    annual_income DECIMAL(15,2),
--    marital_status VARCHAR(20),

    role VARCHAR(50),
    kyc_status VARCHAR(50) DEFAULT 'pending',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
