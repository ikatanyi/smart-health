package io.smarthealth.administration.codes.data;

import io.smarthealth.administration.codes.domain.Code;
import lombok.Data;

import java.io.Serializable;

/**
 * Immutable data object representing a code.
 */
@Data
public class CodeData implements Serializable {

    private final Long id; 
    private final String name; 
    private final boolean systemDefined;

    public static CodeData instance(final Long id, final String name, final boolean systemDefined) {
        return new CodeData(id, name, systemDefined);
    }

    private CodeData(final Long id, final String name, final boolean systemDefined) {
        this.id = id;
        this.name = name;
        this.systemDefined = systemDefined;
    }

    public Long getCodeId() {
        return this.id;
    }
    public static CodeData map(Code code){
       return new CodeData(code.getId(), code.getName(), code.isSystemDefined());
    }
}