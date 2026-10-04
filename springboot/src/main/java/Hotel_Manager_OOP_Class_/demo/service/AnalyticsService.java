package Hotel_Manager_OOP_Class_.demo.service;

import Hotel_Manager_OOP_Class_.demo.dto.DashboardDTO;
import Hotel_Manager_OOP_Class_.demo.repository.BookingRepository;
import Hotel_Manager_OOP_Class_.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AnalyticsService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    public DashboardDTO getDashboardData() {
        long totalRooms = roomRepository.count();
        long totalBookings = bookingRepository.count();

        // Tính doanh thu và tỷ lệ lấp đầy từ dữ liệu trong DB
        double totalRevenue = bookingRepository.findAll().stream()
                .filter(b -> "CHECKED_OUT".equals(b.getStatus()) && b.getTotalPrice() != null)
                .mapToDouble(b -> b.getTotalPrice())
                .sum();

        long occupiedRooms = roomRepository.findAll().stream()
                .filter(r -> "OCCUPIED".equals(r.getStatus()))
                .count();

        long cancelledBookings = bookingRepository.findAll().stream()
                .filter(b -> "CANCELLED".equals(b.getStatus()))
                .count();

        double occupancyRate = totalRooms > 0 ? ((double) occupiedRooms / totalRooms) * 100 : 0.0;
        double cancellationRate = totalBookings > 0 ? ((double) cancelledBookings / totalBookings) * 100 : 0.0;

        return DashboardDTO.builder()
                .totalRevenue(totalRevenue)
                .occupancyRate(Math.round(occupancyRate * 10.0) / 10.0)
                .cancellationRate(Math.round(cancellationRate * 10.0) / 10.0)
                .monthlyRevenues(new ArrayList<>())
                .build();
    }
}