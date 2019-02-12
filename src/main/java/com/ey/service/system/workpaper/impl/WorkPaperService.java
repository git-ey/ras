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
import com.ey.service.wp.output.SAExportManager;
import com.ey.service.wp.output.TExportManager;
import com.ey.service.wp.output.UExportManager;
import com.ey.service.wp.output.VExportManager;
import com.ey.util.Logger;
import com.ey.util.PageData;
import com.ey.util.VbsUtil;
import com.ey.util.VbsUtil.Scripts;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;

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
    // 底稿O
    @Resource(name = "oExportService")
    private OExportManager oExportService;
    // 底稿O
    @Resource(name = "saExportService")
    private SAExportManager saExportService;
    
    //FLAG字段名，导出用
    public static final String PD_FIELD_CFLAG = "CFLAG";
    public static final String PD_FIELD_EFLAG = "EFLAG";
    public static final String PD_FIELD_GFLAG = "GFLAG";
    public static final String PD_FIELD_HFLAG = "HFLAG";
    public static final String PD_FIELD_NFLAG = "NFLAG";
    public static final String PD_FIELD_PFLAG = "PFLAG";
    public static final String PD_FIELD_TFLAG = "TFLAG";
    public static final String PD_FIELD_UFLAG = "UFLAG";
    public static final String PD_FIELD_VFLAG = "VFLAG";
    public static final String PD_FIELD_IFLAG = "IFLAG";
    public static final String PD_FIELD_OFLAG = "OFLAG";
    
    private static final String PD_FIELD_WP_TYPE = "WP_TYPE";
    private static final String PD_FIELD_FUND_ID = "FUND_ID";
    private static final String PD_FIELD_FIRM_CODE = "FIRM_CODE";
    
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
//	    pd = {
//	        "FIRM_CODE": "FG",
//	        "WP_TYPE": "C",
//	        "PERIOD": "20161231",
//	        "OUTBOND_PATH": "D:\\wp\\",
//	        "FUND_ID": "F100066-01"
//	    }
	    String exportPath = pd.getString("OUTBOND_PATH");
	    String tempExportPath = exportPath;
	    int exportPathLength = exportPath.length();
	    if(exportPath.charAt(exportPathLength - 1) == '/' || exportPath.charAt(exportPathLength - 1) == '\\') {
	        tempExportPath = tempExportPath.substring(0, exportPathLength - 1);
	    }
	    tempExportPath = tempExportPath + "_temp" + File.separatorChar;
	    String periodStr = pd.getString("PERIOD");
	    @SuppressWarnings("unchecked")
        List<PageData> fundInfos = (List<PageData>)dao.findForList("WorkPaperMapper.selectFundInfos", pd);
	    if(CollectionUtils.isNotEmpty(fundInfos)) {
	        String errorMsg = StringUtils.EMPTY;
	        for(PageData fundInfo : fundInfos) {
	            try {
	                this.exportOneFundWorkPaper(fundInfo, tempExportPath, periodStr, pd.getString(PD_FIELD_WP_TYPE));
	            }catch (Exception ex) {
	                logger.error("底稿导出异常: " + fundInfo.getString("FUND_ID") + " " + fundInfo.getString("PERIOD"), ex);
	                errorMsg += (ex.getMessage() + '\n');
	            }
	        }
	        try {
	        	if(StringUtils.isEmpty(pd.getString(PD_FIELD_WP_TYPE)) || "H_SUM".equals(pd.getString(PD_FIELD_WP_TYPE))){
	        	    // ↓ daigaokuo@hotmail.com 2019-02-13 ↓
	                // [IMP] 最终输出文件夹路径中添加firm code
		            String hSumExportPath = tempExportPath + (periodStr + File.separatorChar + pd.getString(PD_FIELD_FIRM_CODE) + File.separatorChar + "H_SUM" + File.separatorChar);
		            // ↑ daigaokuo@hotmail.com 2019-02-13 ↑
		            this.hSumExportService.doExport(hSumExportPath, (Object)Constants.EXPORT_AIM_FILE_NAME_H_SUM, pd.getString("FIRM_CODE"), periodStr);
	        	}
	        }catch (Exception ex) {
	            logger.error("H汇总底稿导出异常: " + pd.getString("FIRM_CODE") + " " + periodStr, ex);
	            errorMsg += (ex.getMessage() + '\n');
            }
	        try {
	            if(StringUtils.isEmpty(pd.getString(PD_FIELD_WP_TYPE)) || "SA".equals(pd.getString(PD_FIELD_WP_TYPE))){
	                // ↓ daigaokuo@hotmail.com 2019-02-13 ↓
	                // [IMP] 最终输出文件夹路径中添加firm code
	                String saSumExportPath = tempExportPath + (periodStr + File.separatorChar + pd.getString(PD_FIELD_FIRM_CODE) + File.separatorChar + "SA" + File.separatorChar);
	                // ↑ daigaokuo@hotmail.com 2019-02-13 ↑
                    this.saExportService.doExport(saSumExportPath, (Object)Constants.EXPORT_AIM_FILE_NAME_SA, pd.getString("FIRM_CODE"), periodStr);
                }
	        }catch (Exception ex) {
                logger.error("SA汇总底稿导出异常: " + pd.getString("FIRM_CODE") + " " + periodStr, ex);
                errorMsg += (ex.getMessage() + '\n');
            }
	        if(errorMsg.length() != 0) {
	            throw new Exception(errorMsg);
	        }
	        FileExportUtils.createDir(exportPath);
	        VbsUtil.callScript(Scripts.WORKPAPER_AND_REPORT_CONVERTER, tempExportPath, exportPath);
//	        FileUtils.deleteDirectory(new File(tempExportPath));
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
        final String firmCode = pd.getString(PD_FIELD_FIRM_CODE);
        exportPath += (periodStr + File.separatorChar) ;
        // ↓ daigaokuo@hotmail.com 2019-02-13 ↓
        // [IMP] 最终输出文件夹路径中添加firm code
        final String folderName = exportPath + firmCode + File.separatorChar + fundId;
        // ↑ daigaokuo@hotmail.com 2019-02-13 ↑
        if (this.getExportFlag(pd, PD_FIELD_CFLAG) && (StringUtils.isEmpty(wpType) || "C".equals(wpType))) {
            this.cExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_C, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_GFLAG) && (StringUtils.isEmpty(wpType) || "G".equals(wpType))) {
            this.gExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_G, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_NFLAG) && (StringUtils.isEmpty(wpType) || "N".equals(wpType))) {
            this.nExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_N, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_PFLAG) && (StringUtils.isEmpty(wpType) || "P".equals(wpType))) {
            this.pExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_P, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_EFLAG) && (StringUtils.isEmpty(wpType) || "E".equals(wpType))) {
            this.eExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_E, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_UFLAG) && (StringUtils.isEmpty(wpType) || "U".equals(wpType))) {
            this.uExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_U, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_VFLAG) && (StringUtils.isEmpty(wpType) || "V".equals(wpType))) {
            this.vExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_V, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_TFLAG) && (StringUtils.isEmpty(wpType) || "T".equals(wpType))) {
            this.tExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_T, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_HFLAG) && (StringUtils.isEmpty(wpType) || "H".equals(wpType))) {
            this.hExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_H, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_IFLAG) && (StringUtils.isEmpty(wpType) || "I".equals(wpType))) {
            this.iExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_I, fundId, periodStr);
        }
        if (this.getExportFlag(pd, PD_FIELD_OFLAG) && (StringUtils.isEmpty(wpType) || "O".equals(wpType))) {
            this.oExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_O, fundId, periodStr);
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

