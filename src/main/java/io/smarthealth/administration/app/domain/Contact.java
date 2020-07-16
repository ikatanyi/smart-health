package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "contacts")
public class Contact extends Identifiable {
    private String salutation;
    private String fullName; 
    private String email;
//     @Digits(fraction = 0, integer = 10)
    private String telephone;
    private String mobile;
}
