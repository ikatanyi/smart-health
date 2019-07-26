package io.smarthealth.auth.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * System Permission
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "auth_permission")
public class Permission extends Identifiable{
    private String name; 
}
