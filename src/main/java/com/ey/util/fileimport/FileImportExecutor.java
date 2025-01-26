package com.ey.util.fileimport;

import java.io.File;
import com.ey.entity.system.ImportConfig;
import com.ey.util.csv.CsvImportor;
import com.ey.util.excel.ExcelImportor;

public class FileImportExecutor {

    public static ImportResult importFile(ImportConfig configuration, File file, String fileName) throws FileImportException{
        FileImportor fileImportor = getFileImportor(configuration);
        return fileImportor.getImportResult(file, fileName);
    }

    public static ImportResult importFileCsv(ImportConfig configuration, File file, String fileName) throws FileImportException{
        FileImportor fileImportor = getFileImportor(configuration);
        return fileImportor.getImportResult(file, fileName, true);
    }

    /**
     * 根据configuration里面 imporFileType返回
     * 默认 返回 ExcelImportor
     * @param configuration
     * @return
     */
    private static FileImportor getFileImportor(ImportConfig configuration) throws FileImportException {
        if (configuration.getImportFileType() == ImportConfig.ImportFileType.EXCEL) {
            return new ExcelImportor(configuration);
        } else if (configuration.getImportFileType() == ImportConfig.ImportFileType.CSV) {
        	return new CsvImportor(configuration);
        }
        throw new FileImportException("Can not find Importor. Please check importFileType in configuration");
    }

}
