package io.smarthealth.auth.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 * System Permission
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "auth_permission" , 
            uniqueConstraints = {
                @UniqueConstraint(name = "uk_permission_uuid", columnNames= { "uuid" } )
            } )
public class Permission extends Identifiable{
    private String name; 
}
