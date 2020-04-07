package io.smarthealth.security.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.security.data.PermissionData;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * System Permission
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "auth_permission")
public class Permission extends Identifiable {

    private String permissionGroup;
    private String name;

    public boolean hasCode(final String checkCode) {
        return this.name.equalsIgnoreCase(checkCode);
    }

    public PermissionData toData() {
        PermissionData data = new PermissionData();
        data.setId(this.getId());
        data.setName(this.name);
        data.setPermissionGroup(this.permissionGroup);
        return data;
    }
}
