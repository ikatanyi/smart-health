/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.codes.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Data
public class CodeValues {

    private String name;
    private List<CodeValueData> values=new ArrayList<>();
   
   
}
