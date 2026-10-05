package Hotel_Manager_OOP_Class_.demo.repository;

import Hotel_Manager_OOP_Class_.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query(value = """
            SELECT 
                COUNT(*) AS totalBookings,
                COALESCE(SUM(CASE WHEN status LIKE '%CANCEL%' THEN 1 ELSE 0 END), 0) AS cancelledBookings,
                COALESCE(SUM(CASE WHEN status NOT LIKE '%CANCEL%' THEN 1 ELSE 0 END), 0) AS confirmedBookings
            FROM bookings
            """, nativeQuery = true)
    List<Object[]> getBookingStatistics();

    @Query(value = """
            SELECT COALESCE(SUM(
                CASE 
                    WHEN b.total_deposit > 0 THEN b.total_deposit
                    ELSE (
                        CASE 
                            WHEN DATEDIFF(day, TRY_CONVERT(date, LEFT(bd.check_in_expected, 10)), TRY_CONVERT(date, LEFT(bd.check_out_expected, 10))) <= 0 THEN 1 
                            ELSE DATEDIFF(day, TRY_CONVERT(date, LEFT(bd.check_in_expected, 10)), TRY_CONVERT(date, LEFT(bd.check_out_expected, 10))) 
                        END
                    ) * bd.price_per_night
                END
            ), 0)
            FROM bookings b
            JOIN booking_details bd ON b.id = bd.booking_id
            WHERE b.status NOT LIKE '%CANCEL%'
            """, nativeQuery = true)
    Double calculateTotalRevenue();

    @Query(value = """
            SELECT 
                CONCAT(N'Tháng ', MONTH(TRY_CONVERT(date, LEFT(b.booking_date, 10)))) AS monthName,
                YEAR(TRY_CONVERT(date, LEFT(b.booking_date, 10))) AS yr,
                MONTH(TRY_CONVERT(date, LEFT(b.booking_date, 10))) AS mo,
                COALESCE(SUM(
                    CASE 
                        WHEN b.total_deposit > 0 THEN b.total_deposit
                        ELSE (
                            CASE 
                                WHEN DATEDIFF(day, TRY_CONVERT(date, LEFT(bd.check_in_expected, 10)), TRY_CONVERT(date, LEFT(bd.check_out_expected, 10))) <= 0 THEN 1 
                                ELSE DATEDIFF(day, TRY_CONVERT(date, LEFT(bd.check_in_expected, 10)), TRY_CONVERT(date, LEFT(bd.check_out_expected, 10))) 
                            END
                        ) * bd.price_per_night
                    END
                ), 0) AS revenue,
                COUNT(DISTINCT b.id) AS bookingCount
            FROM bookings b
            JOIN booking_details bd ON b.id = bd.booking_id
            WHERE b.status NOT LIKE '%CANCEL%' AND b.booking_date IS NOT NULL
            GROUP BY YEAR(TRY_CONVERT(date, LEFT(b.booking_date, 10))), MONTH(TRY_CONVERT(date, LEFT(b.booking_date, 10)))
            ORDER BY yr ASC, mo ASC
            """, nativeQuery = true)
    List<Object[]> getMonthlyRevenueDetails();
}
