package com.ey.service.data.fileimport.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.data.fileimport.ImportManager;
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
	@Override
    public void save(PageData pd)throws Exception{
		dao.save("ImportMapper.save", pd);
	}
	
	/**新增导入文件信息
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void saveImportFile(PageData pd) throws Exception {
		dao.save("ImportMapper.saveImportFile", pd);
		// 如果存在错误，则删除已导入数据
		if(StringUtils.isNotBlank(pd.getString("MESSAGE"))){
			this.deleteImportData(pd);
		}
	}
	
	/**更新导入文件信息
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void updateImportFile(PageData importFilePd) throws Exception {
		dao.update("ImportMapper.updateImportFile", importFilePd);
		// 删除已导入数据
	    this.deleteImportData(importFilePd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void delete(PageData pd)throws Exception{
		dao.delete("ImportMapper.delete", pd);
	}
	
	/**删除导入文件记录及数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void deleteImportFile(PageData pd) throws Exception {
		// 删除导入数据
		this.deleteImportData(pd);
		// 删除导入文件信息
		dao.delete("ImportMapper.deleteImportFile", pd);
	}
	
	/**
	 * 删除导入数据
	 * @param pd
	 * @throws Exception
	 */
	private void deleteImportData(PageData pd) throws Exception {
		// 删除导入数据
		dao.delete("ImportMapper.deleteImportData", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void edit(PageData pd)throws Exception{
		dao.update("ImportMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ImportMapper.datalistPage", page);
	}
	
	/**列表导入文件
	 * @param page
	 * @throws Exception
	 */
	@Override
    public List<PageData> listImportFile(Page page) throws Exception {
		return (List<PageData>)dao.findForList("ImportMapper.datalistImportFilePage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@Override
    @SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ImportMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ImportMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
    public void deleteAll(String[] ArrayDATA_IDS)throws Exception {
		dao.delete("ImportMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**
	 * 解析Excel中的数据插入数据库
	 * @param pd
	 * @throws Exception
	 */
	@Override
    public void saveImportData(PageData pd) throws Exception {
		dao.save("ImportMapper.saveImportData", pd);
	}

	/**
	 * 查询文件名存在条数
	 * @param pathFiles
	 * @return
	 */
	@Override
    public Long findFileCount(String pathFile,Integer sheetNo) throws Exception {
		PageData pd = new PageData();
		pd.put("IMPORT_FILE_NAME", pathFile);
		pd.put("SHEET_NO", sheetNo);
		pd.put("NAME_SEG_6", pathFile.split("_")[5]);
		return (Long)dao.findForObject("ImportMapper.findFileCount", pd);
	}
	
	/**
	 * 执行存储过程
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
    public String callableProcedure(PageData pd) throws Exception {
		dao.callProcedure("ImportMapper.callableProcedure", pd);
		return pd.getString("RESULT");
	}
	
}

