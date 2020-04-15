/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.documents.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
//@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String docUploadDir;
}
