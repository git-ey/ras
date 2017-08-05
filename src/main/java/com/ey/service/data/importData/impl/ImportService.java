package com.ey.service.data.importData.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.data.importData.ImportManager;
import com.ey.util.PageData;

/** 
 * 说明： 导入工作台
 * 创建人：andychen
 * 创建时间：2017-08-04
 * @version
 */
@Service("importService")
public class ImportService implements ImportManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ImportMapper.save", pd);
	}
	
	/**新增导入文件信息
	 * @param pd
	 * @throws Exception
	 */
	public void saveImportFile(PageData pd) throws Exception {
		dao.save("ImportMapper.saveImportFile", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("ImportMapper.delete", pd);
	}
	
	/**删除导入文件记录及数据
	 * @param pd
	 * @throws Exception
	 */
	public void deleteImportFile(PageData pd) throws Exception {
		// 删除导入数据
		dao.delete("ImportMapper.deleteImportData", pd);
		// 删除导入文件信息
		dao.delete("ImportMapper.deleteImportFile", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ImportMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ImportMapper.datalistPage", page);
	}
	
	/**列表导入文件
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> listImportFile(Page page) throws Exception {
		return (List<PageData>)dao.findForList("ImportMapper.datalistImportFilePage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ImportMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ImportMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception {
		dao.delete("ImportMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**
	 * 解析Excel中的数据插入数据库
	 * @param pd
	 * @throws Exception
	 */
	public void saveImportData(PageData pd) throws Exception {
		dao.save("ImportMapper.saveImportData", pd);
	}

	/**
	 * 查询文件名存在条数
	 * @param pathFiles
	 * @return
	 */
	public Long findFileCount(List<String> pathFiles) throws Exception {
		return (Long)dao.findForObject("ImportMapper.findFileCount", pathFiles);
	}
	
}

