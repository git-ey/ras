package com.ey.controller.wp.output;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ey.service.system.config.ConfigManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ey.controller.base.BaseController;
import com.ey.dao.DaoSupport;
import com.ey.service.system.report.impl.ReportService;
import com.ey.service.system.workpaper.impl.WorkPaperService;
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

    /**
     * dao
     */
    @Resource(name = "daoSupport")
    protected DaoSupport dao;

	// 报告Report
    @Resource(name = "reportService")
    private ReportService reportService;
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
    // 底稿SA
    @Resource(name = "saExportService")
    private SAExportManager saExportService;

    @Resource(name = "configService")
    private ConfigManager configService;



    String templatePath;

    ExportController(){
        try {
            templatePath =configService.findByCode(Constants.WP_TEMAP_PATH);
        }catch (Exception e){
            templatePath = PathUtil.getClasspath();
        }
    }

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

	@RequestMapping(value = "/test")
	@ResponseBody
	public void test() {
	    new Thread(() -> {
	        String scriptPath = this.getClass().getClassLoader().getResource("vbs/workpaperAndReportConverter.vbs").getPath();
	        if(StringUtils.isBlank(scriptPath)) {
	            return;
	        }
	        if(scriptPath.charAt(0) == '/' || scriptPath.charAt(0) == '\\') {
	            scriptPath = scriptPath.substring(1);
	        }
	        System.out.println(scriptPath);
	        try {
                Runtime.getRuntime().exec("cmd /C " + scriptPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
	    }).start();
	    return;
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
        pd.put("PERIOD", periodStr);

        this.reportService.exportReport(request, response, pd);
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

		this.cExportService.doExport(request, response, fundId, periodStr, templatePath);
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

        this.gExportService.doExport(request, response, fundId, periodStr, templatePath);
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

        this.nExportService.doExport(request, response, fundId, periodStr, templatePath);
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

        this.pExportService.doExport(request, response, fundId, periodStr, templatePath);
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

        this.eExportService.doExport(request, response, fundId, periodStr, templatePath);
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

        this.uExportService.doExport(request, response, fundId, periodStr, templatePath);
    }

    /**
     * 底稿导出--V
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/V")
    public void exportV(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);

        this.vExportService.doExport(request, response, fundId, periodStr, templatePath);
    }

    /**
     * 底稿导出--T
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/T")
    public void exportT(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);

        this.tExportService.doExport(request, response, fundId, periodStr, templatePath);
    }

    /**
     * 底稿导出--HSUM
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/HX")
    public void exportHSum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        String firmCode = pd.getString("FIRM_CODE");

        if(StringUtils.isEmpty(periodStr) || (StringUtils.isEmpty(fundId) && StringUtils.isEmpty(firmCode))) {
            throw new IllegalArgumentException("期间不能为空,基金ID和公司代码至少一个不能为空");
        }
        if(periodStr.length() > 8) {
            periodStr = periodStr.substring(0, 8);
        }else if(periodStr.length() < 8) {
            periodStr = periodStr + (String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + "1231").substring(periodStr.length(), 8);
        }

        if(StringUtils.isNotEmpty(fundId)) {
            this.hSumExportService.doExport(request, response, fundId, periodStr, templatePath);
        }else{
            this.hSumExportService.doExport(firmCode, periodStr, request, response, templatePath);
        }
    }

    /**
     * 底稿导出--H
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/H")
    public void exportH(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);

        this.hExportService.doExport(request, response, fundId, periodStr, templatePath);
    }

    /**
     * 底稿导出--I
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/I")
    public void exportI(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);

        this.iExportService.doExport(request, response, fundId, periodStr, templatePath);
    }

    /**
     * 底稿导出--O
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/O")
    public void exportO(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);

        this.oExportService.doExport(request, response, fundId, periodStr, templatePath);
    }

    /**
     * 底稿导出--SA
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/SA")
    public void exportSA(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        String firmCode = pd.getString("FIRM_CODE");


        if(StringUtils.isEmpty(periodStr) || (StringUtils.isEmpty(fundId) && StringUtils.isEmpty(firmCode))) {
            throw new IllegalArgumentException("期间不能为空,基金ID和公司代码至少一个不能为空");
        }
        if(periodStr.length() > 8) {
            periodStr = periodStr.substring(0, 8);
        }else if(periodStr.length() < 8) {
            periodStr = periodStr + (String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + "1231").substring(periodStr.length(), 8);
        }

        if(StringUtils.isNotEmpty(fundId)) {
            this.saExportService.doExport(request, response, fundId, periodStr, templatePath);
        }else{
            this.saExportService.doExport(firmCode, periodStr, request, response, templatePath);
        }
    }

    @RequestMapping(value = "/download")
    public void downLoadOneFund(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        final String fundId = pd.getString("FUND_ID");
        String periodStr = pd.getString("PEROID");
        periodStr = this.dataCheck(fundId, periodStr);
        pd.put("PEROID", periodStr);

        PageData fundInfos = (PageData)this.dao.findForObject("WorkPaperMapper.selectFundInfos", pd);

        final String fileIdentifier = fundId + "_" + periodStr;
        final String resourcePath = PathUtil.getWebResourcePath(request);
        final String folderName = resourcePath + fileIdentifier + "_" + String.valueOf(new Date().getTime());

        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_CFLAG)) {
            this.cExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_C, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_GFLAG)) {
            this.gExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_G, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_NFLAG)) {
            this.nExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_N, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_PFLAG)) {
            this.pExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_P, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_EFLAG)) {
            this.eExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_E, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_UFLAG)) {
            this.uExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_U, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_VFLAG)) {
            this.vExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_V, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_TFLAG)) {
            this.tExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_T, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_HFLAG)) {
            this.hExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_H, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_IFLAG)) {
            this.iExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_I, fundId, periodStr, templatePath);
        }
        if (this.getExportFlag(fundInfos, WorkPaperService.PD_FIELD_OFLAG)) {
            this.oExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_O, fundId, periodStr, templatePath);
        }
//        this.reportExportService.doExport(folderName, Constants.EXPORT_AIM_FILE_NAME_REPORT, pd);

        FileExportUtils.createDir(folderName);
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
