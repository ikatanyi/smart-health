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
 
@Data
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
}
