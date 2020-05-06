package io.smarthealth.security.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserData implements Serializable {

    @ApiModelProperty(hidden = true)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String uuid;
    @NotNull(message = "Email cannot be blank")
    private String email;
    //@ApiModelProperty(hidden = true)
    private String username;
    @ApiModelProperty(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;
    private String name;
    private boolean enabled;
    @ApiModelProperty(hidden = true)
    private boolean account_locked;
    @ApiModelProperty(hidden = true)
    private boolean account_expired;
    @ApiModelProperty(hidden = true)
    private boolean credentials_expired;
    @ApiModelProperty(hidden = true)
    private boolean verified;
    @ApiModelProperty(hidden = true)
    private LocalDateTime lastLogin;
    private List<String> roles = new ArrayList<>();

}
