package com.ey.entity.system;

import java.util.List;

import com.ey.util.fileimport.BaseModel;

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
	private String[] ignoreRule; // 行过滤规则
	private String[] nameSection; // 文件名解析段
	private String fileNameDelimiter; // 文件名分隔符
	private String callable; // 执行存储过程
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

	public String[] getIgnoreRule() {
		return ignoreRule;
	}

	public void setIgnoreRule(String[] ignoreRule) {
		this.ignoreRule = ignoreRule;
	}

	public String[] getNameSection() {
		return nameSection;
	}

	public void setNameSection(String[] strings) {
		this.nameSection = strings;
	}

	public String getFileNameDelimiter() {
		return fileNameDelimiter;
	}

	public void setFileNameDelimiter(String fileNameDelimiter) {
		this.fileNameDelimiter = fileNameDelimiter;
	}

	public String getCallable() {
		return callable;
	}

	public void setCallable(String callable) {
		this.callable = callable;
	}

	public List<ImportConfigCell> getImportCells() {
		return importCells;
	}

	public void setImportCells(List<ImportConfigCell> importCells) {
		this.importCells = importCells;
	}

	public enum ImportFileType {
		EXCEL,CSV
	}
}
