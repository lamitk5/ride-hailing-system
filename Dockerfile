# BƯỚC 1: XÂY DỰNG
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Tách file pom.xml ra copy vào trước
COPY pom.xml .

# 2. KHÚC ĂN TIỀN LÀ ĐÂY: Bảo Maven lên mạng tải trước toàn bộ thư viện về nhét vào Cache của Docker.
# (Về sau, miễn là không sửa file pom.xml, bước tải mạng siêu nặng này sẽ được đi máy bay trong 0.1 giây)
RUN mvn dependency:go-offline

# 3. Kéo mã nguồn (src) vào. 
COPY src ./src

# 4. Khi này thư viện đã có sẵn ở dòng 2, Code sẽ được đóng gói bằng tốc độ ánh sáng (khoảng 10 giây).
RUN mvn clean package -DskipTests

# BƯỚC 2: KHỞI CHẠY (Mượn 1 cái máy ảo cực kỳ nhẹ chỉ chứa mỗi rèn chạy Java)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Sang cái máy ảo BƯỚC 1, chôm cái cục .jar vừa làm xong ném vào máy ảo này
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 cho bên ngoài gọi vào
EXPOSE 8080

# Chạy ứng dụng Spring Boot!
ENTRYPOINT ["java", "-jar", "app.jar"]
