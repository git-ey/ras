package com.ey.util.excel;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.entity.system.ImportConfig;
import com.ey.entity.system.ImportConfigCell;
import com.ey.service.system.importconfig.ImportConfigCellManager;
import com.ey.service.system.importconfig.ImportConfigManager;
import com.ey.util.PageData;
import com.google.common.collect.Lists;

/**
 * 数据导入设置配置解析
 * @author andyChen
 *
 */
@Service("importConfigParser")
public class ImportConfigParser {
	
	@Resource(name="importConfigService")
	private ImportConfigManager importconfigService;
	@Resource(name="importConfigCellService")
	private ImportConfigCellManager importConfigCellService;
	
	public ImportConfig getConfig(String importTempCode) throws Exception{
    	ImportConfig importConfig = new ImportConfig();
    	// 查询数据导入配置头
    	PageData importConfigPd = importconfigService.findByCode(importTempCode);
    	if(!importConfigPd.isEmpty()){
    		List<ImportConfigCell> importCells = Lists.newArrayList();
    		// 查询数据导入配置行
        	List<PageData> importConfigCellPds = importConfigCellService.findByConfigId(importConfigPd.getString("IMPORTCONFIG_ID"));
    	    if(importConfigCellPds != null && !importConfigCellPds.isEmpty()){
    	    	for(PageData pd : importConfigCellPds){
    	    		ImportConfigCell importConfigCell = new ImportConfigCell();
    	    		importConfigCell.setNumber(Integer.parseInt(pd.get("NUMBER").toString()));
    	    		importConfigCell.setKey(pd.getString("MAPKEY"));
    	    		importConfigCell.setCellType(ImportConfigCell.CellType.getCellType(pd.getString("CELLTYPE")));
    	    		importConfigCell.setNullAble(ImportConfigCell.NullAble.getNullble(Integer.parseInt(pd.get("NULLABLE").toString())));
    	    		importCells.add(importConfigCell);
    	    	}
    	    }
    	    if(importCells.size() > 0){
        		// 导入模板代码
    	    	importConfig.setImportTempCode(importTempCode);
        		// 导入模板名称
    	    	importConfig.setImportTempName(importConfigPd.getString("IMPORT_TEMP_NAME"));
        		// 读取起始行
    	    	importConfig.setStartRowNo(Integer.parseInt(importConfigPd.get("START_ROW_NO").toString()));
        		// 导入文件类型
    	    	if(importConfigPd.getString("IMPORT_FILE_TYPE").equals("EXCEL")){
        			importConfig.setImportFileType(ImportConfig.ImportFileType.EXCEL);
        		}
    	    	// 导入目标表名称
        		importConfig.setTableName(importConfigPd.getString("TABLE_NAME"));
        		// 导入文件名筛选格式
        		importConfig.setFileNameFormat(importConfigPd.getString("FILENAME_FROMAT"));
        		// 导入行过滤规则
        		if(StringUtils.isNotBlank(importConfigPd.getString("IGNORE_RULE"))){
            		importConfig.setIgnoreRule(importConfigPd.getString("IGNORE_RULE").split(","));
        		}
        		// 导入文件名段分割符及解析段
        		if(StringUtils.isNoneBlank(importConfigPd.getString("NAME_SECTION"))){
            		importConfig.setFileNameDelimiter(this.getStringDelimiter(importConfigPd.getString("NAME_SECTION")));
            		importConfig.setNameSection(importConfigPd.getString("NAME_SECTION").split(importConfig.getFileNameDelimiter()));
        		}
        		// 导入文件列字段
        		importConfig.setImportCells(importCells);
    	    }
    	    // 返回数据导入配置
    	    return importConfig;
    	}
    	return importConfig;
    }
	
	/**
	 * 获取字符串分隔符
	 * @param nameSection
	 * @return
	 */
	private String getStringDelimiter(String nameSection){
		String delimiter = ",";
		for(String str : nameSection.split("[^\\D]")){
			if(StringUtils.isNotBlank(str)){
				delimiter = str;
				break;
			}
		}
		return delimiter;
	}
}
