package io.smarthealth.infrastructure.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;    
import lombok.Data;

/**
 *
 * @author Kelsas
 */   
@Data
public class Pager<T> {

    private String code;
    private String message;
    private T content;
    @JsonInclude(Include.NON_NULL)
    private PageDetails pageDetails;

}
