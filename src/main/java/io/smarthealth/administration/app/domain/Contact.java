package io.smarthealth.administration.app.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

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
    private String telephone;
    private String mobile;
}
