/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

import io.smarthealth.ApplicationProperties;
import io.smarthealth.administration.config.domain.GlobalConfigNum;
import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.domain.GlobalConfigurationRepository;
import io.smarthealth.documents.domain.Document;
import io.smarthealth.documents.domain.FileStorageRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.exception.FileStorageException;
import io.smarthealth.infrastructure.exception.MyFileNotFoundException;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
//@RequiredArgsConstructor
public class UploadService {

    private Path rootLocation;

    public String location;

    private final ApplicationProperties properties;
    private final SequenceNumberService sequenceNumberService;
    private final GlobalConfigurationRepository globalConfigurationRepository;
    private final FileStorageRepository fileStorageRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    public UploadService(ApplicationProperties properties, SequenceNumberService sequenceNumberService, GlobalConfigurationRepository globalConfigurationRepository, FileStorageRepository fileStorageRepository) throws IOException {
        this.properties = properties;
        this.sequenceNumberService = sequenceNumberService;
        this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath());
        this.globalConfigurationRepository = globalConfigurationRepository;
        this.fileStorageRepository = fileStorageRepository;
    }

//    public void init() {
//        try {
//            if(!Files.isDirectory(rootLocation, LinkOption.NOFOLLOW_LINKS))
//               Files.createDirectory(rootLocation);
//        } catch (IOException e) {
//            throw new StorageException("Could not initialize storage", e);
//        }
//    }
    public void UploadService() {
        GlobalConfiguration config = globalConfigurationRepository.findByName(GlobalConfigNum.PatientDocuments.name()).orElseThrow(() -> APIException.notFound("Patient documents folder {0} not set", GlobalConfigNum.PatientDocuments.name()));
        try {
            //this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath().concat("/").concat(dir));
            this.rootLocation = Paths.get(config.getValue());
            if (!Files.exists(rootLocation, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        UploadService();
        String documentNo = null;
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the Document No.)
            documentNo = sequenceNumberService.next(1L, Sequences.DocumentNumber.name()) + "." + FilenameUtils.getExtension(fileName);

            Path targetLocation = this.rootLocation.resolve(documentNo);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return documentNo;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + documentNo + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, String directory) {
        Path filePath = null;
        Resource resource = null;
        GlobalConfiguration config = globalConfigurationRepository.findByName(GlobalConfigNum.PatientDocuments.name()).orElseThrow(() -> APIException.notFound("Patient documents folder {0} not set", GlobalConfigNum.PatientDocuments.name()));
        try {
            this.rootLocation = Paths.get(config.getValue());
//        try {
//            this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath().concat("/").concat(directory));
            filePath = this.rootLocation.resolve(fileName).normalize();
            resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + filePath, ex);
        } catch (IOException ex) {
            throw new MyFileNotFoundException("File not found " + filePath, ex);
        }
        return resource;
    }

    public Resource loadPatientDocumentAsResource(final Long id) {
        Document document = fileStorageRepository.findById(id).orElseThrow(() -> APIException.notFound("Document identified by id {0} not found ", id));
        Path filePath = null;
        Resource resource = null;
        GlobalConfiguration config = globalConfigurationRepository.findByName(GlobalConfigNum.PatientDocuments.name()).orElseThrow(() -> APIException.notFound("Patient documents folder {0} not set", GlobalConfigNum.PatientDocuments.name()));
        try {
            this.rootLocation = Paths.get(config.getValue());
            filePath = this.rootLocation.resolve(document.getFileName()).normalize();
            resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + filePath, ex);
        } catch (IOException ex) {
            throw new MyFileNotFoundException("File not found " + filePath, ex);
        }
        return resource;
    }

}
