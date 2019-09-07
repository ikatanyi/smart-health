/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.text.MessageFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Simon.waweru
 */
@Data
public class APISuccess {

    private String title;
    private int status;
    @JsonIgnore
    private HttpStatus httpStatus;
    private String details;

    public APISuccess(String title, int status, String details) {
        this.title = title;
        this.status = status;
        this.details = details;
    }

    public static APISuccess successMessage(final String message, final Object... args) {
        return new APISuccess("Success", HttpStatus.OK.value(), MessageFormat.format(message, args));
    }

}
