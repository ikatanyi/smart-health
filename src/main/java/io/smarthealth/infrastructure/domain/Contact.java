package io.smarthealth.infrastructure.domain;

import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@MappedSuperclass
public abstract class Contact extends Identifiable{

    private String email;
    private String telephone;
    private String mobile;
}
