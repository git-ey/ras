package com.ey.service.system.report;

import java.util.List;

import com.ey.entity.Page;
import com.ey.util.PageData;

/** 
 * 说明： 报告导出接口
 * 创建人：andychen
 * 创建时间：2017-12-05
 * @version
 */
public interface ReportManager{

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
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	
	/**查询段落模板
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listParagraph(String paragraphCode) throws Exception;
	
	/**列表段落模板
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listParagraphAll(PageData pd) throws Exception;
	
}

