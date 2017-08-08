package com.ey.util.fileimport;

public class FileImportException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8634859151036314852L;

	public FileImportException(Throwable throwable, String message){
        super(message, throwable);
    }

    public FileImportException(String message) {
        super(message);
    }
}
