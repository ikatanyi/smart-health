/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.data;

import lombok.Data;

import java.util.Collection;

/**
 *
 * @author Kelsas
 */
@Data
public class RolePermissionsData {

    private final Long id;
    private final String name;
    private final String description;
    private final Boolean disabled;

    private final Collection<PermissionData> permissionUsageData;

    public RolePermissionsData(final Long id, final String name, final String description, final Boolean disabled,
            final Collection<PermissionData> permissionUsageData) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.disabled = disabled;
        this.permissionUsageData = permissionUsageData;
    }
}
