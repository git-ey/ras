package com.ey.util.excel;

import java.util.List;

import javax.annotation.Resource;

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
    	    	ImportConfigCell importConfigCell = new ImportConfigCell();
    	    	for(PageData pd : importConfigCellPds){
    	    		importConfigCell.setNumber(Integer.parseInt(pd.getString("NUMBER")));
    	    		importConfigCell.setKey(pd.getString("MAPKEY"));
    	    		importConfigCell.setCellType(ImportConfigCell.CellType.getCellType(pd.getString("CELLTYPE")));
    	    		importConfigCell.setNullAble(ImportConfigCell.NullAble.getNullble(Integer.parseInt(pd.getString("NULLABLE"))));
    	    		importCells.add(importConfigCell);
    	    	}
    	    }
    	    if(importCells.size() > 0){
        		importConfig.setImportTempCode(importTempCode);
        		importConfig.setImportTempName(importConfigPd.getString("IMPORT_TEMP_NAME"));
        		importConfig.setStartRowNo(Integer.parseInt(importConfigPd.getString("START_ROW_NO")));
        		if(importConfigPd.getString("IMPORT_FILE_TYPE").equals("DBF")){
        			importConfig.setImportFileType(ImportConfig.ImportFileType.DBF);
        		}else{
        			importConfig.setImportFileType(ImportConfig.ImportFileType.EXCEL);
        		}
        		importConfig.setFileNameFormat(importConfigPd.getString("FILENAME_FROMAT"));    	    	
    	    }
    	    // 返回数据导入配置
    	    return importConfig;
    	}
    	return importConfig;
    }
}
