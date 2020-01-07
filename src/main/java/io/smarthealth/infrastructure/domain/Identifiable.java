package io.smarthealth.infrastructure.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base Entity using {@link  GeneratedValue } Identity Strategy to generate a
 * primary key, with a unique UUID, User and Date audit information.
 *
 * @author Kelsas
 */
@MappedSuperclass
public abstract class Identifiable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /*This is*/
    @Column(length = 38)
    private String companyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public boolean isNew() {
        return this.id == null;
    }
//    @PrePersist
//    public void setTenant(){
//        this.setCompanyId(TenantContext.getCurrentTenant());
//    }
}
