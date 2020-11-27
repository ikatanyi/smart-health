package io.smarthealth;

import javax.validation.constraints.NotNull;  
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Kelsas
 */ 
  
 
@Validated
@Configuration
@ConfigurationProperties(prefix = "io.smarthealth")
public class ApplicationProperties {

    /**
     * The base path where reports will be stored after compilation
     */
    @NotNull
    private Resource storageLocation;
    /**
     * The location of JasperReports source files
     */
    @NotNull
    private Resource reportLocation;
 
    private Resource authServer;
    
    private String reportLoc;
    
    private String docUploadDir;
    
    private String templateUploadDir;
    
    private String integServer;

    public Resource getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(Resource storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Resource getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(Resource reportLocation) {
        this.reportLocation = reportLocation;
    }

    public Resource getAuthServer() {
        return authServer;
    }

    public void setAuthServer(Resource authServer) {
        this.authServer = authServer;
    }

    public String getReportLoc() {
        return reportLoc;
    }

    public void setReportLoc(String reportLoc) {
        this.reportLoc = reportLoc;
    }

    public String getDocUploadDir() {
        return docUploadDir;
    }

    public void setDocUploadDir(String docUploadDir) {
        this.docUploadDir = docUploadDir;
    }

    public String getTemplateUploadDir() {
        return templateUploadDir;
    }

    public void setTemplateUploadDir(String templateUploadDir) {
        this.templateUploadDir = templateUploadDir;
    }

    public String getIntegServer() {
        return integServer;
    }

    public void setIntegServer(String integServer) {
        this.integServer = integServer;
    }
    
    
}
