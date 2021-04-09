package io.smarthealth.stat.data;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import lombok.Getter;
import org.springframework.http.ContentDisposition;

@Getter
public class Report {
    private byte[] report;
    private String fileName;
    private ExportFormat format;

    public Report(byte[] report, String fileName, ExportFormat format) {
        this.report = report;
        this.fileName = fileName;
        this.format = format;
    }

    public ContentDisposition getContentDisposition() {
        return ContentDisposition.builder("inline")
                .filename(fileName + "." + format).build();
    }

    public String getContentType() {
        String contentType = null;
        if(format == ExportFormat.PDF){
            contentType = "application/pdf";
        }else if( format == ExportFormat.XLSX){
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }else if ( format == ExportFormat.HTML){
            contentType = "text/html";
        }
        return contentType;
    }
}
