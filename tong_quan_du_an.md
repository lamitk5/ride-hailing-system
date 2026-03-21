# Tổng Quan & Giải Thích Chi Tiết Cấu Trúc Code Dự Án Ride-Hailing System

Tài liệu này giải thích chi tiết toàn bộ mã nguồn có trong dự án, giúp bạn hiểu rõ bản chất, cấu trúc, và luồng hoạt động của hệ thống đặt xe đa dịch vụ.

## 1. Kiến Trúc Tổng Thể (Architecture Overview)
Dự án được xây dựng theo kiến trúc **Modular Monolith** sử dụng framework **Spring Boot** (Java). Hệ thống sử dụng các công nghệ chính:
- **PostgreSQL + PostGIS**: Lưu trữ dữ liệu quan hệ và tính toán vị trí địa lý (GIS Spatial Data) nhanh chóng.
- **Redis**: Đóng vai trò In-memory cache và lưu trữ vị trí tài xế theo thời gian thực (sử dụng Redis GEO operations: GEOADD, GEORADIUS) thay vì ghi liên tục vào database.
- **WebSocket**: Dùng để đẩy tọa độ tài xế lên giao diện khách hàng và đẩy "cuốc xe" (ride requests) cho tài xế theo thời gian thực.
- **JWT (JSON Web Token)**: Để xác thực và phân quyền cho người dùng một cách an toàn (Stateless - không lưu session).

## 2. Giải Thích Các Tệp Tin Bên Ngoài (`Root Directory`)
- **`pom.xml`**: Tệp Maven quản lý vòng đời dự án và định nghĩa các thư viện (dependencies) cần thiết (Spring Web, Spring Data JPA, PostgreSQL, Hibernate Spatial, Redis, WebSocket, JWT,... ).
- **`docker-compose.yml`**: Tệp Docker dùng để khởi chạy nhanh các dịch vụ hạ tầng gồm cơ sở dữ liệu nền (PostgreSQL có sẵn PostGIS extension) và Redis server.
- **`test-websocket.html`**: Một file HTML tĩnh viết bằng Javascript dùng để test trực tiếp trên trình duyệt giao thức thông báo đẩy điểm tọa độ qua Websocket của hệ thống (giúp dễ debug dưới local).
- **`project_guidelines.md`**: Bản ghi chép lộ trình phát triển và các thông số kỹ thuật (Spec) làm kim chỉ nam của toàn bộ dự án.

## 3. Giải Thích Mã Nguồn Cốt Lõi (`src/main/java/com/ridehailing/`)
Ứng dụng bắt đầu từ **`RideHailingApplication.java`**: Điểm neo kết nối khởi chạy toàn bộ ứng dụng Spring Boot.

### 3.1. Thư mục Cấu hình (`config/`)
Gồm các cài đặt hệ thống, kết nối cơ sở hạ tầng mạng.
- **`RedisConfig.java`**: Khởi tạo kết nối (`RedisTemplate`) để thao tác với Redis cache (đặc biệt là tính năng Geo location lưu vị trí tài xế).
- **`WebSocketConfig.java`**: Thiết lập và đăng ký các endpoint WebSocket, cho phép nhận và truyền tọa độ tài xế/notification đi xe.
- **`security/SecurityConfig.java`**: Quy định bảo mật Web (Spring Security). Phân rã các API công khai (đăng nhập/đăng ký) và các API cần xác thực.
- **`security/JwtAuthenticationFilter.java` & `JwtTokenProvider.java`**: Bộ lọc (Filter) kiểm tra trên mỗi request có gắn token hợp lệ (JWT) không và logic để sinh, giải mã, xác minh Token.

### 3.2. Thư mục Tiện ích chung (`common/`)
- **`entity/BaseEntity.java`**: Lớp chứa các cột mặc định luôn cần như `createdAt`, `updatedAt` để các bảng khác kế thừa (Mô hình DRY - Do not Repeat Yourself).
- **`exception/GlobalExceptionHandler.java`**: Controller Advice. Nơi chứa sự kiện dọn dẹp lỗi tập trung. Khi code ném lỗi (`BadRequestException`, `ResourceNotFoundException`), lớp này "hứng" lỗi và chuyển đổi thành định dạng JSON chuẩn hoá ({status, message}) báo về Frontend.

### 3.3. Các Phân Hệ Nghiệp Vụ (`modules/`)
Được chia theo mô hình Domain-Driven Design (DDD), mỗi module sẽ chứa 4 tầng rõ ràng: `controller` (HTTP Routing), `service` (Logic), `repository` (Database truy vấn), và `entity/dto` (Data).

**A. `account` (Tài khoản & Xác thực)**
- Các file: `Account.java`, `AuthService.java`, `AuthController.java`,...
- Mục tiêu: Lưu thông tin cấu trúc User cơ sở (username/password/role). Xử lý quá trình đăng ký, mã hoá mật khẩu, đăng nhập (`LoginRequestDTO`) cấp phát trả về Token (`LoginResponseDTO`).

**B. `user` (Khách hàng)**
- Các file: `User.java`, `UserService.java`, `UserController.java`.
- Cấu trúc hồ sơ 1-1 với Account để lưu thông tin cụ thể (Họ Tên, SĐT, Địa chỉ) của Khách hàng, phục vụ cho việc gọi và hiển thị trên màn hình xác nhận thông tin.

**C. `driver` (Tài xế)**
- Các file: `Driver.java`, `DriverService.java`, `DriverController.java`.
- Hồ sơ của tài xế. Quan trọng nhất là lưu trữ thông tin về biến trạng thái (Status) như (AVAILABLE - Rảnh rỗi, ON_RIDE - Đang có khách, OFFLINE - Tắt máy).

**D. `vehicle` (Quản lý Phương Tiện & Giá Cuốc)**
- Các file: `Vehicle.java` và `VehicleType.java`, cùng các service tương ứng.
- Phân biệt giữa chiếc xe vật lý (biển số, loại xe của bác tài nọ) và Hạng Xe hệ thống quy định (GrabCar 4 chỗ, GrabBike 2 chỗ). Bảng này quyết định thuật toán tính tiền do có trường lưu `base_fare` (phí khởi điểm) và phí trên mỗi KM (`price_per_km`).

**E. `ride` (Điểm Cốt Lõi - Điều Phối Cuốc Xe)**
- Các file: `Ride.java`, `RideService.java`, `RideController.java`.
- `Ride (Cuốc xe)` chính là mô hình chứa "Mã Khách", "Mã Tài xế", điểm đón (pickup), điểm đến (dropoff), Trạng thái chuyến đi, chi phí (fare). 
- **Business Logic Nổi Bật** tại `RideService`: Cơ chế chống Race-Condition bằng `Optimistic Locking` (cột `@Version`). Khi cuốc nổ (bắn cho 5 người), nếu 2 người ấn vào cùng một thời điểm, chỉ người đầu tiên sửa được version trong Datebase làm thay đổi trạng thái cuốc (`driver_id`), DB sẽ báo lỗi khóa Optimistic và hệ thống từ chối người thứ hai một cách mượt mà.

**F. `location` (Theo Dõi Định Vị Địa Lý - Tracking)**
- Các file: `LocationController.java`, `LocationWebSocketController.java`, `DriverLocationService.java`, `RideTrajectory.java`.
- Các Controller nhận liên tục vị trí POST và đẩy sang Redis bằng `DriverLocationService`. Tại service này có logic tính toán bán kính `GEORADIUS` lấy danh sách xe rảnh trong phạm vi (vd: 2 KM).
- Sau khi cuốc đi thành công, mọi toạ độ trong suốt chuyến xe được lưu gộp thành dòng dữ liệu duy nhất `RideTrajectory` để dùng chuẩn vẽ `LineString` - vẽ lại đường line chi tiết đã đi (Bản đồ Lịch sử).

**G. `payment` (Thanh toán Giao Dịch)**
- Các file: `Payment.java`, `PaymentService.java`.
- Gắn chặt với cuối luồng khi `Ride` xong (COMPLETED). Sinh giao dịch chuyển tiền dựa theo `actual_fare` đã chốt và chuyển hướng đến các hàm API lưu ví.

## 4. Bức Tranh Tổng Thể (Luồng Hoạt Động Của Hệ Thống)
Bạn có thể hệ thống hóa chuỗi hoạt động của hệ thống theo 6 bước như sau:
1. **Lên sóng:** Tài xế mở app, tự động bắn tọa độ (`location module` thao tác với Redis). Tài xế báo AVAILABLE.
2. **Khách đặt:** App khách hiển thị xe gần nhất (cũng lấy từ `location module`). Khách nhấn "Đặt xe" => API gửi đến `ride module`. `RideService` tạo bản ghi và lưu lại vào DB PostgreSQL.
3. **Phát cuốc:** Server gom lấy 5 bác tài gần nhất gửi Notification qua `WebSocket`.
4. **Tranh cuốc:** Các bác tài đồng loạt bấm nhận. Optimistic Lock trên bảng `Ride` sẽ được check, chỉ cho 1 bác tài chốt đơn thành công. Module cập nhật DB Ride có người nhận chuyển status -> ACCEPTED.
5. **Di chuyển:** Tài xế tới đón -> đổi trạng thái ON_RIDE. Ứng dụng liên tục nhả JSON Tọa độ lên WebSocket chuyển tiếp về tay khách hàng qua Pub/Sub Redis. Lộ trình được record lại.
6. **Kết thúc:** Tới trạm, đổi trạng thái COMPLETED. Lưu vết `RideTrajectory` bằng PostGIS, chuyển giao dịch sang `payment module` để trừ tiền và giải phóng tài xế quay lại rảnh (AVAILABLE).
