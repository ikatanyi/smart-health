package io.smarthealth.accounting.acc.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "acc_tx_types")
public class TransactionTypeEntity extends Identifiable {

    @Column(name = "identifier", nullable = false, length = 32)
    private String identifier;
    @Column(name = "a_name", nullable = false, length = 256)
    private String name;
    @Column(name = "description", nullable = true, length = 2048)
    private String description;

    public TransactionTypeEntity() {
        super();
    }

}
