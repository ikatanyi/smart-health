package io.smarthealth.infrastructure.sequence.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "sequence_data")
public class SequenceData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "sequence_name")
    private String sequenceName;
    @Column(name = "sequence_increment")
    private Integer sequenceIncrement;
    @Column(name = "sequence_min_value")
    private Integer sequenceMinValue;
    @Column(name = "sequence_max_value")
    private Long sequenceMaxValue;
    @Column(name = "sequence_cur_value")
    private Long sequenceCurValue;
    @Column(name = "sequence_cycle")
    private boolean sequenceCycle;
    @Column(name = "company_id", length = 38)
    private String companyId;

    public SequenceData() {
    }

    public SequenceData(String sequenceName, String companyId) {
        this.sequenceName = sequenceName;
        this.companyId = companyId;
        this.sequenceIncrement = 1;
        this.sequenceMinValue = 1;
        this.sequenceMaxValue = Long.MAX_VALUE;
        this.sequenceCurValue = 0L;
        this.sequenceCycle = false;

    }

}
