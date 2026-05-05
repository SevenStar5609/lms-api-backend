package vn.edu.hutech.lms_api.dto.module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModuleRequestDTO {

    @NotBlank(message = "Tên chương học không được để trống")
    private String title;

    @NotNull(message = "Số thứ tự không được để trống")
    private Integer orderIndex;

    @NotNull(message = "ID Khóa học không được để trống")
    private Long courseId;
}