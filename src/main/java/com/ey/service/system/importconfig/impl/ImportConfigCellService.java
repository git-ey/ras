package com.ey.service.system.importconfig.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.importconfig.ImportConfigCellManager;
import com.ey.util.PageData;

/** 
 * 说明： 数据导入设置(明细)
 * 创建人：andychen
 * 创建时间：2017-08-03
 * @version
 */
@Service("importConfigCellService")
public class ImportConfigCellService implements ImportConfigCellManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ImportConfigCellMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ImportConfigCellMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ImportConfigCellMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ImportConfigCellMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ImportConfigCellMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ImportConfigCellMapper.findById", pd);
	}
	
	/**通过配置头id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByConfigId(@Param("IMPORTCONFIG_ID") String importConfigId)throws Exception {
		return (List<PageData>)dao.findForObject("ImportConfigCellMapper.findByConfigId", importConfigId);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ImportConfigCellMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**查询明细总数
	 * @param pd
	 * @throws Exception
	 */
	public PageData findCount(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ImportConfigCellMapper.findCount", pd);
	}
	
}

