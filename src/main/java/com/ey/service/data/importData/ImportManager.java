package com.ey.service.data.importData;

import java.util.List;
import com.ey.entity.Page;
import com.ey.util.PageData;

/** 
 * 说明： 导入工作台接口
 * 创建人：andychen
 * 创建时间：2017-08-04
 * @version
 */
public interface ImportManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception;
	
	/**新增导入文件信息
	 * @param pd
	 * @throws Exception
	 */
	public void saveImportFile(PageData pd) throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception;
	
	/**删除导入文件记录及数据
	 * @param pd
	 * @throws Exception
	 */
	public void deleteImportFile(PageData pd) throws Exception;
	
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
	
	/**列表导入文件
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> listImportFile(Page page) throws Exception;
	
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
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	
	/**
	 * 解析Excel中的数据插入数据库
	 * @param pd
	 * @throws Exception
	 */
	public void saveImportData(PageData pd) throws Exception;

	/**
	 * 查询文件名存在条数
	 * @param pathFiles
	 * @return
	 */
	public Long findFileCount(List<String> pathFiles) throws Exception;
	
}

