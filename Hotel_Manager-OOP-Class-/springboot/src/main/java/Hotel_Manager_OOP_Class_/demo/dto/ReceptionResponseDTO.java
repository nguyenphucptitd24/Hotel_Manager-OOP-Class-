package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceptionResponseDTO {
    private String message;
    private Integer bookingId;
    private Integer roomId;
    private String roomNumber;
    private String status;
}
