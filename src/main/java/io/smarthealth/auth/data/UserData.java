/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.data;
 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data; 

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
