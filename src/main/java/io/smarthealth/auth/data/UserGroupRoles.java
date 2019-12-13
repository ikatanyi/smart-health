/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class UserGroupRoles {

    private String groupName;
    private List<String> roles = new ArrayList<>();

}
