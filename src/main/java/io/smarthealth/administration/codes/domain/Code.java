package io.smarthealth.administration.codes.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "m_code", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"code_name"}, name = "code_name")})
public class Code extends Identifiable {

    @Column(name = "code_name", length = 100)
    private String name;

    @Column(name = "is_system_defined")
    private boolean systemDefined;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "code", orphanRemoval = true)
    private Set<CodeValue> values;

}
