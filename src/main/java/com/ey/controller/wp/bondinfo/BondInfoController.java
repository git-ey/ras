package com.ey.controller.wp.bondinfo;

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
import com.ey.service.system.loger.LogerManager;
import com.ey.service.wp.bondinfo.BondInfoManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.FileDownload;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.fileimport.MapResult;

/** 
 * 说明：债券投资信息
 * 创建人：andychen
 * 创建时间：2017-12-03
 */
@Controller
@RequestMapping(value="/bondinfo")
public class BondInfoController extends BaseController {
	
	String menuUrl = "bondinfo/list.do"; //菜单地址(权限用)
	@Resource(name="bondinfoService")
	private BondInfoManager bondinfoService;
	@Resource(name = "logService")
	private LogerManager logManager;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增BondInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("BONDINFO_ID", this.get32UUID());	//主键
		bondinfoService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除BondInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		bondinfoService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改BondInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		bondinfoService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表BondInfo");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = bondinfoService.list(page);	//列出BondInfo列表
		mv.setViewName("wp/bondinfo/bondinfo_list");
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
		mv.setViewName("wp/bondinfo/bondinfo_edit");
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
		pd = bondinfoService.findById(pd);	//根据ID读取
		mv.setViewName("wp/bondinfo/bondinfo_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除BondInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			bondinfoService.deleteAll(ArrayDATA_IDS);
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
		mv.setViewName("wp/bondinfo/uploadexcel");
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
		FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "EyBond_Info.xlsx",
				"EyBond_Info.xlsx");
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
		logManager.save(Jurisdiction.getUsername(), "从EXCEL导入债券投资信息到数据库");
		ModelAndView mv = this.getModelAndView();
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		}
		MapResult mapResult = readExcel(file, EBI_IMPORT_TEMPLATE_CODE);
		/* 存入数据库操作====================================== */
		List<Map> maps = mapResult.getResult();
		bondinfoService.saveBatch(maps);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出BondInfo到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("期间");	//1
		titles.add("债券代码");	//2
		titles.add("债券简称");	//3
		titles.add("债券全称");	//4
		titles.add("上市市场");	//5
		titles.add("债券类型");	//6
		titles.add("停牌状态");	//7
		titles.add("停牌原因");	//8
		titles.add("发行面值");	//9
		titles.add("最新面值");	//10
		titles.add("票面利率%");	//11
		titles.add("发行价格 元");	//12
		titles.add("起息日期");	//13
		titles.add("止息日期");	//14
		titles.add("兑付日");	//15
		titles.add("计息方式");	//16
		titles.add("付息方式");	//17
		titles.add("年付息次数");	//18
		titles.add("一年多次付息应计利息处理规则");	//19
		titles.add("每年付息日");	//20
		titles.add("上一付息日");	//21
		titles.add("下一付息日");	//22
		titles.add("最新债券评级");	//23
		titles.add("最新债券评级机构");	//24
		titles.add("最新债券评级日期");	//25
		titles.add("发债主体最新评级");	//26
		titles.add("发债主体最新评级机构");	//27
		titles.add("发债主体最新评级日期");	//28
		titles.add("是否免税");	//29
		titles.add("税率");	//30
		titles.add("特殊条款");	//31
		titles.add("是否存在提前行权");	//32
		titles.add("第N年末行权");	//33
		titles.add("发行人利率选择权");	//34
		titles.add("回售权");	//35
		titles.add("赎回权");	//36
		dataMap.put("titles", titles);
		List<PageData> varOList = bondinfoService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("PERIOD"));	    //1
			vpd.put("var2", varOList.get(i).getString("BONG_CODE"));	    //2
			vpd.put("var3", varOList.get(i).getString("BOND_NAME"));	    //3
			vpd.put("var4", varOList.get(i).getString("FULL_NAME"));	    //4
			vpd.put("var5", varOList.get(i).getString("MARKET"));	    //5
			vpd.put("var6", varOList.get(i).getString("BOND_TYPE"));	    //6
			vpd.put("var7", varOList.get(i).getString("SUSPENSION"));	    //7
			vpd.put("var8", varOList.get(i).getString("SUSPENSION_INFO"));	    //8
			vpd.put("var9", varOList.get(i).get("PAR_VALUE_ISSUE").toString());	//9
			vpd.put("var10", varOList.get(i).get("PAR_VALUE_LAST").toString());	//10
			vpd.put("var11", varOList.get(i).get("COUPON_RATE").toString());	//11
			vpd.put("var12", varOList.get(i).get("ISSUE_PRICE").toString());	//12
			vpd.put("var13", varOList.get(i).getString("DATE_FROM"));	    //13
			vpd.put("var14", varOList.get(i).getString("DATE_TO"));	    //14
			vpd.put("var15", varOList.get(i).getString("DATE_PAY"));	    //15
			vpd.put("var16", varOList.get(i).getString("INTEREST_MODE"));	    //16
			vpd.put("var17", varOList.get(i).getString("PAYMENT_METHOD"));	    //17
			vpd.put("var18", varOList.get(i).get("PAYMENT_TIMES_YEAR").toString());	//18
			vpd.put("var19", varOList.get(i).getString("INTEREST_PAY_METHOD"));	    //19
			vpd.put("var20", varOList.get(i).getString("PAY_DATE_YEAR"));	    //20
			vpd.put("var21", varOList.get(i).getString("PAY_DATE_LAST"));	    //21
			vpd.put("var22", varOList.get(i).getString("PAY_DATE_NEXT"));	    //22
			vpd.put("var23", varOList.get(i).getString("BOND_RATING"));	    //23
			vpd.put("var24", varOList.get(i).getString("BOND_RATING_ORG"));	    //24
			vpd.put("var25", varOList.get(i).getString("BOND_RATING_DATE"));	    //25
			vpd.put("var26", varOList.get(i).getString("ENTITY_RATING"));	    //26
			vpd.put("var27", varOList.get(i).getString("ENTITY_RATING_ORG"));	    //27
			vpd.put("var28", varOList.get(i).getString("ENTITY_RATING_DATE"));	    //28
			vpd.put("var29", varOList.get(i).getString("TAX_FREE"));	    //29
			vpd.put("var30", varOList.get(i).get("TAX_RATE").toString());	//30
			vpd.put("var31", varOList.get(i).getString("SPECIAL_CLAUSE"));	    //31
			vpd.put("var32", varOList.get(i).getString("EARLY_EXERCISE"));	    //32
			vpd.put("var33", varOList.get(i).get("YEAR_N").toString());	//33
			vpd.put("var34", varOList.get(i).getString("INTEREST_RATE_OPTION"));	    //34
			vpd.put("var35", varOList.get(i).getString("SELL_BACK"));	    //35
			vpd.put("var36", varOList.get(i).getString("REDEMPTION"));	    //36
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
