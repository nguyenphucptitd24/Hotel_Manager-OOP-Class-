-- ==========================================================
-- 1. KHỞI TẠO CƠ SỞ DỮ LIỆU
-- ==========================================================
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'hotel_db')
BEGIN
    CREATE DATABASE hotel_db;
END
GO

USE hotel_db;
GO

-- ==========================================================
-- 2. DỌN DẸP CÁC BẢNG CŨ (NẾU ĐÃ TỒN TẠI)
-- ==========================================================
IF OBJECT_ID('booking_details', 'U') IS NOT NULL DROP TABLE booking_details;
IF OBJECT_ID('bookings', 'U') IS NOT NULL DROP TABLE bookings;
IF OBJECT_ID('rooms', 'U') IS NOT NULL DROP TABLE rooms;
IF OBJECT_ID('room_types', 'U') IS NOT NULL DROP TABLE room_types;
IF OBJECT_ID('customers', 'U') IS NOT NULL DROP TABLE customers;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;
IF OBJECT_ID('roles', 'U') IS NOT NULL DROP TABLE roles;
GO

-- ==========================================================
-- 3. TẠO CẤU TRÚC CÁC BẢNG
-- ==========================================================
CREATE TABLE roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE room_types (
    id INT PRIMARY KEY,
    name NVARCHAR(50) NOT NULL,
    base_price DECIMAL(12, 2) NOT NULL,
    capacity INT NOT NULL,
    description NVARCHAR(MAX)
);

CREATE TABLE rooms (
    id INT PRIMARY KEY,
    room_number NVARCHAR(10) NOT NULL UNIQUE,
    room_type_id INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'AVAILABLE',
    floor INT NOT NULL,
    CONSTRAINT fk_rooms_type FOREIGN KEY (room_type_id) REFERENCES room_types(id)
);

CREATE TABLE customers (
    id INT PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    identity_card NVARCHAR(50),
    phone NVARCHAR(50),
    email NVARCHAR(100),
    country NVARCHAR(50)
);

CREATE TABLE bookings (
    id INT PRIMARY KEY,
    booking_code NVARCHAR(50) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    user_id INT,
    status NVARCHAR(20) DEFAULT 'CONFIRMED',
    booking_date NVARCHAR(50),
    total_deposit DECIMAL(12, 2) DEFAULT 0,
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE booking_details (
    id INT PRIMARY KEY,
    booking_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_expected NVARCHAR(50) NOT NULL,
    check_out_expected NVARCHAR(50) NOT NULL,
    check_in_actual NVARCHAR(50),
    check_out_actual NVARCHAR(50),
    price_per_night DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_booking_details_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_details_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);
GO

-- ==========================================================
-- 4. CHÈN DỮ LIỆU MẪU BAN ĐẦU
-- ==========================================================
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_STAFF');
INSERT INTO users (username, password_hash, full_name, role_id) 
VALUES ('admin', 'admin123', N'Quản Trị Viên', 1);
GO

-- ==========================================================
-- 5. BULK INSERT TƯƠNG THÍCH HOÀN TOÀN LINUX DOCKER
-- ==========================================================
BULK INSERT room_types
FROM '/data/room_types_import.csv'
WITH (
    FORMAT = 'CSV',
    FIRSTROW = 2,
    FIELDTERMINATOR = ',',
    ROWTERMINATOR = '0x0a',
    TABLOCK
);

BULK INSERT rooms
FROM '/data/rooms_import.csv'
WITH (
    FORMAT = 'CSV',
    FIRSTROW = 2,
    FIELDTERMINATOR = ',',
    ROWTERMINATOR = '0x0a',
    TABLOCK
);

BULK INSERT customers
FROM '/data/customers_import.csv'
WITH (
    FORMAT = 'CSV',
    FIRSTROW = 2,
    FIELDTERMINATOR = ',',
    ROWTERMINATOR = '0x0a',
    TABLOCK
);

BULK INSERT bookings
FROM '/data/bookings_import.csv'
WITH (
    FORMAT = 'CSV',
    FIRSTROW = 2,
    FIELDTERMINATOR = ',',
    ROWTERMINATOR = '0x0a',
    TABLOCK
);

BULK INSERT booking_details
FROM '/data/booking_details_import.csv'
WITH (
    FORMAT = 'CSV',
    FIRSTROW = 2,
    FIELDTERMINATOR = ',',
    ROWTERMINATOR = '0x0a',
    TABLOCK
);
GO

-- ==========================================================
-- 6. KIỂM TRA SỐ LƯỢNG BẢN GHI
-- ==========================================================
SELECT 'room_types' AS Table_Name, COUNT(*) AS Total_Records FROM room_types
UNION ALL
SELECT 'rooms', COUNT(*) FROM rooms
UNION ALL
SELECT 'customers', COUNT(*) FROM customers
UNION ALL
SELECT 'bookings', COUNT(*) FROM bookings
UNION ALL
SELECT 'booking_details', COUNT(*) FROM booking_details;
GO