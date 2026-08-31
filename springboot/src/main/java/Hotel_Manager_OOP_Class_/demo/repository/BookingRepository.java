package Hotel_Manager_OOP_Class_.demo.repository;

import Hotel_Manager_OOP_Class_.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByBookingCode(String bookingCode);

    Page<Booking> findByCustomerId(Integer customerId, Pageable pageable);

    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    List<Object[]> countByStatus();

    @Query("SELECT YEAR(CAST(b.bookingDate AS date)), MONTH(CAST(b.bookingDate AS date)), SUM(b.totalDeposit) " +
            "FROM Booking b " +
            "WHERE b.totalDeposit IS NOT NULL " +
            "GROUP BY YEAR(CAST(b.bookingDate AS date)), MONTH(CAST(b.bookingDate AS date)) " +
            "ORDER BY YEAR(CAST(b.bookingDate AS date)), MONTH(CAST(b.bookingDate AS date))")
    List<Object[]> getRevenueByMonth();

    @Query("SELECT YEAR(CAST(b.bookingDate AS date)), SUM(b.totalDeposit) " +
            "FROM Booking b " +
            "WHERE b.totalDeposit IS NOT NULL " +
            "GROUP BY YEAR(CAST(b.bookingDate AS date)) " +
            "ORDER BY YEAR(CAST(b.bookingDate AS date))")
    List<Object[]> getRevenueByYear();
}
