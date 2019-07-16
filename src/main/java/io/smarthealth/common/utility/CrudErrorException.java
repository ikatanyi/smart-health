/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.common.utility;

//import javax.ejb.ApplicationException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 *
 * @author simon.waweru
 */
@ControllerAdvice
public class CrudErrorException extends RuntimeException {

    public CrudErrorException(String message) {
        super(message);
    }

    public CrudErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
