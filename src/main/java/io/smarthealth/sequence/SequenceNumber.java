package io.smarthealth.sequence;


import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.*;
import java.io.Serializable;
import lombok.Data;

/**
 * Entity that holds the next available sequence number for a specific sequence definition.
 */
@Entity
@Data
public class SequenceNumber extends Identifiable {
 

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "definition_id")
    private SequenceDefinition definition;

    @Column(name = "sequence_group", nullable = true, length = 40)
    private String group;

    @Column(nullable = false)
    private Long number = 1L;

    protected SequenceNumber() {
    }

    public SequenceNumber(SequenceDefinition sequenceDefinition) {
        this.definition = sequenceDefinition;
    }

    public SequenceNumber(SequenceDefinition definition, Long number) {
        this.definition = definition;
        this.number = number;
    } 

    @Override
    public String toString() {
        return getNumber().toString();
    }
}
