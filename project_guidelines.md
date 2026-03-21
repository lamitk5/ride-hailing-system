# Kế Hoạch & Hướng Dẫn Phát Triển (Real-time Ride-Hailing Dispatch System)

Dựa trên phân tích từ tệp Word về cơ sở dữ liệu và sơ đồ ERD, tôi đề xuất thiết kế kiến trúc hệ thống, cấu trúc thư mục, và lộ trình phát triển chi tiết như sau:

---

## 1. Phân Tích Công Nghệ & Kiến Trúc (Tech Stack)

Hệ thống đặt xe công nghệ yêu cầu khả năng xử lý bất đồng bộ cao, quản lý vị trí địa lý theo thời gian thực (Geospatial data) và xử lý giao dịch an toàn (Race Condition/Optimistic Locking). Dưới đây là Tech Stack đề xuất lý tưởng nhất:

- **Backend Framework:** Java Spring Boot hoặc Node.js (NestJS). Khuyến nghị **Spring Boot** do hỗ trợ rất mạnh về dữ liệu không gian (Hibernate Spatial), Data JPA, và Security.
- **Database chính:** PostgreSQL kèm extension **PostGIS** BẮT BUỘC. (Giúp lưu và tính toán `GEOMETRY(Point, 4326)`, `GEOMETRY(LineString, 4326)` cực kỳ nhanh chóng).
- **In-Memory Cache & Real-time Location:** Redis (Dùng `GEOADD`, `GEORADIUS` để quản lý vị trí tài xế theo thời gian thực thay vì update vào DB liên tục).
- **Message Broker (Queue):** Apache Kafka hoặc RabbitMQ (Dùng để đưa các request "đặt xe", "tính toán lộ trình", "cộng/trừ tiền" vào hàng đợi, tránh quá tải server).
- **Real-time Communication:** WebSocket / Socket.io (Dùng để đẩy dữ liệu vị trí tài xế cho khách hàng và đẩy thông báo có khách cho tài xế).
- **Kiến trúc:** Modular Monolith (Phân chia module rõ ràng theo Domain-Driven Design). Dễ dàng mở rộng lên Microservices sau này.

---

## 2. Cấu Trúc Thư Mục Dự Án (Modular Monolith)

ride-hailing-system/
├── src/
│ ├── main/
│ │ ├── java/com/ridehailing/
│ │ │ ├── RideHailingApplication.java
│ │ │ ├── config/ # Cấu hình hệ thống (Security, PostGIS, Redis, Kafka, WebSocket)
│ │ │ ├── common/ # Lớp dùng chung (Exceptions, Constants, BaseEntity, Utils)
│ │ │ ├── modules/ # Các Module nghiệp vụ cốt lõi
│ │ │ │ ├── account/ # Quản lý tài khoản (Account, JWT Authentication, Role)
│ │ │ │ ├── user/ # Quản lý hồ sơ Khách hàng (User)
│ │ │ │ ├── driver/ # Quản lý hồ sơ & trạng thái Tài xế (Driver)
│ │ │ │ ├── vehicle/ # Quản lý xe và bảng giá xe (Vehicle, VehicleType)
│ │ │ │ ├── ride/ # Core System: Dispatching, Đặt xe, Trạng thái (Ride)
│ │ │ │ ├── location/ # WebSocket Tracking, Lưu vết lộ trình (RideTrajectory)
│ │ │ │ └── payment/ # Quản lý ví, thanh toán (Payment)
│ │ │ └── infrastructure/ # Kết nối External API (Google Maps API, VNPay API)
│ │ └── resources/
│ │ ├── application.yml # File cấu hình môi trường
│ │ └── db/migration/ # Flyway scripts: Cấu trúc bảng và dữ liệu mẫu (V1\_\_init.sql)
├── docker-compose.yml # File để dựng nhanh PostgreSQL+PostGIS, Redis, Kafka ở local
├── pom.xml / build.gradle # Quản lý thư viện
└── README.md # Tài liệu hướng dẫn cài đặt dự án

```

ride/
├── controller/        # REST APIs (Nhận HTTP Request) và WebSocket Controllers
├── service/           # Chứa logic nghiệp vụ phức tạp (Business DB Logic)
├── repository/        # Tương tác với Database (Spring Data JPA)
├── entity/            # Các Model map với table (Ride). Cần cấu hình @Version cho Optimistic Lock
├── dto/               # DTOs (Data Transfer Objects) request/response validate dữ liệu
└── event/             # Trình phát/lắng nghe các sự kiện (Vd: RideRequestedEvent)

---

## 3. Lộ Trình Triển Khai (Roadmap) & Workflow Hệ Thống

Dưới đây là các bước để xây dựng dự án một cách an toàn và chuẩn xác nhất:

### Giai Đoạn 1: Thiết Lập Core & Database (Nền tảng)
1. Cài đặt Docker, chạy file `docker-compose.yml` để dựng PostgreSQL cài sẵn PostGIS, Redis.
2. Viết file Migration (Flyway/Liquibase) để tự động tạo toàn bộ bảng như trong tài liệu (accounts, users, drivers, vehicles, rides...).
3. *Lưu ý quan trọng:* Cột `version` trong bảng `rides` bắt buộc ánh xạ vào biến `@Version` trong JPA để khởi động tính năng Optimistic Lock.

### Giai Đoạn 2: Xác thực & Phân quyền (Auth & IAM)
1. Code module `account`, triển khai JWT Authentication.
2. Tách rõ ràng 3 luồng đăng nhập: Client App (Customer), Driver App (Tài xế), Admin Portal.
3. Liên kết `ACCOUNT` 1-1 với `USER` và `DRIVER` lúc đăng ký.

### Giai Đoạn 3: Cấu hình bảng giá (Vehicle & Pricing)
1. Thêm dữ liệu cứng vào bảng `vehicle_types` (GrabBike, GrabCar...).
2. Viết API chuẩn bị sẵn thuật toán ước tính tiền (`base_fare + distance * price_per_km`) dựa theo kết quả khoảng cách. Nên tích hợp Google Maps API (Distance Matrix) tại đây để tính `estimated_distance` chính xác theo đường bộ.

### Giai Đoạn 4: Trái Tim Hệ Thống (Điều phối - Dispatch Engine) 🚀
Đây là phần khó và ăn tiền nhất:
1. **Quản lý Tài Xế:** Tài xế khi bật app, cứ mỗi 3-5 giây sẽ POST tọa độ GPS lên server. Server KHÔNG lưu xuống DB relational ngay, mà sẽ lưu vào Redis Geo (Lệnh `GEOADD drivers_location`).
2. **Khách hàng gọi xe:**
   * Tạo bản ghi `rides` với trạng thái `REQUESTED`, tài xế `driver_id` đang `NULL`.
   * Server dùng `GEORADIUS` trên Redis để quét tìm top 5 tài xế gần nhất (< 2km) có `status = AVAILABLE`.
3. **Phát Cuốc (Broadcasting):** Gửi event qua WebSocket tới client của 5 tài xế kia.
4. **Xử lý Race Condition:** Vì 5 tài xế đều thấy Notification nổ cuốc, nếu 2 người cùng bấm MỞ nhận chuyến đồng thời -> Lỗi Optimistic Lock phát huy tác dụng. Người bấm đầu tiên (vài mili-giây) sẽ update version DB từ `0` -> `1`, hệ thống lưu `driver_id` của họ vào `rides`. Người bấm thứ 2 sẽ bị DB từ chối (`OptimisticLockingFailureException`), server báo lỗi "Cuốc đã bị người khác nhận".

### Giai Đoạn 5: Tracking Di Chuyển & Thanh Toán
1. Khi cuốc chuyển sang `IN_PROGRESS`, tọa độ tài xế tiếp tục được gửi lên qua WebSocket định kỳ và Append vào tọa độ dạng chuỗi array.
2. Khi tài xế ấn "Kết thúc chuyến" (`COMPLETED`), mảng array tọa độ đó sẽ gộp lại thành 1 chuỗi `LineString` duy nhất (GEOMETRY) và lưu xuống bảng `ride_trajectories`.
3. Khóa cứng giá tiền `actual_fare` lại như thuyết minh trong Word.
4. Chuyển tiếp luồng logic sang Event tính tiền thanh toán (Payment).

## 💡 Lời Khuyên (Best Practices) Dành Cho Bạn
* **Quản lý Geometry:** Các hệ tọa độ GPS có SRID là 4326. Khi tính khoảng cách trong PostGIS, đừng viết lệnh tính khoảng cách nhẩm theo độ, hãy ép kiểu đường đi: `ST_Distance(point1::geography, point2::geography)` để trả về đúng đơn vị **Mét**.
* **Micro-commit:** Hãy Commit theo từng Phase ở trên để dễ trace lại lỗi. Không code toàn bộ rồi mới test. Mọi phase kết thúc phải có Unit Test kèm theo.
* **Mở rộng tương lai (Kafka):** Ở GĐ4, khi ứng dụng có dấu hiệu hàng chục ngàn Request/s, hãy nhét chuỗi "Khách gửi request -> Tìm tài xế" vào hàng đợi Kafka thay vì xử lý Synchronous (đồng bộ) trên API trực tiếp.
```
