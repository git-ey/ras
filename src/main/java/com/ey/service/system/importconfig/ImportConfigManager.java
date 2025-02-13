package com.ey.service.system.importconfig;

import java.util.List;

import com.ey.entity.Page;
import com.ey.util.PageData;

/** 
 * 说明： 数据导入设置接口
 * 创建人：andychen
 * 创建时间：2017-08-03
 * @version
 */
public interface ImportConfigManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception;
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception;
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception;
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**通过Code获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByCode(String importTempCode)throws Exception;
	
	/**通过导入文件类型获取数据
	 * @param pd
	 * @throws Exception
	 */
	public List<String> findByImportTempCode(PageData pd) throws Exception;
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	
	/**
	 * 获取创建数据库表SQL
	 * @param importConfigId
	 * @return
	 * @throws Exception
	 */
	public String getTableSql(String importConfigId) throws Exception;
	
}

