package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {
    private Double totalRevenue;
    private Double occupancyRate;
    private Double cancellationRate;
    private Long totalBookings;
    private Long confirmedBookings;
    private Long cancelledBookings;
    private List<MonthlyRevenueDTO> monthlyRevenue;

    public DashboardDTO(Double totalRevenue, Double occupancyRate, Double cancellationRate, List<MonthlyRevenueDTO> monthlyRevenue) {
        this.totalRevenue = totalRevenue;
        this.occupancyRate = occupancyRate;
        this.cancellationRate = cancellationRate;
        this.monthlyRevenue = monthlyRevenue;
    }
}
