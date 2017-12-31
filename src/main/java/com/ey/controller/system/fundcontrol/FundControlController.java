package com.ey.controller.system.fundcontrol;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ey.controller.base.BaseController;
import com.ey.entity.Page;
import com.ey.service.system.fundcontrol.FundControlManager;
import com.ey.service.system.loger.LogerManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.FileDownload;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.fileimport.MapResult;

/** 
 * 说明：报告章节控制表
 * 创建人：andychen
 * 创建时间：2017-12-31
 */
@Controller
@RequestMapping(value="/fundcontrol")
public class FundControlController extends BaseController {
	
	String menuUrl = "fundcontrol/list.do"; //菜单地址(权限用)
	@Resource(name="fundcontrolService")
	private FundControlManager fundcontrolService;
	@Resource(name = "logService")
	private LogerManager logManager;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增FundControl");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("FUNDCONTROL_ID", this.get32UUID());	//主键
		fundcontrolService.save(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	public void delete(PrintWriter out) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除FundControl");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		fundcontrolService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改FundControl");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		fundcontrolService.edit(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表FundControl");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = fundcontrolService.list(page);	//列出FundControl列表
		mv.setViewName("system/fundcontrol/fundcontrol_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	
	/**去新增页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goAdd")
	public ModelAndView goAdd()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/fundcontrol/fundcontrol_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	 /**去修改页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	public ModelAndView goEdit()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = fundcontrolService.findById(pd);	//根据ID读取
		mv.setViewName("system/fundcontrol/fundcontrol_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"批量删除FundControl");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			fundcontrolService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		}else{
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}
	
	/**
	 * 打开上传EXCEL页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/goUploadExcel")
	public ModelAndView goUploadExcel() throws Exception {
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("system/fundcontrol/uploadexcel");
		return mv;
	}

	/**
	 * 下载模版
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downExcel")
	public void downExcel(HttpServletResponse response) throws Exception {
		FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Fund_Control.xlsx",
				"Fund_Control.xlsx");
	}

	/**
	 * 从EXCEL导入到数据库
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/readExcel")
	public ModelAndView readExcel(@RequestParam(value = "excel", required = false) MultipartFile file)
			throws Exception {
		logManager.save(Jurisdiction.getUsername(), "从EXCEL导入报告章节控制表到数据库");
		ModelAndView mv = this.getModelAndView();
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		}
		MapResult mapResult = readExcel(file, SFC_IMPORT_TEMPLATE_CODE);
		/* 存入数据库操作====================================== */
		List<Map> maps = mapResult.getResult();
		fundcontrolService.saveBatch(maps);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出FundControl到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("基金代码");	//1
		titles.add("股票投资收益构成");	//2
		titles.add("股票买卖差价收入");	//3
		titles.add("股票赎回差价收入");	//4
		titles.add("股票申购差价收入");	//5
		titles.add("债券投资收益构成");	//6
		titles.add("债券买卖差价收入");	//7
		titles.add("债券赎回差价收入");	//8
		titles.add("债券申购差价收入");	//9
		titles.add("贵金属投资收益构成");	//10
		titles.add("贵金属买卖差价收入");	//11
		titles.add("贵金属赎回差价收入");	//12
		titles.add("贵金属申购差价收入");	//13
		titles.add("利率风险敏感性");	//14
		titles.add("价格风险敏感性");	//15
		titles.add("价格风险敞口");	//16
		titles.add("启用");	//17
		dataMap.put("titles", titles);
		List<PageData> varOList = fundcontrolService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FUND_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("STOCK_ALL"));	    //2
			vpd.put("var3", varOList.get(i).getString("STOCK_BS"));	    //3
			vpd.put("var4", varOList.get(i).getString("STOCK_R"));	    //4
			vpd.put("var5", varOList.get(i).getString("STOCK_P"));	    //5
			vpd.put("var6", varOList.get(i).getString("BOND_ALL"));	    //6
			vpd.put("var7", varOList.get(i).getString("BOND_BS"));	    //7
			vpd.put("var8", varOList.get(i).getString("BOND_R"));	    //8
			vpd.put("var9", varOList.get(i).getString("BOND_P"));	    //9
			vpd.put("var10", varOList.get(i).getString("GOLD_ALL"));	    //10
			vpd.put("var11", varOList.get(i).getString("GOLD_BS"));	    //11
			vpd.put("var12", varOList.get(i).getString("GOLD_R"));	    //12
			vpd.put("var13", varOList.get(i).getString("GOLD_P"));	    //13
			vpd.put("var14", varOList.get(i).getString("RISK_S_INT"));	    //14
			vpd.put("var15", varOList.get(i).getString("RISK_S_PRICE"));	    //15
			vpd.put("var16", varOList.get(i).getString("RISK_E_PRICE"));	    //16
			vpd.put("var17", varOList.get(i).getString("ACTIVE"));	    //17
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
