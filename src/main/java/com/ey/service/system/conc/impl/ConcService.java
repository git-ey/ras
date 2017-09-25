package com.ey.service.system.conc.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.util.PageData;
import com.ey.service.system.conc.ConcManager;

/** 
 * 说明： 并发程序管理
 * 创建人：andychen
 * 创建时间：2017-08-15
 * @version
 */
@Service("concService")
public class ConcService implements ConcManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ConcMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ConcMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ConcMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ConcMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ConcMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ConcMapper.findById", pd);
	}
	
	/**通过code获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByCode(PageData pd) throws Exception {
		return (PageData)dao.findForObject("ConcMapper.findByCode", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ConcMapper.deleteAll", ArrayDATA_IDS);
	}
	
}

