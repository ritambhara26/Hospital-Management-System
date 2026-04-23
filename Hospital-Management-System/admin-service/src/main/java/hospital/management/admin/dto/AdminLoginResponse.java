package hospital.management.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long adminId;
    private String email;
    private String firstName;
    private String lastName;

}

