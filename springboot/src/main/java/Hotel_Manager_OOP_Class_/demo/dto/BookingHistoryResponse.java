package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHistoryResponse {

    private Integer id;

    private String bookingCode;

    private String bookingDate;

    private BigDecimal totalDeposit;

    private String status;
}