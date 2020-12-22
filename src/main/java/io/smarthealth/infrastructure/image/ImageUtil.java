/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.image;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.infrastructure.exception.APIException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Kennedy.Imbenzi
 */
public class ImageUtil {

    @Autowired
    ClinicalImageRepository portraitRepository;

    private File ImageDirRoot;

    @Value("${upload.image.max-size:10242880}")
    Long maxSize;

    @Value("${clinical.image.upload.dir}")
    private String imageUploadDir ;
            /**
             * Delete patient's photo
             */
            //    @Autowired
            //    ImageUtil( @Value("${clinical.image.upload.dir}") String uploadDir) {
            //        this.ImageDirRoot = new File(uploadDir);
            //    }


    public Boolean deleteImage(String potraitName) throws IOException {
        //remove file if exists on folder
        /*Delete patient file*/

        ImageDirRoot = new File(imageUploadDir);
        Boolean state = false;
        System.out.println("-------------Deleting patient portrait--------------");
        File file = new File(this.ImageDirRoot, potraitName);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully");
                state = true;
            } else {
                System.out.println("Fail to delete file");
            }
        }
        //delete from directory

        return state;
    }

    @Transactional
    public ClinicalImage createImage(PatientScanTest scanTest, MultipartFile file) throws IOException {
        if (file == null) {
            return null;
        }

        throwIfInvalidSize(file.getSize());
        throwIfInvalidContentType(file.getContentType());
        File fileForPatient = new File(imageUploadDir, file.getOriginalFilename());

        try (
                InputStream in = file.getInputStream();
                OutputStream out = new FileOutputStream(fileForPatient)) {
            FileCopyUtils.copy(in, out);
            ClinicalImage portrait = new ClinicalImage();
            portrait.setContentType(file.getContentType());
            portrait.setSize(file.getSize());
            portrait.setImageUrl(imageUploadDir);
            portrait.setImageName(file.getOriginalFilename());
            portrait = portraitRepository.save(portrait);
            return portrait;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void throwIfInvalidSize(final Long size) {

        if (size > maxSize) {
            throw APIException.badRequest("Image can''t exceed size of {0}", maxSize);
        }
    }

    private void throwIfInvalidContentType(final String contentType) {
        if (!contentType.contains(MediaType.IMAGE_JPEG_VALUE)
                && !contentType.contains(MediaType.IMAGE_PNG_VALUE)) {
            throw APIException.badRequest("Only content type {0} and {1} allowed", MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
        }
    }

}
