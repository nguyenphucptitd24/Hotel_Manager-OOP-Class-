package Hotel_Manager_OOP_Class_.demo.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bookingCode;
    private String status;
    private Double totalPrice;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String note;

  
    private String bookingDate;
    private BigDecimal totalDeposit;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}