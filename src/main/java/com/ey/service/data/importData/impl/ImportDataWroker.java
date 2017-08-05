package com.ey.service.data.importData.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.ey.entity.system.ImportConfig;
import com.ey.service.data.importData.ImportManager;
import com.ey.service.system.importconfig.ImportConfigManager;
import com.ey.util.FileUtil;
import com.ey.util.PageData;
import com.ey.util.Tools;
import com.ey.util.UuidUtil;
import com.ey.util.excel.FileImportException;
import com.ey.util.excel.FileImportExecutor;
import com.ey.util.excel.ImportConfigParser;
import com.ey.util.excel.MapResult;

public class ImportDataWroker implements Callable<Boolean> {
	/**
	 * 插入数据的阀值数
	 */
	private final int INSERT_COUNT = 2;
	/**
	 * 导入文件ID标识
	 */
	private final String IMPORT_FILE_ID = "`import_file_id`";
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
		// 导入数据文件类型
		String importFileType = pd.getString("IMPORT_FILE_TYPE");
		// 获取数据导入配置
		List<String> importConfigs = importConfigService.findByImportFileType(importFileType);
		if (importConfigs == null || importConfigs.size() == 0) {
			this.updateImportStatus("N", "未找到有效的导入设置");
			return result;
		}
		// 处理Excel过程
		if (importFileType.equals("EXCEL")) {
			try {
				this.saveExcelData(importConfigs);
			} catch (Exception e) {
				importMessage = e.getMessage();
			}
		}
		if (importFileType.equals("DBF")) {
			// 处理DBF数据
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
	 * 导入Excel数据
	 * 
	 * @return
	 * @throws Exception
	 */
	private void saveExcelData(List<String> importConfigs) throws Exception {
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
			List<String> pathFiles = FileUtil.getPathFile(pd.getString("IMPORT_FILE_PATH"),
					configuration.getFileNameFormat());
			if (pathFiles == null || pathFiles.size() == 0) {
				throw new Exception("未找到可处理的文件");
			}
			// 校验文件是否已导入
			if(!cehckFileExsits(pathFiles)){
				throw new Exception("存在已导入过的文件");
			}
			for (String pathFile : pathFiles) {
				String importMessage = null;
				// 获取导入文件记录ID
				String importFileId = UuidUtil.get32UUID();// 导入数据文件ID
				// 解析并导入Excel文件
				int cnt = 1;
				try {
					cnt = this.importExcelFile(pathFile, configuration, importFileId);
				} catch (Exception e) {
					importMessage = e.getMessage();
				}
				// 回写导入文件信息表
				try {
					this.saveImportFile(importFileId, pd.get("IMPORT_ID").toString(), pathFile,
							configuration.getTableName(), importMessage, cnt);
				} catch (Exception e) {
					throw new Exception("回写导入文件信息表失败:" + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 检查导入文件是否已导入
	 * @param pathFiles
	 * @return
	 * @throws Exception
	 */
	private Boolean cehckFileExsits(List<String> pathFiles) throws Exception{
		Long filrCnt = importService.findFileCount(pathFiles);
		if(filrCnt > 0){
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * 导入Excel数据文件
	 * 
	 * @param pathFile
	 * @param configuration
	 * @throws Exception
	 */
	private int importExcelFile(String pathFile, ImportConfig configuration, String importFileId) throws Exception {
		File importFile = new File(pathFile);
		MapResult mapResult = null;
		try {
			mapResult = (MapResult) FileImportExecutor.importFile(configuration, importFile, importFile.getName());
		} catch (FileImportException e) {
			throw new Exception("导入Excel文件数据失败:" + importFile.getName() + "," + e.getMessage());
		}
		List<Map> maps = mapResult.getResult();
		int cnt = 1; // 数据插入处理计数器
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
					sbv.append("'" + et.getValue() + "',");
				}
			}
			if (cnt == 1) {
				sbf.append(IMPORT_FILE_ID);
			}
			sbv.append("'" + importFileId + "'),"); // 绑定导入文件ID
			// 100次保存一次
			if (cnt % INSERT_COUNT == 0) {
				String tableValue = sbv.toString().replace(",)", ")");
				// 插入数据
				if (StringUtils.isNotBlank(configuration.getTableName())) {
					try {
						this.saveImportFileData(configuration.getTableName(), sbf.toString(),
								tableValue.substring(0, tableValue.length() - 1));
					} catch (Exception e) {
						throw new Exception(
								"插入数据表失败:" + configuration.getTableName() + "," + e.getMessage());
					}
				}
				// 重新初始化
				sbv = new StringBuilder();
			}
			cnt++;
		}
		String tableValue = sbv.toString().replace(",)", ")");
		// 插入数据库对应表
		if (StringUtils.isNotBlank(configuration.getTableName()) && StringUtils.isNotBlank(tableValue)) {
			try {
				this.saveImportFileData(configuration.getTableName(), sbf.toString(),
						tableValue.substring(0, tableValue.length() - 1));
			} catch (Exception e) {
				throw new Exception("插入数据表失败:" + configuration.getTableName() + "," + e.getMessage());
			}
		}
		return cnt;
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
	private void saveImportFile(String importFileId, String importId, String pathFile, String tableName, String message,
			Integer cnt) throws Exception {
		PageData importFilePd = new PageData();
		importFilePd.put("IMPORT_FILE_ID", importFileId);
		importFilePd.put("IMPORT_ID", importId);
		importFilePd.put("IMPORT_FILE_NAME", pathFile);
		importFilePd.put("TABLE_NAME", tableName);
		importFilePd.put("MESSAGE", message);
		importFilePd.put("CNT", (cnt - 1));
		importService.saveImportFile(importFilePd);
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
