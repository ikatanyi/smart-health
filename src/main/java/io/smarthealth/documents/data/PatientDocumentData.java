package io.smarthealth.documents.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.documents.domain.Document;
import io.smarthealth.documents.domain.enumeration.DocumentType;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jboss.logging.annotations.Message;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;

@Data
public class PatientDocumentData {
    @NotNull(message = "File object cannot be empty/null")
    private MultipartFile file;
    private String name;
    private String patientNumber;
    private String visitNumber;
    private String comments;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
    private LocalDate date;

    @ApiModelProperty(hidden = true)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType = DocumentType.General;

    @ApiModelProperty(required=false, hidden=true)
    private String fileType;
    @ApiModelProperty(required=false, hidden=true)
    private Long size;
    @ApiModelProperty(required=false, hidden=true)
    private String fileDownloadUri;
    @ApiModelProperty(hidden = true)
    private String patientName;

    @ApiModelProperty(hidden = true)
    private Long id;

    public Document fromData(){
        Document document = new Document();
        document.setNotes(this.comments);
        document.setDocumentType(DocumentType.General);
        document.setDocumentDate(this.getDate());
        document.setFileName(this.getName());
        if(this.getFile()!=null){
            document.setSize(this.getFile().getSize());
            document.setFileType(this.getFile().getContentType());
        }
        return document;
    }

}
