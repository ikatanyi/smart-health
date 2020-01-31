/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.lang;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FormatConverter implements Converter<String, ExportFormat> {

    @Override
    public ExportFormat convert(String source) {
        if (source == null) {
            return ExportFormat.PDF;
        }
        String upper = source.toUpperCase();
        try {
            return ExportFormat.valueOf(upper);
        } catch (Exception e) {
            return ExportFormat.PDF;
        }
    }

}
