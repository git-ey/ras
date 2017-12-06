package com.ey.controller.system.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ey.controller.base.BaseController;
import com.ey.entity.Page;
import com.ey.service.system.config.ConfigManager;
import com.ey.service.system.fund.FundManager;
import com.ey.service.system.mgrcompany.MgrcompanyManager;
import com.ey.service.system.report.ReportManager;
import com.ey.util.Jurisdiction;
import com.ey.util.PageData;
import com.ey.util.Tools;
import com.google.common.collect.Lists;

/**
 * 说明：报告导出 创建人：andychen 创建时间：2017-12-05
 */
@Controller
@RequestMapping(value = "/report")
public class ReportController extends BaseController {

	String menuUrl = "report/list.do"; // 菜单地址(权限用)
	@Resource(name = "reportService")
	private ReportManager reportService;
	@Resource(name = "mgrcompanyService")
	private MgrcompanyManager mgrcompanyService;
	@Resource(name = "fundService")
	private FundManager fundService;
	@Resource(name="configService")
	private ConfigManager configService;
	@Autowired
	@Qualifier("taskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;
	
	// 报告输出路径
	public static final String REP_PATH = "REP_PATH";

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增Report");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("REPORT_ID", this.get32UUID()); // 主键
		pd.put("OPERATOR", Jurisdiction.getUsername()); // 操作人
		pd.put("RUN_DATETIME", Tools.date2Str(new Date()));
		pd.put("RESULT", "R");// 运行中
		// 组装运行参数
		pd.put("RUN_PARAM", pd.getString("PERIOD") + "," + pd.getString("FIRM_CODE") + "," + pd.getString("FUND_ID")
				+ "," + pd.getString("MF") + "," + pd.getString("ETF") + "," + pd.getString("STRUCTURED"));
		reportService.save(pd);
		// 执行并发程序
		taskExecutor.submit(new ReportRunWorker(reportService, pd));
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Report");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> varList = reportService.list(page); // 列出Report列表
		mv.setViewName("system/report/report_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 去新增页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> companyList = mgrcompanyService.listAll(pd);
		List<PageData> fundList = fundService.listAllFund(pd);
		List<PageData> p1List = Lists.newArrayList();
		List<PageData> p2List = Lists.newArrayList();
		List<PageData> p3List = Lists.newArrayList();
		List<PageData> p4List = Lists.newArrayList();
		List<PageData> p5List = Lists.newArrayList();
		List<PageData> pList = reportService.listParagraphAll(pd);
		for (PageData pds : pList) {
			switch (pds.getString("PARAGRAPH_TYPE")) {
			case "RP01":
				p1List.add(pds);
				break;
			case "RP02":
				p2List.add(pds);
				break;
			case "RP03":
				p3List.add(pds);
				break;
			case "RP04":
				p4List.add(pds);
				break;
			case "RP05":
				p5List.add(pds);
				break;
			default:
			}
		}
		// 输出路径
		String configValue = configService.findByCode(REP_PATH);
		mv.setViewName("system/report/report_edit");
		mv.addObject("companyList", companyList);
		mv.addObject("fundList", fundList);
		mv.addObject("p1List", p1List);
		mv.addObject("p2List", p2List);
		mv.addObject("p3List", p3List);
		mv.addObject("p4List", p4List);
		mv.addObject("p5List", p5List);
		mv.addObject("outbondPath", configValue);
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}
}
