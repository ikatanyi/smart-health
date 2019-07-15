package io.smarthealth.auth.domain;

import io.smarthealth.common.domain.Identifiable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * User's Role
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "auth_role")
public class Role extends Identifiable{
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "permission_role", joinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private List<Permission> permissions;
}
