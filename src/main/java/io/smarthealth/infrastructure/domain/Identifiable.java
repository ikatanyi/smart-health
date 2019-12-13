package io.smarthealth.infrastructure.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Base Entity using {@link  GeneratedValue } Identity Strategy to generate a
 * primary key, with a unique UUID, User and Date audit information.
 *
 * @author Kelsas
 */
@Data
@MappedSuperclass
public abstract class Identifiable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 38)
    private String companyId;

}
