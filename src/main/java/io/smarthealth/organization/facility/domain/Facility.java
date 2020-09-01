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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "facility", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"facilityName"}, name = "unique_facility_name")})
public class Facility extends Auditable {

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_facility_company_logo_id"))
    @OneToOne(cascade = CascadeType.ALL)
    private CompanyLogo companyLogo=new CompanyLogo();

    public enum Type {
        Hospital, Clinic, Speciality
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_facility_org_id"))
    private Organisation organization;
    private String registrationNumber;
    private String facilityType;
    private String taxNumber;
    private String facilityClass; //government classifications
    private String facilityName;
    private String footerMsg;
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

    public void addLogo(CompanyLogo logo) {
        logo.setFacility(this);
       this.companyLogo=logo;
    }
}
