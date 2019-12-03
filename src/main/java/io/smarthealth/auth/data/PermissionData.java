/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PermissionData {

    private final String permissionGroup;
    private final String name; 
    private final Boolean selected;

    public static PermissionData from(final String permissionCode, final boolean isSelected) {
        return new PermissionData(null, permissionCode, isSelected);
    }

    public static PermissionData instance(final String grouping, final String code,  final Boolean selected) {
        return new PermissionData(grouping, code,  selected);
    }

    private PermissionData(final String grouping, final String name, final Boolean selected) {
        this.permissionGroup = grouping;
        this.name = name; 
        this.selected = selected;
    }
}
