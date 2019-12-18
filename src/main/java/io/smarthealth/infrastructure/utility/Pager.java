package io.smarthealth.infrastructure.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;     

/**
 *
 * @author Kelsas
 */  
public class Pager<T> {

    private String code;
    private String message;
    private T content;
    @JsonInclude(Include.NON_NULL)
    private PageDetails pageDetails;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public PageDetails getPageDetails() {
        return pageDetails;
    }

    public void setPageDetails(PageDetails pageDetails) {
        this.pageDetails = pageDetails;
    }

}
