 package io.smarthealth.organization.domain;

import io.smarthealth.common.domain.SetupMetadata;
import javax.persistence.Entity;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
 @Entity
 @Data 
public class DepatmentUnit extends SetupMetadata{
    private String description;
}
