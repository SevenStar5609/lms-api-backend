package vn.edu.hutech.lms_api.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {
    @NotNull(message = "courseId khong duoc de trong")
    private Long courseId;

    @NotNull(message = "rating khong duoc de trong")
    @Min(value = 1, message = "rating phai tu 1 den 5")
    @Max(value = 5, message = "rating phai tu 1 den 5")
    private Integer rating;

    private String comment;
}
