package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private Integer id;
    private String bookingCode;
    private Integer customerId;
    private LocalDateTime checkInExpected;
    private LocalDateTime checkOutExpected;
    private BigDecimal totalPrice;
    private BigDecimal totalDeposit;
    private String status;
    private List<Integer> roomIds;
}
