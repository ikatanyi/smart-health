/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.sequence.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SequenceRequest {
    private String format;
    private Long current;
    private Long number;
}
