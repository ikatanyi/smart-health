package io.smarthealth.sequence;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
public class SequenceDefinition extends Identifiable {
  
    @Version
    private long version;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 100)
    private String format;

    protected SequenceDefinition() {
    }

    public SequenceDefinition(Long tenantId, String name) {
        this.tenantId = tenantId;
        this.name = name;
    }

    public SequenceDefinition(Long tenantId, String name, String format) {
        this.tenantId = tenantId;
        this.name = name;
        this.format = format;
    }
}
