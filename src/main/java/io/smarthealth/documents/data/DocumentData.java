/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.documents.data;

import io.smarthealth.documents.domain.Document;
import io.smarthealth.documents.domain.enumeration.DocumentType;
import io.smarthealth.documents.domain.enumeration.Status;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class DocumentData {
    private MultipartFile docfile;
    @ApiModelProperty(required=true, hidden=true)
    private String fileName; 
    private String patientNumber;
    private String patientName;
    private Long servicePointId;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    @ApiModelProperty(required=true, hidden=true)
    private String documentNumber;
    @ApiModelProperty(required=true, hidden=true)
    @Enumerated(EnumType.STRING)
    private Status status=Status.AwaitingReview; 
    private String notes;
    @ApiModelProperty(required=true, hidden=true)
    private String fileType;
    @ApiModelProperty(required=true, hidden=true)
    private Long size;
    @ApiModelProperty(required=true, hidden=true)
    private String fileDownloadUri;
    
    public Document fromData(){
        Document document = new Document();
        document.setNotes(notes);
        document.setDocumentType(this.getDocumentType());    
        document.setSize(this.getDocfile().getSize());
        document.setFileType(this.getDocfile().getContentType());
        return document;
    }
}
