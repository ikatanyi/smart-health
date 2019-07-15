package io.smarthealth.common.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 *  Simple Name and {@link Identifiable} attributes base class 
 * @author Kelsas
 */
@Data
@MappedSuperclass
public abstract class BaseMetadata extends Identifiable {

    @Column(name = "a_name")
    private String name;
    private boolean isEnabled = true;
    private LocalDateTime createDatetime = LocalDateTime.now();
}
