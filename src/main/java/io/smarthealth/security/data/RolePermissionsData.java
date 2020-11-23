package io.smarthealth.security.data;

import java.util.Collection;

/**
 *
 * @author Kelsas
 */
//@Data
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public Collection<PermissionData> getPermissionUsageData() {
        return permissionUsageData;
    }
    
}
