package io.smarthealth.infrastructure.utility;

import java.util.List;

/**
 *
 * @author Kelsas
 */
public class ListData<T> {

    private String code;
    private String message;
    private List<T> content;

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

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

}
