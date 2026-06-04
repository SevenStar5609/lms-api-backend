package vn.edu.hutech.lms_api.dto.course;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseSearchCriteria {
    private String title;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long instructorId;
    private String instructorName;
    private String status;
}
