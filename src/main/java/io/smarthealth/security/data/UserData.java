package io.smarthealth.security.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;  
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData implements Serializable {

    private Long id;
    private String uuid;
    private String email;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;
    private String name;
    private boolean enabled;
    private boolean account_locked;
    private boolean account_expired;
    private boolean credentials_expired;
    private boolean verified;
    private LocalDateTime lastLogin;
    private List<String> roles = new ArrayList<>();

}
