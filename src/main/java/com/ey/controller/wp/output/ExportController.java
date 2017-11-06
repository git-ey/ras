package com.ey.controller.wp.output;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ey.controller.base.BaseController;
import com.ey.service.wp.output.CExportManager;
import com.ey.service.wp.output.EExportManager;
import com.ey.service.wp.output.GExportManager;
import com.ey.service.wp.output.NExportManager;
import com.ey.service.wp.output.PExportManager;
import com.ey.service.wp.output.ReportExportManager;
import com.ey.service.wp.output.UExportManager;
import com.ey.util.DelAllFile;
import com.ey.util.FileZip;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.fileexport.Constants;
import com.ey.util.fileexport.FileExportUtils;

/**
 * 说明： 底稿输出Controller
 */
@Controller
@RequestMapping(value = "/wpExport")
public class ExportController extends BaseController {
	// 报告Report
    @Resource(name = "reportExportService")
    private ReportExportManager reportExportService;
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

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}
	
	/**
	 * 数据校验，如periodStr位数不足8位则补齐
	 * @author Dai Zong 2017年10月17日
	 * 
	 * @param fundId 基金ID
	 * @param periodStr 期间字符串
	 * @return 处理过的期间字符串
	 */
	private String dataCheck(String fundId, String periodStr) {
	    if(StringUtils.isEmpty(fundId) || StringUtils.isEmpty(periodStr)) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
	    if(periodStr.length() > 8) {
	        periodStr = periodStr.substring(0, 8);
	    }else if(periodStr.length() < 8) {
	        periodStr = periodStr + (String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + "1231").substring(periodStr.length(), 8);
	    }
	    return periodStr;
	}
	
	/**
     * 导出Word报告
     * 
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/Report")
    public void exportReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        
        this.reportExportService.doExport(request, response, fundId, periodStr);
    }

	/**
	 * 底稿导出--C
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/C")
	public void exportC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PageData pd = this.getPageData();
		String fundId = pd.getString("FUND_ID");
		String periodStr = pd.getString("PEROID");
		periodStr = this.dataCheck(fundId, periodStr);
        
		this.cExportService.doExport(request, response, fundId, periodStr);
	}
	
	/**
     * 底稿导出--G
     * 
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/G")
    public void exportG(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        
        this.gExportService.doExport(request, response, fundId, periodStr);
    }
    
    /**
     * 底稿导出--N
     * 
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/N")
    public void exportN(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        
        this.nExportService.doExport(request, response, fundId, periodStr);
    }
    
    /**
     * 底稿导出--P
     * 
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/P")
    public void exportP(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        
        this.pExportService.doExport(request, response, fundId, periodStr);
    }
    
    /**
     * 底稿导出--E
     * 
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/E")
    public void exportE(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        
        this.eExportService.doExport(request, response, fundId, periodStr);
    }
    
    /**
     * 底稿导出--U
     * 
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/U")
    public void exportU(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        
        this.uExportService.doExport(request, response, fundId, periodStr);
    }
    
    @RequestMapping(value = "/download")
    public void downLoadOneFund(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        final String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        
        final String fileIdentifier = fundId + "_" + periodStr;
        final String resourcePath = PathUtil.getWebResourcePath(request);
        final String folderName = resourcePath + fileIdentifier + "_" + String.valueOf(new Date().getTime());
        
        this.cExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_C, fundId, periodStr);
        this.gExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_G, fundId, periodStr);
        this.nExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_N, fundId, periodStr);
        this.pExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_P, fundId, periodStr);
        this.eExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_E, fundId, periodStr);
        this.uExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_U, fundId, periodStr);
        this.reportExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_REPORT, fundId, periodStr);
        
        final String zipFileName = fileIdentifier + ".zip";
        final String zipFileFullName = resourcePath + zipFileName;
        FileZip.zip(folderName, zipFileFullName);
        
        FileExportUtils.writeFileToHttpResponse(request, response, zipFileName, new File(zipFileFullName));
        
        DelAllFile.delFolder(folderName);
        File zipFile = new File(zipFileFullName);
        if(zipFile.exists() && zipFile.isFile()) {
            zipFile.delete();
        }
    }
}