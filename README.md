Đề tài: Đề tài số 8 - Xây dựng Backend API cho Hệ thống Quản lý Học tập Trực tuyến (LMS) Công nghệ sử dụng: Java 21, Spring Boot 4.0.6, PostgreSQL 16.13, Hibernate/JPA, Flyway, Maven. 

http://localhost:8080/swagger-ui/index.html


I. Giai đoạn 1: Phân tích Nghiệp vụ và Thiết kế Dữ liệu
Đã tiến hành phân tích luồng nghiệp vụ của một hệ thống LMS chuẩn và chốt cấu trúc Cơ sở dữ liệu (CSDL) với 10 bảng cốt lõi, bao gồm:
1.	Nhóm Quản lý Người dùng: users (Học viên, Giảng viên, Admin). 
2.	Nhóm Tổ chức Nội dung: courses (Khóa học), modules (Chương học), lessons (Bài học). 
3.	Nhóm Tiến độ & Ghi danh: enrollments (Ghi danh khóa học), lesson_progress (Tiến độ từng bài). 
4.	Nhóm Kiểm tra Đánh giá: quizzes (Bài kiểm tra), questions (Ngân hàng câu hỏi lưu bằng JSONB), attempts (Lịch sử làm bài). 
5.	Nhóm Chứng nhận: certificates (Lưu thông tin cấp phát chứng chỉ PDF).


II. Giai đoạn 2: Cài đặt Môi trường Database
1.	Đã tải và cài đặt thành công PostgreSQL phiên bản 16.13 (phiên bản ổn định, tương thích tốt với Spring Boot).
2.	Bỏ qua cài đặt Stack Builder để giữ môi trường máy chủ nhẹ nhàng.
3.	Truy cập công cụ quản lý giao diện pgAdmin 4, đăng nhập bằng tài khoản postgres (quyền siêu quản trị).
4.	Đã khởi tạo thành công một database trống có tên là lms_db để chuẩn bị cho việc mapping dữ liệu từ Spring Boot.



III. Giai đoạn 3: Khởi tạo và Cấu hình Project Spring Boot
1.	Khởi tạo mã nguồn (Spring Initializr):
o	Tạo project Maven, ngôn ngữ Java (phiên bản 21).
o	Phiên bản Spring Boot: 4.0.6.
o	Tên thư mục/Artifact: lms-api.
o	Đã tích hợp 7 Dependencies (Thư viện) cốt lõi: Spring Web, Spring Data JPA, PostgreSQL Driver, Validation, Spring Security, Flyway Migration, và Lombok. 
2.	Thiết lập IDE (IntelliJ IDEA):
o	Mở project thành công trên IntelliJ IDEA.
o	Xử lý thành công lỗi xung đột phiên bản Java (Cannot compile module for JVM target 21) bằng cách đồng bộ Project Structure và Java Compiler sang Oracle OpenJDK 21.
3.	Cấu hình kết nối CSDL:
o	Viết cấu hình trong file application.properties để ứng dụng kết nối qua cổng 5432 tới database lms_db.
o	Bật tính năng spring.jpa.hibernate.ddl-auto=validate để kiểm soát cấu trúc bảng an toàn.
4.	Kết quả:
o	Đã build và Run thành công file LmsApiApplication.java.
o	Máy chủ Tomcat đã khởi động thành công ở cổng 8080.
o	Thư viện Spring Security đã kích hoạt và tự động sinh mật khẩu bảo vệ mặc định.



IV. Giai đoạn 4: Định hướng Cấu trúc Thư mục (Bước tiếp theo)
Dựa trên kiến trúc mã nguồn tham khảo được yêu cầu (cấu trúc yoedu-api), bước tiếp theo dự án sẽ được phân bổ các packages trong thư mục src/main/java/vn/edu/hutech/lms_api/ theo đúng chuẩn thiết kế phân lớp, bao gồm:
•	common (Chứa các hằng số, tiện ích dùng chung)
•	config (Chứa file cấu hình hệ thống, CORS, Swagger)
•	controller (Tầng giao tiếp REST API xử lý HTTP Request)
•	domain hoặc entity (Tầng ánh xạ các bảng CSDL bằng JPA)
•	dto (Tầng đóng gói dữ liệu truyền tải Data Transfer Object)
•	repository (Tầng thao tác trực tiếp với Database)
•	security (Tầng xử lý phân quyền, mã hóa JWT)
•	service (Tầng xử lý logic nghiệp vụ chính)
Dưới đây là nội dung cập nhật cho Nhật ký tiến độ của bạn. Bạn có thể copy phần này và dán nối tiếp ngay bên dưới "Giai đoạn IV" trong file Word để giữ mạch báo cáo nhé:



V. Giai đoạn 5: Khởi tạo Database tự động (Migration) & Xây dựng Tầng Domain (Entity)
1.	Thiết lập Flyway Migration:
o	Tạo file script V1__Init_Database.sql trong thư mục src/main/resources/db/migration.
o	Viết mã DDL chuẩn PostgreSQL để khởi tạo 10 bảng dữ liệu cốt lõi có liên kết khóa ngoại (Foreign Key) chặt chẽ, bao gồm việc sử dụng kiểu dữ liệu JSONB tối ưu cho bảng questions và attempts. 
o	Chạy ứng dụng để Flyway tự động thực thi script, ánh xạ thành công 10 bảng vào CSDL lms_db.
o	Xử lý thành công sự cố gián đoạn kết nối Database (Connection Refused/Timeout) bằng cách cấu hình lại IP (127.0.0.1) và khởi động lại Service PostgreSQL trên Windows.
2.	Xây dựng Tầng Domain (ORM Mapping):
o	Tạo package domain theo chuẩn cấu trúc dự án mẫu yoedu-api.
o	Viết 10 Class Java tương ứng để ánh xạ 1-1 với 10 bảng trong PostgreSQL thông qua Spring Data JPA.
o	Sử dụng các Annotation chuẩn: @Entity, @Table, @Id, @Column, @ManyToOne, @JoinColumn.
o	Tích hợp Lombok (@Getter, @Setter, @Builder, @NoArgsConstructor) để tối ưu và làm sạch mã nguồn, giảm thiểu boilerplate code. 
o	Áp dụng @JdbcTypeCode(SqlTypes.JSON) của Hibernate để tự động chuyển đổi qua lại giữa kiểu JSONB (PostgreSQL) và đối tượng Map (Java).




VI. Giai đoạn 6: Hoàn thiện luồng API cốt lõi đầu tiên (Quản lý Khóa học)
Xây dựng thành công luồng xử lý hoàn chỉnh từ trên xuống dưới (Controller -> Service -> Repository -> Database) cho thực thể Course (Khóa học):
1.	Tầng Repository (repository):
o	Tạo CourseRepository và UserRepository kế thừa JpaRepository để tận dụng các hàm thao tác CSDL có sẵn của Spring Data JPA. 
2.	Tầng DTO - Data Transfer Object (dto):
o	Tạo CourseRequestDTO để hứng dữ liệu đầu vào. Tích hợp Bean Validation (@NotBlank) để kiểm tra tính hợp lệ của dữ liệu từ client. 
o	Tạo CourseResponseDTO để chuẩn hóa và ẩn giấu các thông tin nhạy cảm trước khi trả dữ liệu về cho client.
3.	Tầng Service (service & service.impl):
o	Áp dụng design pattern Interface - Implementation. Tạo CourseService (Interface) và CourseServiceImpl (Class).
o	Viết logic nghiệp vụ tạo khóa học: Kiểm tra sự tồn tại của giảng viên (instructorId) thông qua UserRepository, thực hiện chuyển đổi dữ liệu (Mapping) từ DTO sang Entity và lưu xuống Database.
4.	Tầng Controller (controller):
o	Tạo CourseController với annotation @RestController và định tuyến @RequestMapping("/api/v1/courses").
o	Mở 2 API Endpoint đầu tiên:
	POST /api/v1/courses: API tạo khóa học mới, có áp dụng @Valid để kích hoạt bắt lỗi đầu vào. 
	GET /api/v1/courses: API truy xuất danh sách toàn bộ khóa học hiện có.




VII. Giai đoạn 7: Tích hợp Swagger (OpenAPI) và Xử lý lỗi tập trung
1.	Xây dựng bộ bắt lỗi toàn cục (Global Exception Handling):
o	Tạo lớp GlobalExceptionHandler sử dụng annotation @RestControllerAdvice.
o	Bắt và xử lý chuẩn hóa các ngoại lệ phổ biến: MethodArgumentNotValidException (lỗi đầu vào từ @Valid, trả về 400 Bad Request) và RuntimeException (lỗi logic nghiệp vụ, trả về 404 Not Found).
o	Chuyển đổi các thông báo lỗi lỗi rườm rà của máy chủ thành định dạng JSON thân thiện, dễ đọc cho phía Client (Frontend/Mobile).
2.	Tự động hóa lưu vết thời gian (Auditing):
o	Tích hợp @CreationTimestamp và @UpdateTimestamp của Hibernate vào tầng Entity để hệ thống tự động điền giá trị cho các trường createdAt và updatedAt khi có thao tác INSERT/UPDATE, khắc phục triệt để tình trạng trả về giá trị null.
3.	Tích hợp giao diện tài liệu API (Swagger UI):
o	Thêm dependency springdoc-openapi-starter-webmvc-ui vào file pom.xml.
o	Tạo file cấu hình OpenApiConfig để thiết lập các metadata cho API (Tiêu đề, Phiên bản, Thông tin liên hệ).
4.	Xử lý sự cố (Troubleshooting) trong quá trình nâng cấp:
o	Vấn đề: Gặp lỗi NoSuchMethodError: ControllerAdviceBean (mã lỗi 500) do xung đột giữa cơ chế tự động quét lỗi của thư viện Springdoc (phiên bản cũ) và lõi framework Spring Boot 4.0.6.
o	Giải pháp: Đã xử lý triệt để bằng cách can thiệp vào file application.properties:
	Cấu hình springdoc.packages-to-scan=vn.edu.hutech.lms_api.controller để thu hẹp phạm vi quét, chỉ tập trung vào Controller.
	Bổ sung springdoc.override-with-generic-response=false để ngăn chặn thư viện tự động ghi đè cơ chế bắt lỗi của GlobalExceptionHandler.
o	Kết quả: Giao diện Swagger UI đã khởi chạy thành công tại địa chỉ http://localhost:8080/swagger-ui/index.html, cung cấp môi trường test API trực quan bằng đồ họa để chuẩn bị cho Chương 4 của báo cáo.




VIII. Giai đoạn 8: Xây dựng API Quản lý Nội dung (Chương học & Bài học)
1.	Phát triển chức năng Quản lý Chương học (Module):
o	Repository: Tạo ModuleRepository kế thừa JpaRepository, ứng dụng tính năng Derived Query Method (findByCourseIdOrderByOrderIndexAsc) để tự động hóa việc truy vấn danh sách chương học theo ID khóa học, có sắp xếp thứ tự.
o	DTO: Xây dựng ModuleRequestDTO tích hợp Bean Validation để ràng buộc dữ liệu đầu vào (bắt buộc nhập tên, số thứ tự, ID khóa học) và ModuleResponseDTO để trả về thông tin chi tiết (bao gồm cả tên khóa học liên kết).
o	Service & Controller: Triển khai ModuleService và ModuleController, hoàn thiện 2 API endpoint:
	POST /api/v1/modules: Thêm mới chương học, kèm logic kiểm tra sự tồn tại của khóa học.
	GET /api/v1/modules/course/{courseId}: Lấy danh sách toàn bộ chương học thuộc về một khóa học cụ thể.
o	Tối ưu cấu trúc dự án: Áp dụng mô hình kiến trúc "Package by Feature" cho tầng DTO. Phân hoạch lại thư mục dto thành các thư mục con (course, module) giúp mã nguồn có tính module hóa cao, dễ dàng mở rộng và bảo trì khi hệ thống phình to.
2.	Phát triển chức năng Quản lý Bài học (Lesson):
o	Repository: Tạo LessonRepository, viết method findByModuleIdOrderByOrderIndexAsc để lấy danh sách bài học theo ID chương học.
o	DTO: Thiết lập LessonRequestDTO (xử lý đa dạng loại nội dung như VIDEO, DOC, QUIZ) và LessonResponseDTO (ánh xạ lồng ghép tên chương học).
o	Service & Controller: Triển khai LessonService và LessonController cho 2 API cốt lõi:
	POST /api/v1/lessons: Thêm bài học mới vào một chương cụ thể.
	GET /api/v1/lessons/module/{moduleId}: Truy xuất danh sách bài học theo từng chương.
o	Kiểm thử tích hợp (Integration Test): Tiến hành chạy thử nghiệm thông qua giao diện Swagger UI. Xác nhận luồng dữ liệu liên kết 3 cấp độ (Course -> Module -> Lesson) hoạt động ổn định, dữ liệu courseTitle và moduleTitle được ánh xạ chính xác trong Response JSON.

