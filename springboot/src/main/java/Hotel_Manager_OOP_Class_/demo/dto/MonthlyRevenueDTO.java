package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRevenueDTO {
    private String month;
    private Double revenue;
    private Long bookingCount;

    public MonthlyRevenueDTO(String month, Double revenue) {
        this.month = month;
        this.revenue = revenue;
        this.bookingCount = 0L;
    }
}
