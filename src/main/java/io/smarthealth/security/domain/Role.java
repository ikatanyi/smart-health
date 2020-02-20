package io.smarthealth.security.domain;
  
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.security.data.RoleData;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;

/**
 * User's Role
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "auth_role")
public class Role extends Identifiable {

    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @Column(name = "is_disabled", nullable = false)
    private Boolean disabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "auth_permission_role",
            joinColumns = {
                @JoinColumn(name = "role_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_role_role__id"))},
            inverseJoinColumns = {
                @JoinColumn(name = "permission_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_role_permission_id"))}, uniqueConstraints = {
                @UniqueConstraint(name = "ROLE_PERMISSION_UK_auth_permission_role", columnNames = {"role_id", "permission_id"})})
    private Set<Permission> permissions = new HashSet<>();

    protected Role() {
        //
    }

    public Role(final String name, final String description) {
        this.name = name.trim();
        this.description = description.trim();
        this.disabled = false;
    }

    public boolean updatePermission(final Permission permission, final boolean isSelected) {
        boolean changed = false;
        if (isSelected) {
            changed = addPermission(permission);
        } else {
            changed = removePermission(permission);
        }

        return changed;
    }

    private boolean addPermission(final Permission permission) {
        return this.permissions.add(permission);
    }

    private boolean removePermission(final Permission permission) {
        return this.permissions.remove(permission);
    }

    public Collection<Permission> getPermissions() {
        return this.permissions;
    }

    public boolean hasPermissionTo(final String permissionCode) {
        boolean match = false;
        for (final Permission permission : this.permissions) {
            if (permission.hasCode(permissionCode)) {
                match = true;
                break;
            }
        }
        return match;
    }

    public RoleData toData() {
        return new RoleData(getId(), this.name, this.description, this.disabled);
    }

}
