package com.ey.service.system.concruning.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.util.PageData;
import com.ey.service.system.concruning.ConcRuningManager;

/** 
 * 说明： 并发工作台
 * 创建人：andychen
 * 创建时间：2017-08-15
 * @version
 */
@Service("concruningService")
public class ConcRuningService implements ConcRuningManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ConcRuningMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ConcRuningMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ConcRuningMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ConcRuningMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ConcRuningMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ConcRuningMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ConcRuningMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**
	 * 执行存储过程
	 * @param pd
	 * @throws Exception
	 */
	public void runProcedure(PageData pd) throws Exception {
		dao.callProcedure("ConcRuningMapper.runProcedure", pd);
	}

	/**列表日志
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listLog(PageData pd)  throws Exception {
		return (List<PageData>)dao.findForList("ConcRuningMapper.listLog", pd);
	}
	
}

