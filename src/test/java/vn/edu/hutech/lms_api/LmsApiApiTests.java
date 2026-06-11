package vn.edu.hutech.lms_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.edu.hutech.lms_api.controller.CertificateController;
import vn.edu.hutech.lms_api.controller.DashboardController;
import vn.edu.hutech.lms_api.controller.EnrollmentController;
import vn.edu.hutech.lms_api.controller.LessonController;
import vn.edu.hutech.lms_api.controller.QuizController;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import vn.edu.hutech.lms_api.dto.dashboard.DashboardResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.lesson.LessonResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResultResponseDTO;
import vn.edu.hutech.lms_api.exception.ForbiddenOperationException;
import vn.edu.hutech.lms_api.exception.GlobalExceptionHandler;
import vn.edu.hutech.lms_api.service.CertificateService;
import vn.edu.hutech.lms_api.service.DashboardService;
import vn.edu.hutech.lms_api.service.EnrollmentService;
import vn.edu.hutech.lms_api.service.LessonService;
import vn.edu.hutech.lms_api.service.QuizService;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LmsApiApiTests {

    private MockMvc mockMvc;
    private EnrollmentService enrollmentService;
    private LessonService lessonService;
    private QuizService quizService;
    private CertificateService certificateService;
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        enrollmentService = mock(EnrollmentService.class);
        lessonService = mock(LessonService.class);
        quizService = mock(QuizService.class);
        certificateService = mock(CertificateService.class);
        dashboardService = mock(DashboardService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new EnrollmentController(enrollmentService),
                        new LessonController(lessonService),
                        new QuizController(quizService),
                        new CertificateController(certificateService),
                        new DashboardController(dashboardService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void enrollCourseReturnsCreatedEnrollment() throws Exception {
        when(enrollmentService.enrollCourse(any())).thenReturn(EnrollmentResponseDTO.builder()
                .id(10L)
                .userId(1L)
                .courseId(2L)
                .courseTitle("Spring LMS")
                .progress(0.0)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.courseId").value(2))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void enrollCourseRejectsMissingCourseId() throws Exception {
        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.courseId").exists());
    }

    @Test
    void markLessonCompleteReturnsUpdatedProgress() throws Exception {
        when(enrollmentService.markLessonAsCompleted(10L, 20L)).thenReturn(75.0);

        mockMvc.perform(post("/api/v1/enrollments/10/lessons/20/complete"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("75.0%")));

        verify(enrollmentService).markLessonAsCompleted(10L, 20L);
    }

    @Test
    void getLessonReturnsProtectedLessonContent() throws Exception {
        when(lessonService.getLessonById(20L)).thenReturn(LessonResponseDTO.builder()
                .id(20L)
                .title("Lesson 1")
                .contentType("VIDEO")
                .contentUrl("https://example.com/video.mp4")
                .moduleId(5L)
                .moduleTitle("Module 1")
                .build());

        mockMvc.perform(get("/api/v1/lessons/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.contentType").value("VIDEO"));
    }

    @Test
    void getLessonReturnsForbiddenWhenLearnerIsNotEnrolled() throws Exception {
        when(lessonService.getLessonById(20L))
                .thenThrow(new ForbiddenOperationException("Ban chua ghi danh khoa hoc nay nen khong the xem noi dung bai hoc!"));

        mockMvc.perform(get("/api/v1/lessons/20"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(containsString("chua ghi danh")));
    }

    @Test
    void submitQuizReturnsAttemptResult() throws Exception {
        when(quizService.submitQuiz(any())).thenReturn(QuizResultResponseDTO.builder()
                .attemptId(99L)
                .totalQuestions(2)
                .correctCount(2)
                .score(100)
                .status("PASSED")
                .submittedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/v1/quizzes/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quizId\":7,\"answers\":{\"1\":\"A\",\"2\":\"B\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptId").value(99))
                .andExpect(jsonPath("$.score").value(100))
                .andExpect(jsonPath("$.status").value("PASSED"));
    }

    @Test
    void submitQuizRejectsMissingAnswers() throws Exception {
        mockMvc.perform(post("/api/v1/quizzes/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quizId\":7}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answers").exists());
    }

    @Test
    void issueCertificateReturnsPdfUrl() throws Exception {
        when(certificateService.issueCertificate(2L)).thenReturn(CertificateResponseDTO.builder()
                .id(50L)
                .certificateCode("YOOT-CERT-12345678")
                .pdfUrl("http://localhost:8080/api/v1/files/download/cert.pdf")
                .userId(1L)
                .courseId(2L)
                .courseTitle("Spring LMS")
                .createdAt(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/v1/certificates/issue/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificateCode").value("YOOT-CERT-12345678"))
                .andExpect(jsonPath("$.pdfUrl").value(containsString("cert.pdf")));

        verify(certificateService).issueCertificate(eq(2L));
    }

    @Test
    void dashboardReturnsLearningReport() throws Exception {
        when(dashboardService.getSystemDashboard()).thenReturn(DashboardResponseDTO.builder()
                .totalLearners(12)
                .totalEnrollments(30)
                .completedCourses(9)
                .completionRate(30.0)
                .averageQuizScore(82.5)
                .build());

        mockMvc.perform(get("/api/v1/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLearners").value(12))
                .andExpect(jsonPath("$.completionRate").value(30.0))
                .andExpect(jsonPath("$.averageQuizScore").value(82.5));
    }
}
