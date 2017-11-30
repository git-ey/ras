package com.ey.controller.system.concruning;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ey.controller.base.BaseController;
import com.ey.entity.Page;
import com.ey.service.system.conc.ConcManager;
import com.ey.service.system.concparam.ConcParamManager;
import com.ey.service.system.concruning.ConcRuningManager;
import com.ey.util.AppUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.Tools;

/**
 * 说明：并发工作台 创建时间：2017-08-15
 */
@Controller
@RequestMapping(value = "/concruning")
public class ConcRuningController extends BaseController {

	String menuUrl = "concruning/list.do"; // 菜单地址(权限用)
	@Resource(name = "concruningService")
	private ConcRuningManager concruningService;
	@Resource(name = "concService")
	private ConcManager concService;
	@Resource(name = "concParamService")
	private ConcParamManager concParamService;
	@Autowired
	@Qualifier("taskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增ConcRuning");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 获取运行ID
		String concRuningId = this.get32UUID();
		// 获取并发程序定义
		PageData concPd = concService.findByCode(pd);
		// 构建并发程序参数
		StringBuilder concParam = new StringBuilder();
		List<Map.Entry<String, Object>> pds = new ArrayList<Map.Entry<String, Object>>(pd.entrySet());
		// 排序
		Collections.sort(pds, new Comparator<Map.Entry<String, Object>>() {
			@Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});
		int i = 1;
		for (Map.Entry<String, Object> et : pds) {
			if (i < pds.size()) {
				concParam.append(et.getValue() + ",");
			}
			i++;
		}
		pd.put("CONC_PROGRAM", concPd.getString("CONC_PROGRAM"));
		// 参数排序
		concParam.append(concRuningId);
		String[] paramArray = concParam.toString().split(",", -1);// 分割后保留空字符
		pd.put("CONC_PARAM", paramArray);
		// 记录日志
		pd.put("CONCRUNING_ID", concRuningId); // 主键
		pd.put("START_DATETIME", Tools.date2Str(new Date())); // 开始时间
		pd.put("RESULT", "R"); // 运行状态
		pd.put("OPERATOR", Jurisdiction.getUsername()); // 运行人
		concruningService.save(pd);
		// 执行并发程序
		taskExecutor.submit(new ConcRunWorker(concruningService, pd));
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
		logBefore(logger, Jurisdiction.getUsername() + "列表ConcRuning");
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
		List<PageData> varList = concruningService.list(page); // 列出ConcRuning列表
		mv.setViewName("system/concruning/concruning_list");
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
		pd.put("ENABLED_FLAG", "Y");
		// 查询可用并发程序
		Page page = new Page();
		page.setPd(pd);
		List<PageData> coucList = concService.list(page);
		mv.setViewName("system/concruning/concruning_edit");
		mv.addObject("msg", "save");
		mv.addObject("concList", coucList);
		mv.addObject("pd", pd);
		return mv;
	}

	@RequestMapping(value = "/getConcParam")
	@ResponseBody
	public Object getConcParam() throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		// 初始化参数
		Page page = new Page();
		page.setPd(pd);
		List<PageData> pdList = concParamService.listParam(page);
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
		logBefore(logger, Jurisdiction.getUsername() + "导出ConcRunLog到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<PageData> varOList = concruningService.listLog(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			// 构建Excel标题
			if (i == 0) {
				List<String> titles = new ArrayList<String>();
				titles.add(varOList.get(i).getString("LOG_MSG_1"));
				titles.add(varOList.get(i).getString("LOG_MSG_2"));
				titles.add(varOList.get(i).getString("LOG_MSG_3"));
				titles.add(varOList.get(i).getString("LOG_MSG_4"));
				titles.add(varOList.get(i).getString("LOG_MSG_5"));
				titles.add(varOList.get(i).getString("LOG_MSG_6"));
				titles.add(varOList.get(i).getString("LOG_MSG_7"));
				titles.add(varOList.get(i).getString("LOG_MSG_8"));
				titles.add(varOList.get(i).getString("LOG_MSG_9"));
				titles.add(varOList.get(i).getString("LOG_MSG_10"));
				titles.add(varOList.get(i).getString("LOG_MSG_11"));
				titles.add(varOList.get(i).getString("LOG_MSG_12"));
				titles.add(varOList.get(i).getString("LOG_MSG_13"));
				titles.add(varOList.get(i).getString("LOG_MSG_14"));
				titles.add(varOList.get(i).getString("LOG_MSG_15"));
				titles.add(varOList.get(i).getString("LOG_MSG_16"));
				titles.add(varOList.get(i).getString("LOG_MSG_17"));
				titles.add(varOList.get(i).getString("LOG_MSG_18"));
				titles.add(varOList.get(i).getString("LOG_MSG_19"));
				titles.add(varOList.get(i).getString("LOG_MSG_20"));
				dataMap.put("titles", titles);
			} else {
			    // 构建Excel内容
				PageData vpd = new PageData();
				vpd.put("var1", varOList.get(i).getString("LOG_MSG_1")); // 1
				vpd.put("var2", varOList.get(i).getString("LOG_MSG_2")); // 2
				vpd.put("var3", varOList.get(i).getString("LOG_MSG_3")); // 3
				vpd.put("var4", varOList.get(i).getString("LOG_MSG_4")); // 4
				vpd.put("var5", varOList.get(i).getString("LOG_MSG_5")); // 5
				vpd.put("var6", varOList.get(i).getString("LOG_MSG_6")); // 6
				vpd.put("var7", varOList.get(i).getString("LOG_MSG_7")); // 7
				vpd.put("var8", varOList.get(i).getString("LOG_MSG_8")); // 8
				vpd.put("var9", varOList.get(i).getString("LOG_MSG_9")); // 9
				vpd.put("var10", varOList.get(i).getString("LOG_MSG_10")); // 10
				vpd.put("var11", varOList.get(i).getString("LOG_MSG_11")); // 11
				vpd.put("var12", varOList.get(i).getString("LOG_MSG_12")); // 12
				vpd.put("var13", varOList.get(i).getString("LOG_MSG_13")); // 13
				vpd.put("var14", varOList.get(i).getString("LOG_MSG_14")); // 14
				vpd.put("var15", varOList.get(i).getString("LOG_MSG_15")); // 15
				vpd.put("var16", varOList.get(i).getString("LOG_MSG_16")); // 16
				vpd.put("var17", varOList.get(i).getString("LOG_MSG_17")); // 17
				vpd.put("var18", varOList.get(i).getString("LOG_MSG_18")); // 18
				vpd.put("var19", varOList.get(i).getString("LOG_MSG_19")); // 19
				vpd.put("var20", varOList.get(i).getString("LOG_MSG_20")); // 20
				varList.add(vpd);
			}
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
