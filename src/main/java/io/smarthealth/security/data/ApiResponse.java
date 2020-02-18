package io.smarthealth.security.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ApiResponse {

    private Boolean success;
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
