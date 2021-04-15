package io.smarthealth.infrastructure.utility;

import io.smarthealth.administration.config.domain.GlobalConfiguration;
import io.smarthealth.administration.config.service.ConfigService;
import io.smarthealth.approval.api.ApprovalsConfigurationController;
import io.smarthealth.messaging.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableScheduling
public class DBAutoBackup {
    //TODO: Fetch db parameters from appproperties files
    String DB_USER = "smarthealth";
    String DB_PASSWORD = "Sm@rt_123";
    String DB_NAME_LIST = "smarthealth";


    @Autowired
    EmailService emailService;

    @Autowired
    ConfigService configService;

    //To enable and disable backup services, just in case - Kelsas
    //@Value("${backup.job.enabled:false}")
    //private boolean isEnabled;

        @Scheduled(cron = "0 25 22 * * ?")
//    @Scheduled(fixedRate = 100000)
    public void schedule() {
        // if(isEnabled) {
             System.out.println("Backup Started at " + new Date());

             Date backupDate = new Date();
             SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
             String backupDateStr = format.format(backupDate);

             String fileName = "Daily_DB_Backup"; // default file name
             GlobalConfiguration saveBackUPFolderTO = configService.getByNameOrThrow("SaveBackUPFolderTO");


             String folderPath = saveBackUPFolderTO.getValue();
             File f1 = new File(folderPath);
             f1.mkdir(); // create folder if not exist

             String saveFileName = fileName + "_" + backupDateStr + ".sql";
             String savePath = folderPath + File.separator + saveFileName;

             String executeCmd = "mysqldump -u " + DB_USER + " -p" + DB_PASSWORD + "  --databases " + DB_NAME_LIST
                     + " -r " + savePath;

             Process runtimeProcess = null;
             try {
                 runtimeProcess = Runtime.getRuntime().exec(executeCmd);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             int processComplete = 0;
             try {
                 processComplete = runtimeProcess.waitFor();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }

             if (processComplete == 0) {
                 System.out.println("Backup Complete at " + new Date());
                 File file = new File(folderPath + "/" + saveFileName);

                 //zip the file
                 String zipFileName = folderPath + "/" + saveFileName.replace(".sql", ".zip");
                 File generatedZipFile = new File(zipFileName);
                 zipSingleFile(file, zipFileName);

                 Optional<GlobalConfiguration> sendBackUpToConfig = configService.findByName("SendBackUpTo");
                 String sendTo = "";
                 if (sendBackUpToConfig.isPresent()) {
                     sendTo = sendBackUpToConfig.get().getValue();
                 } else {
                     return;
                 }

                 //send mail
                 System.out.println("About to send email ");
                 emailService.sendMailWithAttachment(sendTo, saveFileName, "Please find attached database backup of " + saveFileName,
                         zipFileName, saveFileName.replace(".sql", ".zip"), "application/zip");
             } else {
                 System.out.println("Backup Failure");
             }
       //  }
    }

    private void zipSingleFile(File file, String zipFileName) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            //Close the zip entry to write to zip file
            zos.closeEntry();
            //Close resources
            zos.close();
            fis.close();
            fos.close();
            System.out.println(file.getCanonicalPath() + " is zipped to " + zipFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
