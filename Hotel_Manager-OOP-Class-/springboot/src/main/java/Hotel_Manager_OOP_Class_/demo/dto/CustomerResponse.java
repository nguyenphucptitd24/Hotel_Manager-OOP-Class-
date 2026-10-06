package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Integer id;

    private String fullName;

    private String identityCard;

    private String phone;

    private String email;

    private String country;
}