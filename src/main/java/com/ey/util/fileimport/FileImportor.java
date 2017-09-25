package com.ey.util.fileimport;

import java.io.File;

public abstract class FileImportor {

    public abstract ImportResult getImportResult(File file, String fileName) throws FileImportException;

}
