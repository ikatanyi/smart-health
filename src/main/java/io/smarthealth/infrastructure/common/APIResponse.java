/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Simon.waweru
 */
@Data
public class APIResponse {

    private int status;
    @JsonIgnore
    private HttpStatus httpStatus;
    private String message;
    private boolean success;
    private Object data;

    public APIResponse(boolean success, String message, int status, Object data) {
        this.status = status;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public APIResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static APIResponse successMessage(String message, HttpStatus status, Object payload) {
        return new APIResponse(true, message, status.value(), payload);
    }

    public static APIResponse errorMessage(String message, HttpStatus status, Object payload) {
        return new APIResponse(false, message, status.value(), payload);
    }

}
