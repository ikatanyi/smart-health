/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.jobs.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class UpdateJobDetailData {
     private final String displayName; 
    private final String cronExpression;
    private final boolean active;
}
