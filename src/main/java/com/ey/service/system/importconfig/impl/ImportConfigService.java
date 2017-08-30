package com.ey.service.system.importconfig.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.importconfig.ImportConfigManager;
import com.ey.util.PageData;

/** 
 * 说明： 数据导入设置
 * 创建人：andychen
 * 创建时间：2017-08-03
 * @version
 */
@Service("importConfigService")
public class ImportConfigService implements ImportConfigManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ImportConfigMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ImportConfigMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ImportConfigMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ImportConfigMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ImportConfigMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ImportConfigMapper.findById", pd);
	}
	
	/**通过Code获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByCode(String importTempCode)throws Exception{
		return (PageData)dao.findForObject("ImportConfigMapper.findByCode", importTempCode);
	}
	
	/**通过导入文件类型获取数据
	 * @param pd
	 * @throws Exception
	 */
	public List<String> findByImportTempCode(PageData pd) throws Exception {
		return (List<String>) dao.findForList("ImportConfigMapper.findByImportTempCode", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ImportConfigMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**
	 * 获取创建数据库表SQL
	 * @param importConfigId
	 * @return
	 * @throws Exception
	 */
	public String getTableSql(String importConfigId) throws Exception {
		PageData configPd = new PageData();
		configPd.put("IMPORTCONFIG_ID", importConfigId);
		// 获取头行数据
		PageData head = (PageData)dao.findForObject("ImportConfigMapper.findById", configPd);
		List<PageData> lines = (List<PageData>)dao.findForList("ImportConfigCellMapper.findByConfigId", importConfigId);
		// 组装建表脚本
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE `"+ head.getString("TABLE_NAME") +"` ( `SEQ` int NULL COMMENT '序号', \n");
		for(PageData line : lines){
			if(line.getString("CELLTYPE").toLowerCase().equals("string")){
				sb.append("`"+line.getString("MAPKEY")+"` varchar(240) NULL COMMENT '"+line.getString("DESCRIPTION")+"'");
			}else if(line.getString("CELLTYPE").toLowerCase().equals("bigdecimal")){
				sb.append("`"+line.getString("MAPKEY")+"` decimal(15,5) NULL COMMENT '"+line.getString("DESCRIPTION")+"'");
			}else{
				sb.append("`"+line.getString("MAPKEY")+"`"+ line.getString("CELLTYPE").toLowerCase() +" NULL COMMENT '"+line.getString("DESCRIPTION")+"'");
			}
			sb.append(", \n");
		}
		sb.append(" IMPORT_FILE_ID varchar(60) NULL COMMENT '导入文件ID' , \n KEY `"+head.getString("TABLE_NAME")+"_n1` (`IMPORT_FILE_ID`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}
	
}

