package com.ey.util.excel;

import java.io.File;

import com.ey.entity.system.ImportConfig;

public class FileImportExecutor {
	
    public static ImportResult importFile(ImportConfig configuration, File file, String fileName) throws FileImportException{
        FileImportor fileImportor = getFileImportor(configuration);
        return fileImportor.getImportResult(file, fileName);
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
        }
        throw new FileImportException("Can not find Importor. Please check importFileType in configuration");
    }

}
