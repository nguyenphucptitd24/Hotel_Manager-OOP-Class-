package Hotel_Manager_OOP_Class.controller;

import Hotel_Manager_OOP_Class.dto.DashboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardStats() {
        List<DashboardDTO.MonthlyRevenueDTO> monthlyList = List.of(
            new DashboardDTO.MonthlyRevenueDTO("Tháng 1", 180000.0),
            new DashboardDTO.MonthlyRevenueDTO("Tháng 2", 220000.0),
            new DashboardDTO.MonthlyRevenueDTO("Tháng 3", 250000.0),
            new DashboardDTO.MonthlyRevenueDTO("Tháng 4", 210000.0),
            new DashboardDTO.MonthlyRevenueDTO("Tháng 5", 280000.0),
            new DashboardDTO.MonthlyRevenueDTO("Tháng 6", 310000.0)
        );

        DashboardDTO response = new DashboardDTO(1285000.0, 67.2, 32.8, monthlyList);
        return ResponseEntity.ok(response);
    }
}