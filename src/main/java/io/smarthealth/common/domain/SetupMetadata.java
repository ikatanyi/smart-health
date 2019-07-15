package io.smarthealth.common.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 * Base Class for Lookup Tables
 *
 * @author Kelsas
 */
@Data
@MappedSuperclass
public abstract class SetupMetadata extends Identifiable {
 
    @Column(name = "a_name")
    private String name;
    private boolean active = true;
    private LocalDateTime dateCreated = LocalDateTime.now();
}
