package io.smarthealth.organization.person.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.person.data.BiometricDataIndicator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "hand_indicator")
    protected BiometricDataIndicator indicator;
    @Lob
    protected String template;
    @Transient
    private String data;

}
