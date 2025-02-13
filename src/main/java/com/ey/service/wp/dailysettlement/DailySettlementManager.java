package com.ey.service.wp.dailysettlement;

import java.util.List;
import java.util.Map;

import com.ey.entity.Page;
import com.ey.util.PageData;

/** 
 * 说明： 日交割汇总接口
 * 创建人：andychen
 * 创建时间：2017-09-24
 * @version
 */
public interface DailySettlementManager{

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
	
	/**批量新增
	 * @param pds
	 * @throws Exception
	 */
	public void saveBatch(List<Map> maps) throws Exception;
	
}

