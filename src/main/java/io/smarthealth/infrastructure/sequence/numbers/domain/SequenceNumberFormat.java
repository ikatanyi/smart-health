package io.smarthealth.infrastructure.sequence.numbers.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.infrastructure.sequence.numbers.data.SequenceNumberFormatData;
import io.smarthealth.infrastructure.sequence.SequenceType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Deprecated
@Entity
@Data
public class SequenceNumberFormat extends Identifiable {

    @Enumerated(EnumType.STRING)
    private EntitySequenceType sequenceType;

    @Column(name = "prefix_value", length = 10, nullable = true)
    private String prefix;

    @Column(name = "suffix_value", length = 10, nullable = true)
    private String suffix;

    private int maxLength = 9;

    public SequenceNumberFormatData toData() {
        SequenceNumberFormatData data = new SequenceNumberFormatData();
        data.setSequenceType(sequenceType);
        data.setPrefix(prefix);
        data.setSuffix(suffix);
        data.setMaxLength(maxLength);

        return data;
    }
}
