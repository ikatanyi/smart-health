/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.documents.domain;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.documents.data.DocResponse;
import io.smarthealth.documents.data.DocumentData;
import io.smarthealth.documents.domain.enumeration.DocumentType;
import io.smarthealth.documents.domain.enumeration.Status;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "document")
public class Document extends Auditable {

    private String fileName;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_document_service_patient_id"))
    private Patient patient;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_document_service_point_id"))
    private ServicePoint servicePoint;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String documentNumber;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String notes;
    private String fileType;
    private Long size;

    public DocumentData toData() {
        DocumentData data = new DocumentData();
        data.setDocumentNumber(this.getDocumentNumber());
        data.setDocumentType(this.getDocumentType());
        data.setFileName(this.getFileName());
        data.setFileType(this.getFileType());
        data.setNotes(this.getNotes());
        if (this.getPatient() != null) {
            data.setPatientNumber(this.getPatient().getPatientNumber());
            data.setPatientName(this.getPatient().getFullName());
        }
        if (this.getServicePoint() != null) {
            data.setServicePointId(this.getServicePoint().getId());
        }

        data.setFileDownloadUri(getPublicUrl());
        data.setSize(this.getSize());
        data.setStatus(this.getStatus());
        return data;
    }

    public DocResponse toSimpleData() {
        DocResponse doc = new DocResponse();
        doc.setDocumentId(this.documentNumber);
        doc.setDocumentName(this.fileName);
        doc.setFileType(this.fileType);
        doc.setUrl(getPublicUrl());
        return doc;
    }

    public String getPublicUrl() {
        if (fileName != null) {

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/downloadFile/")
                    .path(this.servicePoint.getServicePointType().name() + "/")
                    .path(this.fileName)
                    .toUriString();
            return fileDownloadUri;
        }
        return null;
    }
}
