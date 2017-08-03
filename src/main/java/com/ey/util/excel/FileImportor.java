package com.ey.util.excel;

import java.io.File;

public abstract class FileImportor {

    public abstract ImportResult getImportResult(File file, String fileName) throws FileImportException;

}
