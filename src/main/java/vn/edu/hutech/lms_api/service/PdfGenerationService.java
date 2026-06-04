package vn.edu.hutech.lms_api.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationService {

    private static final Color NAVY = new Color(21, 45, 85);
    private static final Color GOLD = new Color(190, 145, 52);
    private static final Color SOFT_BLUE = new Color(242, 247, 252);
    private static final Color TEXT = new Color(35, 39, 47);

    private final ResourceLoader resourceLoader;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public PdfGenerationService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String generateCertificatePdf(String studentName, String courseName, String certCode) {
        String fileName = "CERT_" + certCode + ".pdf";
        Path filePath = getCertificatePath(fileName);

        try {
            Files.createDirectories(filePath.getParent());

            Document document = new Document(PageSize.A4.rotate(), 56, 56, 46, 42);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            BaseFont baseFont = loadUnicodeFont("classpath:fonts/arial.ttf");
            Font titleFont = new Font(baseFont, 32, Font.BOLD, NAVY);
            Font subtitleFont = new Font(baseFont, 13, Font.NORMAL, new Color(90, 100, 115));
            Font labelFont = new Font(baseFont, 14, Font.NORMAL, TEXT);
            Font nameFont = new Font(baseFont, 30, Font.BOLD, GOLD);
            Font courseFont = new Font(baseFont, 20, Font.BOLD, NAVY);
            Font smallFont = new Font(baseFont, 10, Font.NORMAL, new Color(95, 101, 112));
            Font signatureFont = new Font(baseFont, 11, Font.BOLD, NAVY);

            drawCertificateFrame(writer);

            Paragraph brand = new Paragraph("HUTECH LMS", new Font(baseFont, 15, Font.BOLD, GOLD));
            brand.setAlignment(Element.ALIGN_CENTER);
            brand.setSpacingAfter(8);
            document.add(brand);

            Paragraph title = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(8);
            document.add(title);

            Paragraph subtitle = new Paragraph("This certificate is proudly presented to", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(22);
            document.add(subtitle);

            Paragraph name = new Paragraph(studentName.toUpperCase(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingAfter(10);
            document.add(name);

            Paragraph divider = new Paragraph(new Chunk("____________________________________________", new Font(baseFont, 14, Font.NORMAL, GOLD)));
            divider.setAlignment(Element.ALIGN_CENTER);
            divider.setSpacingAfter(18);
            document.add(divider);

            Paragraph text = new Paragraph("has successfully completed the course", labelFont);
            text.setAlignment(Element.ALIGN_CENTER);
            text.setSpacingAfter(12);
            document.add(text);

            Paragraph course = new Paragraph(courseName, courseFont);
            course.setAlignment(Element.ALIGN_CENTER);
            course.setSpacingAfter(28);
            document.add(course);

            PdfPTable details = new PdfPTable(3);
            details.setWidthPercentage(82);
            details.setWidths(new float[]{1.1f, 1.2f, 1.1f});
            details.setSpacingBefore(10);
            details.setSpacingAfter(28);
            details.addCell(detailCell("Issued date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), baseFont));
            details.addCell(detailCell("Certificate code", certCode, baseFont));
            details.addCell(detailCell("Verification", "Valid via LMS system", baseFont));
            document.add(details);

            PdfPTable signatures = new PdfPTable(2);
            signatures.setWidthPercentage(78);
            signatures.setWidths(new float[]{1, 1});
            signatures.addCell(signatureCell("Academic Director", signatureFont, smallFont));
            signatures.addCell(signatureCell("LMS Administrator", signatureFont, smallFont));
            document.add(signatures);

            Paragraph footer = new Paragraph("This certificate was generated electronically by HUTECH LMS.", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(22);
            document.add(footer);

            document.close();
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Loi khi tao file PDF chung chi: " + e.getMessage());
        }
    }

    public Path getCertificatePath(String fileName) {
        return Paths.get(uploadDir, fileName);
    }

    private BaseFont loadUnicodeFont(String location) throws Exception {
        Resource fontResource = resourceLoader.getResource(location);
        return BaseFont.createFont(fontResource.getFile().getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private void drawCertificateFrame(PdfWriter writer) {
        PdfContentByte canvas = writer.getDirectContentUnder();
        Rectangle page = PageSize.A4.rotate();

        canvas.saveState();
        canvas.setColorFill(SOFT_BLUE);
        canvas.rectangle(0, 0, page.getWidth(), page.getHeight());
        canvas.fill();

        canvas.setColorFill(Color.WHITE);
        canvas.roundRectangle(34, 30, page.getWidth() - 68, page.getHeight() - 60, 18);
        canvas.fill();

        canvas.setColorStroke(NAVY);
        canvas.setLineWidth(2.4f);
        canvas.roundRectangle(46, 42, page.getWidth() - 92, page.getHeight() - 84, 14);
        canvas.stroke();

        canvas.setColorStroke(GOLD);
        canvas.setLineWidth(1.2f);
        canvas.roundRectangle(60, 56, page.getWidth() - 120, page.getHeight() - 112, 10);
        canvas.stroke();

        canvas.setColorFill(new Color(248, 241, 224));
        canvas.circle(118, page.getHeight() - 104, 38);
        canvas.fill();
        canvas.setColorFill(GOLD);
        canvas.circle(118, page.getHeight() - 104, 27);
        canvas.fill();
        canvas.setColorFill(Color.WHITE);
        canvas.circle(118, page.getHeight() - 104, 19);
        canvas.fill();
        canvas.restoreState();
    }

    private PdfPCell detailCell(String label, String value, BaseFont baseFont) {
        Font labelFont = new Font(baseFont, 9, Font.NORMAL, new Color(105, 111, 122));
        Font valueFont = new Font(baseFont, 11, Font.BOLD, NAVY);
        Paragraph content = new Paragraph();
        content.setAlignment(Element.ALIGN_CENTER);
        content.add(new Chunk(label.toUpperCase() + "\n", labelFont));
        content.add(new Chunk(value, valueFont));

        PdfPCell cell = new PdfPCell(content);
        cell.setPadding(12);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(new Color(224, 228, 236));
        cell.setBackgroundColor(new Color(250, 252, 255));
        return cell;
    }

    private PdfPCell signatureCell(String title, Font titleFont, Font smallFont) {
        Paragraph content = new Paragraph();
        content.setAlignment(Element.ALIGN_CENTER);
        content.add(new Chunk("__________________________\n", smallFont));
        content.add(new Chunk(title + "\n", titleFont));
        content.add(new Chunk("Authorized signature", smallFont));

        PdfPCell cell = new PdfPCell(content);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingTop(10);
        return cell;
    }
}
