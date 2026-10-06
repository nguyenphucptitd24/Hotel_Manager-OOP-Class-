package Hotel_Manager_OOP_Class_.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String username;
    private String password;
    private String fullName;
    private Integer roleId;
}