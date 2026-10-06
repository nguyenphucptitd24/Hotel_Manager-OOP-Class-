package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Integer id;
    private String roomNumber;
    private Integer floor;
    private String status;
    private Integer roomTypeId;
    private String roomTypeName;
    private BigDecimal basePrice;
}
