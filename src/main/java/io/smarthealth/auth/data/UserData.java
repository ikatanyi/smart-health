/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.data;

import io.smarthealth.auth.domain.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kelsas
 */
@Data
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
    private List<String> roles = new ArrayList<>();

    public static User map(UserData userData) {
        ModelMapper modelMapper = new ModelMapper();
        User user = modelMapper.map(userData, User.class);
        return user;
    }

    public static UserData map(User user) {
        ModelMapper modelMapper = new ModelMapper();
        UserData userData = modelMapper.map(user, UserData.class);
        return userData;
    }

}
