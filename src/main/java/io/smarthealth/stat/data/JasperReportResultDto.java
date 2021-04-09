package io.smarthealth.stat.data;

import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.Serializable;
@Getter
@Setter
public class JasperReportResultDto implements Serializable {

    private JasperPrint jasperPrint;
    private String jasperFile;
    private String filename;

    public JasperReportResultDto(JasperPrint jasperPrint, String jasperFile, String filename) {
        this.jasperPrint = jasperPrint;
        this.jasperFile = jasperFile;
        this.filename = filename;

    }
}
