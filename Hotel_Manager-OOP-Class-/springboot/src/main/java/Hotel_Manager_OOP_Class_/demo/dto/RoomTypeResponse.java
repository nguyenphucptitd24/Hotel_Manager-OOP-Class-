package Hotel_Manager_OOP_Class_.demo.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomTypeResponse {

    private final Integer id;
    private final String name;
    private final BigDecimal basePrice;
    private final Integer capacity;
    private final String description;
}