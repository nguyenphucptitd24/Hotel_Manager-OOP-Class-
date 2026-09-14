package Hotel_Manager_OOP_Class_.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CreateBookingRequest {
    private Integer customerId;
    private LocalDateTime checkInExpected;
    private LocalDateTime checkOutExpected;
    private List<Integer> roomIds;
    private String note;
}
