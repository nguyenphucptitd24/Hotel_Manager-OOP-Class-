package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceptionBookingDTO {
    private Integer bookingId;
    private String bookingCode;
    private String customerName;
    private String customerPhone;
    private Integer roomId;
    private String roomNumber;
    private String checkInExpected;
    private String checkOutExpected;
    private String checkInActual;
    private String checkOutActual;
    private String status;
}
