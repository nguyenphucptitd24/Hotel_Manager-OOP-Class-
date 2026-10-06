package Hotel_Manager_OOP_Class_.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetail {

    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in_expected", nullable = false, length = 50)
    private String checkInExpected;

    @Column(name = "check_out_expected", nullable = false, length = 50)
    private String checkOutExpected;

    @Column(name = "check_in_actual", length = 50)
    private String checkInActual;

    @Column(name = "check_out_actual", length = 50)
    private String checkOutActual;

    @Column(name = "price_per_night", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerNight;
}
