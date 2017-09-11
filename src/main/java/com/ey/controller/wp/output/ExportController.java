package com.ey.controller.wp.output;

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
import com.ey.service.wp.output.GExportManager;
import com.ey.service.wp.output.NExportManager;
import com.ey.service.wp.output.ReportExportManager;
import com.ey.util.PageData;

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
}
