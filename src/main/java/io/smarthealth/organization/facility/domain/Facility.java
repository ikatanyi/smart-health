package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.company.domain.CompanyLogo;
import io.smarthealth.organization.org.domain.Organisation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility",uniqueConstraints = {
    @UniqueConstraint(columnNames = {"facilityName"}, name="unique_facility_name")})
public class Facility extends Auditable {

    @OneToOne(mappedBy = "facility", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_facility_company_logo_id"))
    private CompanyLogo companyLogo;

    public enum Type {
        Hospital, Clinic, Speciality
    }

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_facility_org_id"))
    private Organisation organization; 
    private String registrationNumber;
    private String facilityType;
    private String taxNumber;
    private String facilityClass; //government classifications
    private String facilityName;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_facility_facility_parent_id"))
    private Facility parentFacility;
   
    private boolean enabled;

    @OneToMany(mappedBy = "facility")
    private List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "facility")
    private Set<FacilityBank> facilityBanks;

    public void addBank(FacilityBank bank) {
        bank.setFacility(this);
        facilityBanks.add(bank);
    } 
}
