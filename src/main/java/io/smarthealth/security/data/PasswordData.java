package io.smarthealth.security.data;

import io.smarthealth.security.validator.ValidPassword;
import lombok.Data;

@Data
public class PasswordData {

    private String currentPassword;
    @ValidPassword
    private String newPassword;

}
