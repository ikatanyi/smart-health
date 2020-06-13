package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabTestData;
import io.smarthealth.clinical.laboratory.data.Labs;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "lab_test_types")
public class LabTest extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_types_service_id"))
    private Item service;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_test_types_displine_id"))
    private LabDiscipline displine;
    private String testName;
    private String code;
    private String gender;
    private Boolean requiresConsent;
    private String turnAroundTime;
    private Boolean hasReferenceValue;
    private Boolean active;
    private Boolean isPanel;

    @OneToMany(mappedBy = "labTest", cascade = CascadeType.ALL)
    private List<Analyte> analytes = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "lab_test_panel",
            joinColumns = {
                @JoinColumn(name = "lab_test_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "lab_test_panel_id")})
    private Set<LabTest> panelTests = new HashSet<LabTest>();

    public void addAnalyte(Analyte analyte) {
        analyte.setLabTest(this);
        analytes.add(analyte);
    }

    public void addAnalytes(List<Analyte> analytes) {
        this.analytes = new ArrayList<>();
        this.analytes = analytes;
        this.analytes.forEach(x -> x.setLabTest(this));
    }

    public LabTestData toData() {
        LabTestData data = new LabTestData();
        data.setId(this.getId());
        data.setTurnAroundTime(this.turnAroundTime);
        data.setActive(this.active);
        data.setShortName(this.code);
        data.setRequiresConsent(this.requiresConsent);
        data.setHasReferenceValue(this.hasReferenceValue);
        data.setGender(this.gender);
        data.setIsPanel(this.isPanel);

        if (this.displine != null) {
            data.setCategory(this.displine.getDisplineName());
            data.setCategoryId(this.displine.getId());
        }
        data.setTestName(this.testName);
        if (this.service != null) {
            data.setItemId(this.service.getId());
            data.setItemCode(this.service.getItemCode());
            data.setItemName(this.service.getItemName());
        }

        data.setAnalytes(
                this.analytes
                        .stream()
                        .map(x -> x.toData())
                        .collect(Collectors.toList())
        );
        data.setPanelTests(
                this.panelTests
                        .stream()
                        .map(x ->new Labs(x.getId(), x.getCode(), x.getTestName()) )
                        .collect(Collectors.toSet())
        );
        return data;
    }

    public void resetAnalytes() {
        this.analytes = new ArrayList<>();
    }

    public Labs toSimpleData() {
        return new Labs(this.getId(), this.code, this.testName);
    }
}
