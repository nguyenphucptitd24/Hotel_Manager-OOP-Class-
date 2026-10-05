# Hotel Management Database & Docker Setup
## Khởi chạy 
Yêu cầu 2 terminal
cd hotel-frontend
```bash
npm run dev
```
cd springboot 
```bash
./mvnw spring-boot:run 
```
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

- **Tài khoản mặc định:** `admin` / `admin123` (Role: `ROLE_ADMIN`), `staff01` / `123456` (Role: `ROLE_STAFF`)
- **Tổng số bản ghi:** 7 loại phòng, 70 phòng, 36.275 khách hàng và đơn đặt phòng.
## 5. Customer Management & Stay Profiles

### 5.1. Quản lý khách hàng

API quản lý và tìm kiếm khách hàng:

`GET /api/v1/customers`

Hỗ trợ tìm kiếm khách hàng theo nhiều tiêu chí:

- Họ và tên
- Số điện thoại
- CCCD/CMND/Hộ chiếu
- Email
- Quốc gia

API hỗ trợ phân trang và sắp xếp dữ liệu.

**Tham số:**

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `keyword` | String | Từ khóa tìm kiếm |
| `page` | Integer | Số trang, bắt đầu từ `0` |
| `size` | Integer | Số lượng khách hàng trên mỗi trang |
| `sort` | String | Trường dùng để sắp xếp |

**Ví dụ:**

```http
GET /api/v1/customers?keyword=Nguyen&page=0&size=10
```

### 5.2. Xem lịch sử đặt phòng của khách hàng

API lấy lịch sử đặt phòng của một khách hàng:

`GET /api/v1/customers/{customerId}/bookings`

API hỗ trợ phân trang.

**Tham số:**

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `customerId` | Integer | ID của khách hàng |
| `page` | Integer | Số trang, bắt đầu từ `0` |
| `size` | Integer | Số lượng booking trên mỗi trang |
| `sort` | String | Trường dùng để sắp xếp |

**Ví dụ:**

```http
GET /api/v1/customers/1/bookings?page=0&size=10
```

Thông tin lịch sử đặt phòng bao gồm:

- Mã đặt phòng
- Ngày đặt
- Tiền đặt cọc
- Trạng thái đặt phòng

### 5.3. Backend

Phần Customer Management được triển khai theo mô hình:

```text
CustomerController
       ↓
CustomerService
       ↓
CustomerRepository / BookingRepository
       ↓
SQL Server
```

Các thành phần chính:

- `CustomerController`: cung cấp REST API quản lý khách hàng và lịch sử booking.
- `CustomerService`: xử lý nghiệp vụ và chuyển đổi Entity sang DTO.
- `CustomerRepository`: tìm kiếm khách hàng theo nhiều tiêu chí và hỗ trợ pagination.
- `BookingRepository`: truy vấn lịch sử booking theo `customerId`.
- `CustomerResponse`: DTO chứa thông tin khách hàng.
- `BookingHistoryResponse`: DTO chứa thông tin lịch sử đặt phòng.

### 5.4. Frontend

Trang **Customer Management** cung cấp:

- Tìm kiếm khách hàng theo nhiều tiêu chí.
- Hiển thị danh sách khách hàng dạng bảng.
- Phân trang danh sách khách hàng.
- Xem lịch sử đặt phòng của từng khách hàng.
- Hiển thị lịch sử booking trong popup.
