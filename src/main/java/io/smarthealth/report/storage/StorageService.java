package io.smarthealth.report.storage;

import java.io.File;

/**
 *
 * @author Kelsas
 */
public interface StorageService {

    void init();

    void deleteAll();

    boolean jrxmlFileExists(String file);

    boolean jasperFileExists(String file);

    String loadJrxmlFile(String file);

    File loadJasperFile(String file);

}
