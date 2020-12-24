package com.ey.controller.system.fund;

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
import com.ey.service.system.fund.FundManager;
import com.ey.service.system.loger.LogerManager;
import com.ey.service.system.mgrcompany.MgrcompanyManager;
import com.ey.service.system.othdis.OthdisHeadManager;
import com.ey.service.system.term.TermHeadManager;
import com.ey.service.system.urowset.URowSetManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.FileDownload;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.fileimport.MapResult;

/** 
 * 说明：基金信息
 * 创建人：andychen
 * 创建时间：2017-08-22
 */
@Controller
@RequestMapping(value="/fund")
public class FundController extends BaseController {
	
	String menuUrl = "fund/list.do"; //菜单地址(权限用)
	@Resource(name="fundService")
	private FundManager fundService;
	@Resource(name="mgrcompanyService")
	private MgrcompanyManager mgrcompanyService;
	@Resource(name = "logService")
	private LogerManager logManager;
	@Resource(name="termheadService")
	private TermHeadManager termheadService;
	@Resource(name="othdisheadService")
	private OthdisHeadManager othdisheadService;
	@Resource(name="urowsetService")
	private URowSetManager urowsetService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Fund");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		fundService.save(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public Object delete() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除Fund");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		Map<String,String> map = new HashMap<String,String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errInfo = "success";
		if(Integer.parseInt(fundService.findCount(pd).get("zs").toString()) > 0){
			errInfo = "false";
		}else{
			fundService.delete(pd);
		}
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改Fund");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		fundService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表Fund");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = fundService.list(page);	//列出Fund列表
		mv.setViewName("system/fund/fund_list");
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
		pd = fundService.findById(pd);
		mv.setViewName("system/fund/fund_edit");
		List<PageData> companyList = mgrcompanyService.listAll(pd);
		// 期间账龄信息
		List<PageData> termList = termheadService.listAll(pd);
		// 其他负债批量口径
		List<PageData> othdisList = othdisheadService.listAll(pd);
		// U底稿行集
		List<PageData> uRowSetList = urowsetService.listURowSet(pd);
		mv.addObject("companyList", companyList);
		mv.addObject("othdisList", othdisList);
		mv.addObject("termList",termList);
		mv.addObject("uRowSetList",uRowSetList);
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
		pd = fundService.findById(pd);	//根据ID读取
		List<PageData> companyList = mgrcompanyService.listAll(pd);
		mv.setViewName("system/fund/fund_edit");
		// 期间账龄信息
		List<PageData> termList = termheadService.listAll(pd);
		// 其他负债批量口径
		List<PageData> othdisList = othdisheadService.listAll(pd);
		// U底稿行集
		List<PageData> uRowSetList = urowsetService.listURowSet(pd);
		mv.addObject("companyList", companyList);
		mv.addObject("othdisList", othdisList);
		mv.addObject("termList",termList);
		mv.addObject("uRowSetList",uRowSetList);
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除Fund");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			fundService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		}else{
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}
	
	/**打开上传EXCEL页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/goUploadExcel")
	public ModelAndView goUploadExcel()throws Exception{
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("system/fund/uploadexcel");
		return mv;
	}
	
	/**下载模版
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/downExcel")
	public void downExcel(HttpServletResponse response) throws Exception{
		FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Fund_Control.xlsx", "Fund_Control.xlsx");
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
		logManager.save(Jurisdiction.getUsername(), "从EXCEL导入基金信息到数据库");
		ModelAndView mv = this.getModelAndView();
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		}
		MapResult mapResult = readExcel(file, FI_IMPORT_TEMPLATE_CODE);
		/* 存入数据库操作====================================== */
		List<Map> maps = mapResult.getResult();
		fundService.saveBatch(maps);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出Fund到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("基金代码");	//1
		titles.add("管理公司代码");	//2
		titles.add("基金简称");	//3
		titles.add("基金全称");	//4
		titles.add("基金原名");	//5
		titles.add("帐套号");	//6
		titles.add("TA基金名称");	//7
		titles.add("财务系统");	//8
		titles.add("分级");	//9
		titles.add("保本");	//10
		titles.add("封闭");	//11
		titles.add("沪港");	//12
		titles.add("QD");	//13
		titles.add("货基");	//14
		titles.add("指数");	//15
		titles.add("LOF");	//16
		titles.add("ETF");	//17
		titles.add("FOF");	//18
		titles.add("货币量纲");	//19
		titles.add("合同生效日");	//20
		titles.add("基金终止日");	//21
		titles.add("基金转型日");	//22
		titles.add("利率风险敞口账龄");	//23
		titles.add("其他负债披露类型");	//24
		titles.add("启用");	//25
		titles.add("状态");	//26
		dataMap.put("titles", titles);
		List<PageData> varOList = fundService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FUND_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("FIRM_CODE"));	    //2
			vpd.put("var3", varOList.get(i).getString("SHORT_NAME"));	    //3
			vpd.put("var4", varOList.get(i).getString("FULL_NAME"));	    //4
			vpd.put("var5", varOList.get(i).getString("FULL_NAME_ORIGINAL"));	    //5
			vpd.put("var6", varOList.get(i).getString("LEDGER_NUM"));	    //6
			vpd.put("var7", varOList.get(i).getString("TA_NAME"));	    //7
			vpd.put("var8", varOList.get(i).getString("FIN_SYSTEM"));	    //8
			vpd.put("var9", varOList.get(i).getString("STRUCTURED"));	    //9
			vpd.put("var10", varOList.get(i).getString("GUARANTEED"));	    //10
			vpd.put("var11", varOList.get(i).getString("CLOSED"));	    //11
			vpd.put("var12", varOList.get(i).getString("SHHK"));	    //12
			vpd.put("var13", varOList.get(i).getString("QD"));	    //13
			vpd.put("var14", varOList.get(i).getString("MF"));	    //14
			vpd.put("var15", varOList.get(i).getString("IDX"));	    //15
			vpd.put("var16", varOList.get(i).getString("LOF"));	    //16
			vpd.put("var17", varOList.get(i).getString("ETF"));	    //17
			vpd.put("var18", varOList.get(i).getString("FOF"));	    //18
			vpd.put("var19", varOList.get(i).get("UNIT"));	//19
			vpd.put("var20", varOList.get(i).get("DATE_FROM"));	    //20
			vpd.put("var21", varOList.get(i).get("DATE_TO"));	    //21
			vpd.put("var22", varOList.get(i).get("DATE_TRANSFORM"));	    //22
			vpd.put("var23", varOList.get(i).getString("INTEREST_RATE_PERIOD"));	    //23
			vpd.put("var24", varOList.get(i).getString("OTHER_LIABILITIES"));	    //24
			vpd.put("var25", varOList.get(i).getString("ACTIVE"));	    //25
			vpd.put("var26", varOList.get(i).getString("STATUS"));	    //26
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
