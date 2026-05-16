package vn.edu.hutech.lms_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hutech.lms_api.dto.dashboard.DashboardResponseDTO;
import vn.edu.hutech.lms_api.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Chỉ ADMIN hoặc INSTRUCTOR mới được phép xem thống kê này
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboardData() {
        return ResponseEntity.ok(dashboardService.getSystemDashboard());
    }
}