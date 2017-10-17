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
import com.ey.util.FileDownload;
import com.ey.util.FileZip;
import com.ey.util.PageData;
import com.ey.util.PathUtil;

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
        if(fundId == null || periodStr == null) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
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
		if(fundId == null || periodStr == null) {
		    throw new IllegalArgumentException("基金ID和期间不能为空");
		}
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
        if(fundId == null || periodStr == null) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
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
        if(fundId == null || periodStr == null) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
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
        if(fundId == null || periodStr == null) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
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
        if(fundId == null || periodStr == null) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
        this.eExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
    }
    
    @RequestMapping(value = "/download")
    public void downLoadOneFund(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        if(fundId == null || periodStr == null) {
            throw new IllegalArgumentException("基金ID和期间不能为空");
        }
        
        String path = (String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))).replaceAll("file:/", "").replaceAll("%20", " ").trim(); 
        if(path.indexOf(":") != 1){
            path = File.separator + path;
        }
        
        String folderName = path + fundId + "_" + periodStr + "_" + String.valueOf(new Date().getTime());
        
        long period = Long.parseLong(periodStr);
        cExportService.doExport(folderName, "C.xls", fundId, period);
        gExportService.doExport(folderName, "G.xls", fundId, period);
        nExportService.doExport(folderName, "N.xls", fundId, period);
        pExportService.doExport(folderName, "P.xls", fundId, period);
        eExportService.doExport(folderName, "E.xls", fundId, period);
        
        String zipFileName = path + fundId + "_" + periodStr + ".zip";
        FileZip.zip(folderName, zipFileName);
        
        FileDownload.fileDownload(response, zipFileName, fundId + "_" + periodStr + ".zip");
    }
}