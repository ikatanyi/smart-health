package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "person_fingerprints")
public class Biometrics extends Identifiable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person person;

    private String indicator;
    @Lob
    private byte[] fingerTemplate;

}
