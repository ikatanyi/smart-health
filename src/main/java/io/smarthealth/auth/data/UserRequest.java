package io.smarthealth.auth.data;

import io.smarthealth.auth.validator.ValidCreateUser;
import io.smarthealth.auth.validator.ValidPassword;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data; 

/**
 *
 * @author Kelsas
 */
@Data
@ValidCreateUser
public class UserRequest {

    @NotBlank
    @Size(min = 4, max = 40)
    private String name;

    @NotBlank
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email(message = "Please Provide a valid email")
    private String email;

    @ValidPassword
    private String password;
    
      private List<String> roles = new ArrayList<>();
 
}
