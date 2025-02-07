package com.ey.util.fileimport;

import java.io.File;

public abstract class FileImportor {

    public abstract ImportResult getImportResult(File file, String fileName) throws FileImportException;

    public abstract ImportResult getImportResult(File file, String fileName,boolean flag) throws FileImportException;

}
