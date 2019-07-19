/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ContentPage<T> {
    
    @JsonProperty("data")
    private List<T> contents;
    private Integer totalPages;
    private Long totalElements;
    
}
