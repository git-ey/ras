package com.ey.controller.system.report;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ey.controller.base.BaseController;
import com.ey.entity.Page;
import com.ey.service.system.fund.FundManager;
import com.ey.service.system.mgrcompany.MgrcompanyManager;
import com.ey.service.system.report.ReportManager;
import com.ey.util.AppUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
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
		reportService.save(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "删除Report");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		reportService.delete(pd);
		out.write("success");
		out.close();
	}

	/**
	 * 修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit")
	public ModelAndView edit() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "修改Report");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		reportService.edit(pd);
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
		mv.setViewName("system/report/report_edit");
		mv.addObject("companyList", companyList);
		mv.addObject("fundList", fundList);
		mv.addObject("p1List", p1List);
		mv.addObject("p2List", p2List);
		mv.addObject("p3List", p3List);
		mv.addObject("p4List", p4List);
		mv.addObject("p5List", p5List);
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去修改页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	public ModelAndView goEdit() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = reportService.findById(pd); // 根据ID读取
		mv.setViewName("system/report/report_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 批量删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "批量删除Report");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return null;
		} // 校验权限
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (null != DATA_IDS && !"".equals(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			reportService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		} else {
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}

	/**
	 * 导出到excel
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel")
	public ModelAndView exportExcel() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "导出Report到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("运行参数"); // 1
		titles.add("第一段"); // 2
		titles.add("第二段"); // 3
		titles.add("第三段"); // 4
		titles.add("第四段"); // 5
		titles.add("第五段"); // 6
		titles.add("操作人"); // 7
		titles.add("运行时间"); // 8
		titles.add("导出路径"); // 9
		dataMap.put("titles", titles);
		List<PageData> varOList = reportService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("RUN_PARAM")); // 1
			vpd.put("var2", varOList.get(i).getString("P1")); // 2
			vpd.put("var3", varOList.get(i).getString("P2")); // 3
			vpd.put("var4", varOList.get(i).getString("P3")); // 4
			vpd.put("var5", varOList.get(i).getString("P4")); // 5
			vpd.put("var6", varOList.get(i).getString("P5")); // 6
			vpd.put("var7", varOList.get(i).getString("OPERATOR")); // 7
			vpd.put("var8", varOList.get(i).getString("RUN_DATETIME")); // 8
			vpd.put("var9", varOList.get(i).getString("OUTBOND_PATH")); // 9
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}
}
