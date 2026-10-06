package Hotel_Manager_OOP_Class_.demo.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponse {

    private final Integer id;
    private final String roomNumber;
    private final Integer floor;
    private final String status;

    private final Integer roomTypeId;
    private final String roomTypeName;
    private final BigDecimal basePrice;
}