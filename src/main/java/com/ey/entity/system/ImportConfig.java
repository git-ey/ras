package com.ey.entity.system;

import java.util.List;

import com.ey.util.excel.BaseModel;

/**
 * 数据导入头配置
 * @author andyChen
 *
 */
public class ImportConfig extends BaseModel{
    
	private String importTempCode; // 导入模版代码
	private String importTempName; // 导入模板名称
	private Integer startRowNo; // 读取的起始行 起始为0
	private ImportFileType importFileType; // 导入文件类型
	private String tableName; // 导入目标表
	private String fileNameFormat; // 文件名格式
	private List<ImportConfigCell> importCells; // 导入行定义

	public String getImportTempCode() {
		return importTempCode;
	}

	public void setImportTempCode(String importTempCode) {
		this.importTempCode = importTempCode;
	}

	public String getImportTempName() {
		return importTempName;
	}

	public void setImportTempName(String importTempName) {
		this.importTempName = importTempName;
	}

	public ImportFileType getImportFileType() {
		return importFileType;
	}

	public void setImportFileType(ImportFileType importFileType) {
		this.importFileType = importFileType;
	}

	public Integer getStartRowNo() {
		return startRowNo;
	}

	public void setStartRowNo(Integer startRowNo) {
		this.startRowNo = startRowNo;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFileNameFormat() {
		return fileNameFormat;
	}

	public void setFileNameFormat(String fileNameFormat) {
		this.fileNameFormat = fileNameFormat;
	}

	public List<ImportConfigCell> getImportCells() {
		return importCells;
	}

	public void setImportCells(List<ImportConfigCell> importCells) {
		this.importCells = importCells;
	}

	public enum ImportFileType {
		EXCEL,DBF
	}
}
