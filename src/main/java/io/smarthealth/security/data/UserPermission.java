/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kent
 */
@Data
public class UserPermission {

    private  String group;
    private  List<PermissionData> permissions = new ArrayList<>();

    public UserPermission(String group, List<PermissionData> permissions) {
        this.group = group;
        this.permissions=permissions;
    }
    
    
}
