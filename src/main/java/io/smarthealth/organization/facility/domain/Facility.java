package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.org.domain.Organization;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility")
public class Facility extends Auditable {

    public enum Type {
        Hospital, Clinic, Speciality
    }

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_facility_org_id"))
    private Organization organization; 
    private String registrationNumber;
    private String facilityType;
    private String taxNumber;
    private String facilityClass; //government classifications
    private String facilityName;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_facility_facility_parent_id"))
    private Facility parentFacility;
    @Lob
    private byte[] logo;
   private boolean enabled;
   
    @OneToMany(mappedBy = "facility")
    private List<Ward> wards = new ArrayList<>();

    @OneToMany(mappedBy = "facility")
    private List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "facility")
    private Set<FacilityBank> facilityBanks;

    public void addBank(FacilityBank bank) {
        bank.setFacility(this);
        facilityBanks.add(bank);
    }
}
