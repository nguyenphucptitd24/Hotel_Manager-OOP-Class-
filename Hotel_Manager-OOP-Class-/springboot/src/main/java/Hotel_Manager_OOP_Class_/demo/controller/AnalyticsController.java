package Hotel_Manager_OOP_Class_.demo.controller;

import Hotel_Manager_OOP_Class_.demo.dto.DashboardDTO;
import Hotel_Manager_OOP_Class_.demo.security.All;
import Hotel_Manager_OOP_Class_.demo.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @All
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardStats() {
        DashboardDTO dashboardDTO = analyticsService.getDashboardStats();
        return ResponseEntity.ok(dashboardDTO);
    }
}
