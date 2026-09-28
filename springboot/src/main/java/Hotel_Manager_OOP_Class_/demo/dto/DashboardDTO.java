package Hotel_Manager_OOP_Class.dto;

import java.util.List;

public class DashboardDTO {
    private Double totalRevenue;
    private Double occupancyRate;
    private Double cancellationRate;
    private List<MonthlyRevenueDTO> monthlyRevenue;

    public DashboardDTO() {}

    public DashboardDTO(Double totalRevenue, Double occupancyRate, Double cancellationRate, List<MonthlyRevenueDTO> monthlyRevenue) {
        this.totalRevenue = totalRevenue;
        this.occupancyRate = occupancyRate;
        this.cancellationRate = cancellationRate;
        this.monthlyRevenue = monthlyRevenue;
    }

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }

    public Double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(Double occupancyRate) { this.occupancyRate = occupancyRate; }

    public Double getCancellationRate() { return cancellationRate; }
    public void setCancellationRate(Double cancellationRate) { this.cancellationRate = cancellationRate; }

    public List<MonthlyRevenueDTO> getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(List<MonthlyRevenueDTO> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public static class MonthlyRevenueDTO {
        private String month;
        private Double revenue;

        public MonthlyRevenueDTO() {}

        public MonthlyRevenueDTO(String month, Double revenue) {
            this.month = month;
            this.revenue = revenue;
        }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }

        public Double getRevenue() { return revenue; }
        public void setRevenue(Double revenue) { this.revenue = revenue; }
    }
}