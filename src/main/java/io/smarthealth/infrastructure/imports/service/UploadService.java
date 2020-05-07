/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.imports.service;

import io.smarthealth.ApplicationProperties;
import io.smarthealth.infrastructure.exception.FileStorageException;
import io.smarthealth.report.storage.StorageException;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

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

    @Autowired
    private ResourceLoader resourceLoader;
    
    
    public UploadService(ApplicationProperties properties) throws IOException {
        this.properties = properties;
        this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath());
    }


    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public void UploadService(String dir) {
        try {
            this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath().concat("/").concat(dir));
            Files.createDirectories(rootLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, String directory) {
        UploadService(directory);
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.rootLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, String directory) {
//        UploadService(directory);
        Resource resource = null;
        try {
            Path filePath = this.rootLocation.resolve(fileName).normalize();
            resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
//                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
//            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
        return resource;
    }
}
