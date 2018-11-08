package com.ey.service.system.workpaper.impl;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ey.dao.DaoSupport;
import com.ey.entity.Page;
import com.ey.service.system.workpaper.WorkPaperManager;
import com.ey.service.wp.output.CExportManager;
import com.ey.service.wp.output.EExportManager;
import com.ey.service.wp.output.GExportManager;
import com.ey.service.wp.output.HExportManager;
import com.ey.service.wp.output.HSumExportManager;
import com.ey.service.wp.output.IExportManager;
import com.ey.service.wp.output.NExportManager;
import com.ey.service.wp.output.OExportManager;
import com.ey.service.wp.output.PExportManager;
import com.ey.service.wp.output.TExportManager;
import com.ey.service.wp.output.UExportManager;
import com.ey.service.wp.output.VExportManager;
import com.ey.util.Logger;
import com.ey.util.PageData;
import com.ey.util.fileexport.Constants;

/** 
 * 说明： 底稿导出工作台
 * 创建人：andychen
 * 创建时间：2018-01-01
 * @version
 */
@Service("workpaperService")
public class WorkPaperService implements WorkPaperManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	// 底稿C
    @Resource(name = "cExportService")
    private CExportManager cExportService;
    // 底稿G
    @Resource(name = "gExportService")
    private GExportManager gExportService;
    // 底稿G
    @Resource(name = "nExportService")
    private NExportManager nExportService;
    // 底稿P
    @Resource(name = "pExportService")
    private PExportManager pExportService;
    // 底稿P
    @Resource(name = "eExportService")
    private EExportManager eExportService;
    // 底稿U
    @Resource(name = "uExportService")
    private UExportManager uExportService;
    // 底稿V
    @Resource(name = "vExportService")
    private VExportManager vExportService;
    // 底稿T
    @Resource(name = "tExportService")
    private TExportManager tExportService;
    // 底稿H_SUM
    @Resource(name = "hSumExportService")
    private HSumExportManager hSumExportService;
    // 底稿H
    @Resource(name = "hExportService")
    private HExportManager hExportService;
    // 底稿I
    @Resource(name = "iExportService")
    private IExportManager iExportService;
    // 底稿I
    @Resource(name = "oExportService")
    private OExportManager oExportService;
    
    //FLAG字段名，导出用
    private static final String PD_FIELD_CFLAG = "CFLAG";
    private static final String PD_FIELD_EFLAG = "EFLAG";
    private static final String PD_FIELD_GFLAG = "GFLAG";
    private static final String PD_FIELD_HFLAG = "HFLAG";
    private static final String PD_FIELD_NFLAG = "NFLAG";
    private static final String PD_FIELD_PFLAG = "PFLAG";
    private static final String PD_FIELD_TFLAG = "TFLAG";
    private static final String PD_FIELD_UFLAG = "UFLAG";
    private static final String PD_FIELD_VFLAG = "VFLAG";
    private static final String PD_FIELD_IFLAG = "IFLAG";
    private static final String PD_FIELD_H_SUMFLAG = "H_SUMFLAG";
    private static final String PD_FIELD_OFLAG = "OFLAG";
    private static final String PD_FIELD_SAFLAG = "SAFLAG";
    
    private static final String PD_FIELD_WP_TYPE = "WP_TYPE";
    private static final String PD_FIELD_FUND_ID = "FUND_ID";
    
    private final Logger logger = Logger.getLogger(WorkPaperService.class);
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("WorkPaperMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("WorkPaperMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("WorkPaperMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("WorkPaperMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("WorkPaperMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("WorkPaperMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("WorkPaperMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**
	 * 运行底稿导出程序
	 * 
	 * @param pd
	 */
	public void exportWorkPaper(PageData pd) throws Exception {
//		{
//		    FIRM_CODE=FG, 
//	        WP_TYPE=C,
//		    PERIOD=20161231, 
//		    OUTBOND_PATH=D:\wp\, 
//		    FUND_ID=F100066-01
//		}
	    String exportPath = pd.getString("OUTBOND_PATH");
	    String periodStr = pd.getString("PERIOD");
	    @SuppressWarnings("unchecked")
        List<PageData> fundInfos = (List<PageData>)dao.findForList("WorkPaperMapper.selectFundInfos", pd);
	    if(CollectionUtils.isNotEmpty(fundInfos)) {
	        String errorMsg = StringUtils.EMPTY;
	        for(PageData fundInfo : fundInfos) {
	            try {
	                this.exportOneFundWorkPaper(fundInfo, exportPath, periodStr, pd.getString(PD_FIELD_WP_TYPE));
	            }catch (Exception ex) {
	                logger.error("底稿导出异常: " + fundInfo.getString("FUND_ID") + " " + fundInfo.getString("PERIOD"), ex);
	                errorMsg += (ex.getMessage() + '\n');
	            }
	        }
	        try {
	        	if(StringUtils.isEmpty(pd.getString(PD_FIELD_WP_TYPE)) || PD_FIELD_H_SUMFLAG.equals(pd.getString(PD_FIELD_WP_TYPE))){
		            exportPath += (periodStr + File.separatorChar + "H_SUM" + File.separatorChar) ;
		            this.hSumExportService.doExport(exportPath, (Object)Constants.EXPORT_AIM_FILE_NAME_H_SUM, pd.getString("FIRM_CODE"), periodStr);
	        	}
	        }catch (Exception ex) {
	            logger.error("H汇总底稿导出异常: " + pd.getString("FIRM_CODE") + " " + periodStr, ex);
	            errorMsg += (ex.getMessage() + '\n');
            }
	        if(errorMsg.length() != 0) {
	            throw new Exception(errorMsg);
	        }
	    }
	    // 设置消息
        pd.put("RESULT", "S");
	}
	
	/**
	 * 导出一个基金下所有可输出的底稿
	 * @author Dai Zong 2018年1月2日
	 * 
	 * @param pd 从DataexportMapper中查询出的运行参数
	 * @param exportPath 导出路径
	 * @param periodStr 日期字符串
	 * @param wpType 指定导出类型
	 * @throws Exception
	 */
	private void exportOneFundWorkPaper(PageData pd, String exportPath, String periodStr, String wpType) throws Exception {
        final String fundId = pd.getString(PD_FIELD_FUND_ID);
        final String fileIdentifier = fundId;
        exportPath += (periodStr + File.separatorChar) ;
        final String folderName = exportPath + fileIdentifier;
        if (this.getExportFlag(pd, PD_FIELD_CFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_CFLAG.equals(wpType))) {
            this.cExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_C, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_GFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_GFLAG.equals(wpType))) {
            this.gExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_G, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_NFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_NFLAG.equals(wpType))) {
            this.nExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_N, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_PFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_PFLAG.equals(wpType))) {
            this.pExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_P, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_EFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_EFLAG.equals(wpType))) {
            this.eExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_E, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_UFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_UFLAG.equals(wpType))) {
            this.uExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_U, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_VFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_VFLAG.equals(wpType))) {
            this.vExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_V, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_TFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_TFLAG.equals(wpType))) {
            this.tExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_T, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_HFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_HFLAG.equals(wpType))) {
            this.hExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_H, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_IFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_IFLAG.equals(wpType))) {
            this.iExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_I, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_OFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_OFLAG.equals(wpType))) {
            this.oExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_O, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_SAFLAG) && (StringUtils.isEmpty(wpType) || PD_FIELD_SAFLAG.equals(wpType))) {
            // TODO SA 底稿
//            this.iExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_SA, fundId, periodStr);
        }
	}
	
	/**
	 * 从pd中获取是否应该输出该底稿
	 * @author Dai Zong 2018年1月2日
	 * 
	 * @param pd
	 * @param flagFeild
	 * @return
	 */
	private boolean getExportFlag(PageData pd, String flagFeild) {
	    if(pd == null || StringUtils.isEmpty(flagFeild)) {
	        return false;
	    }
	    Object obj = pd.get(flagFeild);
	    if(obj == null) {
	        return false;
	    } else {
	        return Integer.parseInt(String.valueOf(obj)) > 0 ? true : false;
	    }
	}
	
}

