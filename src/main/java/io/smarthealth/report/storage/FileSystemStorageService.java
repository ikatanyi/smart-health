 package io.smarthealth.report.storage;
 
import io.smarthealth.ApplicationProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

/**
 * @author Kennedy.ikatanyi
 *
 */
@Slf4j
@Component
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final ApplicationProperties properties;

    public FileSystemStorageService(ApplicationProperties properties) throws IOException {
        this.properties = properties;
        this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath());
    }

    @Override
    public void init() {
        try {
            if(Files.notExists(rootLocation))
                Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            FileSystemUtils.deleteRecursively(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not delete files and folders", e);
        }
    }

    @Override
    public boolean jrxmlFileExists(String file) {
        // @formatter:off
        try {
            Path reportFile = Paths.get(properties.getReportLocation().getURI());
            reportFile = reportFile.resolve(file + ".jrxml");
            if (Files.exists(reportFile)) {
                return true;
            }
        } catch (IOException e) {
            log.error("Error while trying to get file URI", e);
            return false;
        }
        // @formatter:on
        return false;
    }

    @Override
    public boolean jasperFileExists(String file) {
        Path reportFile = rootLocation;
        reportFile = reportFile.resolve(file + ".jasper");
        if (Files.exists(reportFile)) {
            return true;
        }
        return false;
    }

    @Override
    public String loadJrxmlFile(String file) {
        // @formatter:off
        try {
            Path reportFile = Paths.get(properties.getReportLocation().getURI());
            reportFile = reportFile.resolve(file + ".jrxml");
            return reportFile.toString();
        } catch (IOException e) {
            log.error("Error while trying to get file prefix", e);
            throw new StorageFileNotFoundException("Could not load file", e);
        }
        // @formatter:on
    }

    @Override
    public File loadJasperFile(String file) {
        Path reportFile = rootLocation;
        reportFile = reportFile.resolve(file + ".jasper");
        return reportFile.toFile();
    }

}
