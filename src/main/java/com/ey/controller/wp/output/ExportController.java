package com.ey.controller.wp.output;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}
	
	/**
	 * 数据校验
	 * @author Dai Zong 2017年10月17日
	 * 
	 * @param fundId 基金ID
	 * @param periodStr 期间字符串
	 */
	private void dataCheck(String fundId, String periodStr) {
	    if(fundId == null || periodStr == null) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
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
        this.dataCheck(fundId, periodStr);
        
        this.reportExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
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
		this.dataCheck(fundId, periodStr);
        
		this.cExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
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
        this.dataCheck(fundId, periodStr);
        
        this.gExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
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
        this.dataCheck(fundId, periodStr);
        
        this.nExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
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
        this.dataCheck(fundId, periodStr);
        
        this.pExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
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
        this.dataCheck(fundId, periodStr);
        
        this.eExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
    }
    
    @RequestMapping(value = "/download")
    public void downLoadOneFund(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        final String fundId = pd.getString("FUND_ID");
        final String periodStr = pd.getString("PEROID");
        this.dataCheck(fundId, periodStr);
        final long period = Long.parseLong(periodStr);
        
        final String fileIdentifier = fundId + "_" + periodStr;
        final String resourcePath = PathUtil.getClassResources();
        final String folderName = resourcePath + fileIdentifier + "_" + String.valueOf(new Date().getTime());
        
        this.cExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_C, fundId, period);
        this.gExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_G, fundId, period);
        this.nExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_N, fundId, period);
        this.pExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_P, fundId, period);
        this.eExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_E, fundId, period);
        this.reportExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_REPORT, fundId, period);
        
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