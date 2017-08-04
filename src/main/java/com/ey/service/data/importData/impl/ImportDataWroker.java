package com.ey.service.data.importData.impl;

import java.util.List;
import java.util.concurrent.Callable;

import com.ey.service.system.importconfig.ImportConfigManager;
import com.ey.util.excel.ImportConfigParser;

public class ImportDataWroker implements Callable<Boolean>{
	// 导入文件类型
	private String importFileType;
	// 导入文件路径
	private String importFilePath;
	// 数据导入配置解析器
	private ImportConfigParser importConfigParser;
	// 数据导入配置服务
	private ImportConfigManager importConfigService;
	
	public ImportDataWroker(ImportConfigParser importConfigParser,ImportConfigManager importconfigService,String importFileType,String importFilePath){
		this.importConfigParser = importConfigParser;
		this.importConfigService = importconfigService;
		this.importFileType = importFileType;
		this.importFilePath = importFilePath;
	}

	@Override
	public Boolean call() throws Exception {
		// 获取数据导入配置
		List<String> importConfigs = importConfigService.findByImportFileType(importFileType);
		for(String importConfigCode : importConfigs){
			System.out.println("importConfigCode:"+importConfigCode);
		}
		return Boolean.TRUE;
	}

}
