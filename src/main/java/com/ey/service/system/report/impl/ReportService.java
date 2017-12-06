package com.ey.service.system.report.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.report.ReportManager;
import com.ey.util.PageData;

/** 
 * 说明： 报告导出
 * 创建人：andychen
 * 创建时间：2017-12-05
 * @version
 */
@Service("reportService")
public class ReportService implements ReportManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void save(PageData pd)throws Exception{
		dao.save("ReportMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void delete(PageData pd)throws Exception{
		dao.delete("ReportMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public void edit(PageData pd)throws Exception{
		dao.update("ReportMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ReportMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ReportMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	@Override
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ReportMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	@Override
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ReportMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**列表段落模板
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listParagraph(String paragraphCode)throws Exception {
		return (List<PageData>)dao.findForList("ReportMapper.listParagraph",paragraphCode);
	}
	
	/**列表段落模板(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> listParagraphAll(PageData pd)throws Exception {
		return (List<PageData>)dao.findForList("ReportMapper.listParagraphAll",pd);
	}
	
	/**
	 * 运行报告导出程序
	 * @param pd
	 */
	public void exportReport(PageData pd) throws Exception {
		
		// 设置消息
		pd.put("RESULT", "S");
	}
	
}

