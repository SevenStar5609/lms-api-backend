package vn.edu.hutech.lms_api.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfGenerationService {

    private final ResourceLoader resourceLoader;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Sử dụng ResourceLoader để tìm tệp phông chữ trong thư mục resources
    public PdfGenerationService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String generateCertificatePdf(String studentName, String courseName, String certCode) {
        String fileName = "CERT_" + certCode + ".pdf";
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            // Khởi tạo tài liệu A4 nằm ngang
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));

            document.open();

            // === PHẦN QUAN TRỌNG: CẤU HÌNH PHÔNG CHỮ UNICODE ===

            // 1. Tải tệp phông chữ TTF từ resources (Ví dụ bạn đã bỏ Arial.ttf vào src/main/resources/fonts/)
            Resource fontResource = resourceLoader.getResource("classpath:fonts/Arial.ttf");

            // 2. Tạo một BaseFont Unicode (nhớ có BaseFont.IDENTITY_H)
            BaseFont unicodeBaseFont = BaseFont.createFont(
                    fontResource.getFile().getAbsolutePath(),
                    BaseFont.IDENTITY_H, // BẮT BUỘC để hỗ trợ Unicode và hiển thị đúng tiếng Việt
                    BaseFont.EMBEDDED
            );

            // 3. Tạo các loại Font Unicode từ BaseFont trên
            Font titleFont = new Font(unicodeBaseFont, 30, Font.BOLD, java.awt.Color.BLUE);
            Font normalFont = new Font(unicodeBaseFont, 16, Font.NORMAL, java.awt.Color.BLACK);
            Font nameFont = new Font(unicodeBaseFont, 24, Font.BOLD, java.awt.Color.RED);
            Font courseFont = new Font(unicodeBaseFont, 20, Font.BOLD, java.awt.Color.BLACK);
            Font codeFont = new Font(unicodeBaseFont, 12, Font.NORMAL, java.awt.Color.GRAY);

            // ================================================

            // 1. Tiêu đề
            Paragraph title = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            // 2. Dòng giới thiệu
            Paragraph text1 = new Paragraph("This is to proudly certify that", normalFont);
            text1.setAlignment(Element.ALIGN_CENTER);
            text1.setSpacingAfter(15);
            document.add(text1);

            // 3. Tên học viên (Giờ đây Unicode sẽ hỗ trợ hiển thị đúng "LÊ HỮU HUY")
            Paragraph name = new Paragraph(studentName.toUpperCase(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingAfter(15);
            document.add(name);

            // 4. Lý do cấp
            Paragraph text2 = new Paragraph("has successfully completed the course:", normalFont);
            text2.setAlignment(Element.ALIGN_CENTER);
            text2.setSpacingAfter(15);
            document.add(text2);

            // 5. Tên khóa học
            Paragraph course = new Paragraph(courseName, courseFont);
            course.setAlignment(Element.ALIGN_CENTER);
            course.setSpacingAfter(50);
            document.add(course);

            // 6. Mã chứng chỉ ở góc phải
            Paragraph code = new Paragraph("Certificate Code: " + certCode, codeFont);
            code.setAlignment(Element.ALIGN_RIGHT);
            document.add(code);

            document.close();

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file PDF Chứng chỉ: " + e.getMessage());
        }
    }
}