package com.ey.service.data.fileimport.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.ey.entity.system.ImportConfig;
import com.ey.service.data.fileimport.ImportManager;
import com.ey.service.system.importconfig.ImportConfigManager;
import com.ey.util.FileUtil;
import com.ey.util.Logger;
import com.ey.util.PageData;
import com.ey.util.Tools;
import com.ey.util.UuidUtil;
import com.ey.util.fileimport.FileImportException;
import com.ey.util.fileimport.FileImportExecutor;
import com.ey.util.fileimport.ImportConfigParser;
import com.ey.util.fileimport.MapResult;
import com.ey.util.fileimport.XlsToCsv;
import com.ey.util.fileimport.XlsxToCsv;

public class ImportDataWroker implements Callable<Boolean> {

	protected Logger logger = Logger.getLogger(ImportDataWroker.class);
	/**
	 * 插入数据的阀值数
	 */
	private final int INSERT_COUNT = 500;
	/**
	 * 导入文件ID标识
	 */
	private final String IMPORT_FILE_ID = "`import_file_id`";
	private final int NAME_SEG_CNT = 6;
	// 数据导入配置解析器
	private ImportConfigParser importConfigParser;
	// 数据导入配置服务
	private ImportConfigManager importConfigService;
	// 数据导入工作台服务
	private ImportManager importService;
	// 数据导入工作台数据
	private PageData pd;

	public ImportDataWroker(ImportConfigParser importConfigParser, ImportConfigManager importconfigService,
			ImportManager importService, PageData pd) {
		this.importConfigParser = importConfigParser;
		this.importConfigService = importconfigService;
		this.importService = importService;
		this.pd = pd;
	}

	@Override
	public Boolean call() throws Exception {
		Boolean result = Boolean.TRUE;
		String importMessage = null;
		// 导入数据文件配置代码
		String importTempCode = StringUtils.isBlank(pd.getString("IMPORT_TEMP_CODE")) ? null
				: pd.getString("IMPORT_TEMP_CODE");
		// 获取数据导入配置
		List<String> importConfigs = importConfigService.findByImportTempCode(importTempCode);
		if (importConfigs == null || importConfigs.size() == 0) {
			this.updateImportStatus("N", "未找到有效的导入设置");
			return result;
		}
		// 处理Excel过程
		try {
			this.saveFileData(importConfigs);
		} catch (Exception e) {
			importMessage = e.getMessage();
		}
		// 完成导入操作回写信息
		if (StringUtils.isNotBlank(importMessage)) {
			this.updateImportStatus("N", importMessage);
		} else {
			this.updateImportStatus("Y", importMessage);
		}
		return result;
	}

	/**
	 * 导入文件数据
	 * 
	 * @return
	 * @throws Exception
	 */
	private void saveFileData(List<String> importConfigs) throws Exception {
		for (String importTempCode : importConfigs) {
			ImportConfig configuration = null;
			try {
				configuration = importConfigParser.getConfig(importTempCode);
			} catch (Exception e) {
				throw new Exception("获取数据导入配置失败:" + importTempCode + "," + e.getMessage());
			}
			// 配置文件没有设置则跳出处理（通过起始行是否为空来鉴别）
			if (configuration == null || configuration.getStartRowNo() == null) {
				throw new Exception("配置代码：" + importTempCode + ",数据导入信息维护不完整");
			}
			List<File> pathFiles = FileUtil.getPathFile(pd.getString("IMPORT_FILE_PATH"),
					configuration.getFileNameFormat());
			// 遍历导入文件
			for (File pathFile : pathFiles) {
				// 导入消息
				String importMessage = null;
				// 获取导入文件记录ID
				String importFileId = UuidUtil.get32UUID();// 导入数据文件ID
				// 初始化导入条数
				Long cnt = 1L;
				int[] nameSection = null;
				// 校验文件是否已导入
				if (!checkFileExsit(pathFile.getName())) {
					// 获取文件名解析段
					nameSection = this.getFileNameSeg(configuration.getNameSection());
					if (configuration.getImportFileType() == ImportConfig.ImportFileType.EXCEL) {
						// 解析并导入Excel文件
						try {
							cnt = this.insertExcelFile(pathFile, configuration, importFileId);
						} catch (Exception e) {
							importMessage = com.ey.util.StringUtil.getStringByLength(e.getMessage(),480);
						}
					} else if (configuration.getImportFileType() == ImportConfig.ImportFileType.CSV) {
						// 解析并导入CSV文件
						try {
							cnt = this.insertCsvFile(pathFile, configuration, importFileId);
						} catch (Exception e) {
							importMessage = com.ey.util.StringUtil.getStringByLength(e.getMessage(),480);
						}
					}
				} else {
					importMessage = "文件已存在,不能重复导入";
				}
				// 文件正常导入后，执行存储过程
				if(StringUtils.isBlank(importMessage) && StringUtils.isNotBlank(configuration.getCallable())){
					this.callProcedure(configuration.getCallable(), importFileId);
				}
				// 回写导入文件信息表
				try {
					this.saveImportFile(importFileId, pd.get("IMPORT_ID").toString(), pathFile.getName(), nameSection,
							configuration.getFileNameDelimiter(), configuration.getTableName(), importMessage, cnt);
				} catch (Exception e) {
					throw new Exception("回写导入文件信息表失败:" + com.ey.util.StringUtil.getStringByLength(e.getMessage(),480));
				}
			}
		}
	}

	/**
	 * 检查导入文件是否已导入
	 * 
	 * @param pathFiles
	 * @return
	 * @throws Exception
	 */
	private Boolean checkFileExsit(String pathFile) throws Exception {
		Long filrCnt = importService.findFileCount(pathFile);
		if (filrCnt > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 导入CSV数据文件
	 * 
	 * @param pathFile
	 * @param configuration
	 * @throws Exception
	 */
	private Long insertCsvFile(File pathFile, ImportConfig configuration, String importFileId) throws Exception {
		String fileName = pathFile.getName();
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
		String csvFilePath = pathFile.getParent() + File.separator + UuidUtil.get32UUID() + ".csv";
		String excelFilePath = pathFile.getPath();
		switch (extension) {
		case "xls":
			// 先执行Excel转换成CSV
			XlsToCsv xlsTocsv = new XlsToCsv(excelFilePath, csvFilePath);
			xlsTocsv.process();
			break;
		case "xlsx":
			// 先执行Excel转换成CSV
			XlsxToCsv xlsxTocsv = new XlsxToCsv(excelFilePath, csvFilePath);
			xlsxTocsv.process();
			break;
		case "csv":
			csvFilePath = excelFilePath;
			break;
		}
		MapResult mapResult = null;
		File csvFile = new File(csvFilePath);
		if(!csvFile.exists()){
			throw new Exception(excelFilePath+" 未生成临时的CSV文件");
		}
		try {
			mapResult = (MapResult) FileImportExecutor.importFile(configuration, csvFile, csvFile.getName());
		} catch (FileImportException e) {
			throw new Exception("导入CSV文件数据失败:" + csvFile.getName() + "," + e.getMessage());
		}
		Long cnt = this.insertData(mapResult, configuration, excelFilePath, importFileId);
		// 删除CSV临时过渡文件，删除不了，强制回收
		boolean result = csvFile.delete();
		if(!result){
			System.gc();//系统进行资源强制回收
			csvFile.delete();
		}
		return cnt;
	}

	/**
	 * 导入Excel数据文件
	 * 
	 * @param pathFile
	 * @param configuration
	 * @throws Exception
	 */
	private Long insertExcelFile(File pathFile, ImportConfig configuration, String importFileId) throws Exception {
		MapResult mapResult = null;
		try {
			mapResult = (MapResult) FileImportExecutor.importFile(configuration, pathFile, pathFile.getName());
		} catch (FileImportException e) {
			throw new Exception("导入Excel文件数据失败:" + pathFile.getName() + "," + e.getMessage());
		}
		return this.insertData(mapResult, configuration, pathFile.getPath(), importFileId);
	}

	/**
	 * 拼接SQL插入脚本并插入数据库
	 * 
	 * @param mapResult
	 * @param configuration
	 * @param fileName
	 * @param importFileId
	 * @throws Exception
	 */
	private Long insertData(MapResult mapResult, ImportConfig configuration, String fileName, String importFileId)
			throws Exception {
		// 判断是否存在错误，存在错误则整个文件不处理
		if (StringUtils.isNotBlank(mapResult.getResMsg())) {
			throw new Exception(fileName + "数据存在错误:" + mapResult.getResMsg());
		}
		List<Map> maps = mapResult.getResult();
		Long cnt = 1L; // 数据插入处理计数器
		StringBuilder sbf = new StringBuilder();
		StringBuilder sbv = new StringBuilder();
		for (Map<String, Object> map : maps) {
			Set<Entry<String, Object>> setMaps = map.entrySet();
			sbv.append("(");
			for (Entry<String, Object> et : setMaps) {
				// 组装插入表属性字段,排除用于Excel特殊梳理的两个KEY
				if (!et.getKey().equals("lineNum") && !et.getKey().equals("isLineLegal")) {
					if (cnt == 1) {
						sbf.append("`" + et.getKey() + "`,");
					}
					sbv.append(et.getValue() + ",");
				}
			}
			if (cnt == 1) {
				sbf.append(IMPORT_FILE_ID);
			}
			sbv.append("'" + importFileId + "'),"); // 绑定导入文件ID
			// 100次保存一次
			if (cnt % INSERT_COUNT == 0) {
				String tableValue = sbv.toString().replace(",)", ")").replace("\"", "'");
				// 插入数据
				try {
					this.saveImportFileData(configuration.getTableName(), sbf.toString(),
							tableValue.substring(0, tableValue.length() - 1));
				} catch (Exception e) {
					throw new Exception("插入数据表失败:" + configuration.getTableName() + "," + com.ey.util.StringUtil.getStringByLength(e.getMessage(),480));
				}
				// 重新初始化
				sbv = new StringBuilder();
				logger.debug(cnt + "条数据插入");
			}
			cnt++;
		}
		String tableValue = sbv.toString().replace(",)", ")").replace("\"", "'");
		// 插入数据库对应表
		if (StringUtils.isNotBlank(tableValue)) {
			try {
				this.saveImportFileData(configuration.getTableName(), sbf.toString(),
						tableValue.substring(0, tableValue.length() - 1));
			} catch (Exception e) {
				throw new Exception("插入数据表失败:" + configuration.getTableName() + "," + com.ey.util.StringUtil.getStringByLength(e.getMessage(),480));
			}
		}
		return cnt;
	}
	
	/**
	 * 执行存储过程
	 * @param callable
	 * @return
	 * @throws Exception 
	 */
	private String callProcedure(String callable,String importFileId) throws Exception{
		PageData procedurePd = new PageData();
		procedurePd.put("PROCEDURE_NAME", callable);
		procedurePd.put("IMPORTFILEID", importFileId);
		procedurePd.put("RETSTR", "");
		return importService.callableProcedure(procedurePd);
	}

	/**
	 * 保存解析文件数据至数据库
	 * 
	 * @param tableName
	 * @param tableFiled
	 * @param tableValue
	 * @throws Exception
	 */
	private void saveImportFileData(String tableName, String tableFiled, String tableValue) throws Exception {
		PageData importDataPd = new PageData();
		importDataPd.put("TABLE_NAME", tableName);
		importDataPd.put("TABLE_FILED", tableFiled);
		importDataPd.put("TABLE_VALUE", tableValue);
		importService.saveImportData(importDataPd);
	}

	/**
	 * 保存导入表信息数据至数据库
	 * 
	 * @param importFileId
	 * @param importId
	 * @param pathFile
	 * @param tableName
	 * @param cnt
	 * @throws Exception
	 */
	private void saveImportFile(String importFileId, String importId, String pathFileName, int[] nameSection,
			String fileNameDelimiter, String tableName, String message, Long cnt) throws Exception {
		PageData importFilePd = new PageData();
		importFilePd.put("IMPORT_FILE_ID", importFileId);
		importFilePd.put("IMPORT_ID", importId);
		importFilePd.put("IMPORT_FILE_NAME", pathFileName);
		// 文件名解析,配置解析规则的才处理
		if (nameSection != null && nameSection.length > 0) {
			this.fileNameParser(importFilePd, pathFileName, nameSection, fileNameDelimiter);
		}
		importFilePd.put("TABLE_NAME", tableName);
		importFilePd.put("MESSAGE", message);
		importFilePd.put("CNT", (cnt - 1));
		importService.saveImportFile(importFilePd);
	}

	/**
	 * 文件名分析器
	 * 
	 * @param importFilePd
	 * @param pathFileName
	 * @param nameSection
	 */
	private void fileNameParser(PageData importFilePd, String pathFileName, int[] nameSection,
			String fileNameDelimiter) {
		String[] fileNames = pathFileName.substring(0, pathFileName.lastIndexOf(".")).split(fileNameDelimiter);
		int idx = 1;
		for (int ni : nameSection) {
			// 最大支持的段数
			if (idx == NAME_SEG_CNT) {
				break;
			}
			if (ni <= fileNames.length) {
				importFilePd.put("NAME_SEG_" + ni, fileNames[ni - 1]);
			}
			idx++;
		}
	}

	/**
	 * 获取继续文件名段索引
	 * 
	 * @param nameSection
	 * @return
	 */
	private int[] getFileNameSeg(String[] nameSection) {
		int[] segIdx = new int[nameSection.length];
		int i = 0;
		for (String ns : nameSection) {
			segIdx[i] = Integer.parseInt(ns);
			i++;
		}
		return segIdx;
	}

	/**
	 * 回写导入操作状态
	 * 
	 * @param importStatus
	 * @throws Exception
	 */
	private void updateImportStatus(String importStatus, String importMessage) throws Exception {
		pd.put("END_DATETIME", Tools.date2Str(new Date()));
		pd.put("IMPORT_STATUS", importStatus);
		pd.put("MESSAGE", importMessage);
		importService.edit(pd);
	}

}
