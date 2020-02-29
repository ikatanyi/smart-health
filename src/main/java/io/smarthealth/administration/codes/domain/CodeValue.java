package io.smarthealth.administration.codes.domain;

import io.smarthealth.administration.codes.data.CodeValueData;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ref_code_value")
public class CodeValue extends Identifiable {

    @Enumerated(EnumType.STRING)
    private Code code;
    @Column(name = "code_value", length = 100)
    private String codeValue;
    @Column(name = "order_position")
    private int position;
    private boolean isActive;

    public CodeValueData toData() {
        CodeValueData data = new CodeValueData();
        data.setId(this.getId());
        data.setActive(this.isActive);
        data.setCode(this.code);
        data.setCodeValue(this.codeValue);
        data.setPosition(this.position);
        return data;
    }
}
