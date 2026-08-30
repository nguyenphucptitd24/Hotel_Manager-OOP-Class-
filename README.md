# Hotel Management Database & Docker Setup

## 1. Khởi chạy Database bằng Docker

Yêu cầu: Đã cài đặt Docker Desktop.

Chạy lệnh sau tại thư mục gốc của repo:

```bash
docker compose up -d

```

## 2. Nạp dữ liệu tự động (Chỉ cần chạy 1 lần đầu)

```bash
docker exec -i hotel_sqlserver_container /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "OOP_Class_123!" -C -i /data/init.sql

```

## 3. Cấu hình kết nối Spring Boot (`application.properties`)

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=hotel_db;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=OOP_Class_123!
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Giữ nguyên cấu trúc và dữ liệu có sẵn, không tự động drop/tạo lại
spring.jpa.hibernate.ddl-auto=validate

```

## 4. Dữ liệu Master có sẵn

- **Tài khoản mặc định:** `admin` / `admin123` (Role: `ROLE_ADMIN`)
- **Tổng số bản ghi:** 7 loại phòng, 70 phòng, 36.275 khách hàng và đơn đặt phòng.
