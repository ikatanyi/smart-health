package io.smarthealth.auth.data;

import io.smarthealth.auth.validator.ValidPassword;
import lombok.Data;
@Data
public class PasswordDto {
    private String oldPassword;
    @ValidPassword
    private String newPassword; 
}
