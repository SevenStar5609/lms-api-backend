package vn.edu.hutech.lms_api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer score; // Điểm số thực tế (VD: 80/100)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_answers", columnDefinition = "jsonb", nullable = false)
    private Map<Long, String> userAnswers; // Lưu: { "id_câu_hỏi": "đáp_án_chọn" }

    @Column(nullable = false)
    private String status; // PASSED hoặc FAILED

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;
}