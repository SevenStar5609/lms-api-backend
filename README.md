# LMS API (HUTECH Learning Management System)

Đây là hệ thống RESTful API cho dự án Quản lý Học tập (Learning Management System - LMS). Hệ thống cung cấp các chức năng quản lý khóa học, người dùng, đăng ký học, theo dõi tiến độ, thi trắc nghiệm và cấp phát chứng chỉ.

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ:** Java 17+
- **Framework:** Spring Boot 3
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Bảo mật:** Spring Security & JWT (JSON Web Token)
- **Tài liệu API:** Swagger UI (Springdoc OpenAPI)
- **Quản lý phiên bản DB:** Flyway
- **Lưu trữ tệp tin:** Cloudinary (dành cho chứng chỉ PDF)

---

## 📋 Yêu cầu hệ thống

Trước khi chạy dự án, hãy đảm bảo máy của bạn đã cài đặt các phần mềm sau:

1. **Java Development Kit (JDK) 17** trở lên.
2. **PostgreSQL** (Phiên bản 12 trở lên).
3. **Maven** (Nếu không sử dụng Maven Wrapper đi kèm).

---

## ⚙️ Cấu hình biến môi trường

Dự án sử dụng file `application.properties` để cấu hình. Bạn có thể thay đổi các giá trị mặc định trực tiếp trong `src/main/resources/application.properties` hoặc thiết lập qua các biến môi trường của hệ điều hành.

Các thông số quan trọng cần lưu ý:

### 1. Cấu hình Cơ sở dữ liệu (PostgreSQL)
Mặc định hệ thống kết nối tới database có tên là `lms_db` ở cổng `5432`.
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/lms_db
spring.datasource.username=postgres
spring.datasource.password=123456
2. Cấu hình JWT (Json Web Token)
Đảm bảo bạn có chuỗi bí mật đủ mạnh để ký JWT (được cấu hình trong class JwtService hoặc cấu hình thêm nếu hệ thống yêu cầu).
3. Cấu hình lưu trữ tệp & Cloudinary (Cấp chứng chỉ)
Hệ thống sử dụng thư mục cục bộ uploads/ để lưu trữ tệp gốc hoặc có thể cấu hình dùng Cloudinary.
# Bật/tắt lưu trữ trên Cloudinary
cloudinary.enabled=false

# Nếu cloudinary.enabled=true, hãy cung cấp các thông tin sau:
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
🚀 Hướng dẫn Cài đặt & Chạy dự án
Bước 1: Chuẩn bị Cơ sở dữ liệu
Mở trình quản lý PostgreSQL (ví dụ pgAdmin hoặc psql) và tạo một database mới:
CREATE DATABASE lms_db;
Lưu ý: Bạn không cần tạo các bảng thủ công. Hệ thống sử dụng thư viện Flyway (src/main/resources/db/migration/) sẽ tự động chạy các script SQL để tạo bảng và quản lý cấu trúc database ngay khi ứng dụng khởi động.
Bước 2: Clone dự án và Cài đặt thư viện
Mở Terminal (Command Prompt / PowerShell) và di chuyển vào thư mục dự án:
# Clone dự án (nếu bạn dùng Git)
# git clone <repository-url>
# cd lms-api

# Tải các thư viện (Dependencies)
./mvnw clean install -DskipTests
(Nếu bạn dùng Windows PowerShell, có thể dùng ./mvnw.cmd clean install -DskipTests)
Bước 3: Chạy dự án
Bạn có thể chạy dự án bằng lệnh Maven:
./mvnw spring-boot:run
Hoặc nếu bạn dùng IDE (IntelliJ IDEA, Eclipse, VS Code), chỉ cần chạy file LmsApiApplication.java nằm trong thư mục src/main/java/vn/edu/hutech/lms_api/.
Bước 4: Kiểm tra ứng dụng hoạt động
Khi console thông báo "Started LmsApiApplication", ứng dụng đã khởi chạy thành công ở cổng 8080.
Bạn có thể truy cập Tài liệu API (Swagger UI) để xem và test các Endpoints tại địa chỉ: 👉 http://localhost:8080/swagger-ui/index.html
🧪 Chạy Kiểm thử (Unit Tests)
Dự án đã được viết sẵn một bộ kiểm thử API tự động (bao phủ các tính năng như Đăng ký khóa học, Trắc nghiệm, Cấp chứng chỉ, Dashboard,...).
Để thực thi tất cả các Test Cases, chạy lệnh:
./mvnw test
📂 Cấu trúc thư mục chính
src/main/java/vn/edu/hutech/lms_api/
 ├── config/        # Cấu hình Spring Security, OpenAPI, CORS...
 ├── controller/    # Các API Endpoints xử lý request HTTP
 ├── domain/        # Các Entity ánh xạ với Database (JPA)
 ├── dto/           # Data Transfer Objects cho Request/Response
 ├── exception/     # Xử lý lỗi toàn cục (Global Exception Handler)
 ├── repository/    # Cung cấp phương thức tương tác với DB (Spring Data JPA)
 ├── security/      # Các bộ lọc bảo mật, xử lý JWT Token
 └── service/       # Chứa logic nghiệp vụ lõi (Business Logic)

Bạn có thể thay đổi các giá trị ở mục Cloudinary hoặc thông tin đăng nhập PostgreSQL trong cấu hình cho phù hợp với môi trường thực tế của bạn nhé.
