/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.data;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class CodesData {

    private String code;
    private String codeText;
    private String codeTextShort; //short code

    private String codeTypeKeyId;

    private boolean active;
}
