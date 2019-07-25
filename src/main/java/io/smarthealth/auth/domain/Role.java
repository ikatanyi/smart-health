package io.smarthealth.auth.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 * User's Role
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "auth_role" , 
            uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_uuid", columnNames= { "uuid" } )
            } )
public class Role extends Identifiable{
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "auth_permission_role", joinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private List<Permission> permissions;
}
