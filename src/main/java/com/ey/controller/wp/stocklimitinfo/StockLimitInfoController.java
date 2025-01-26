package com.ey.controller.wp.stocklimitinfo;

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
import com.ey.service.wp.stocklimitinfo.StockLimitInfoManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.FileDownload;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.fileimport.MapResult;

/** 
 * 说明：股票流通受限信息
 * 创建人：andychen
 * 创建时间：2017-12-02
 */
@Controller
@RequestMapping(value="/stocklimitinfo")
public class StockLimitInfoController extends BaseController {
	
	String menuUrl = "stocklimitinfo/list.do"; //菜单地址(权限用)
	@Resource(name="stocklimitinfoService")
	private StockLimitInfoManager stocklimitinfoService;
	@Resource(name = "logService")
	private LogerManager logManager;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增StockLimitInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("STOCKLIMITINFO_ID", this.get32UUID());	//主键
		pd.put("ACTIVE", "Y");	//启用
		pd.put("STATUS", "");	//状态
		stocklimitinfoService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除StockLimitInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		stocklimitinfoService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改StockLimitInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		stocklimitinfoService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表StockLimitInfo");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = stocklimitinfoService.list(page);	//列出StockLimitInfo列表
		mv.setViewName("wp/stocklimitinfo/stocklimitinfo_list");
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
		mv.setViewName("wp/stocklimitinfo/stocklimitinfo_edit");
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
		pd = stocklimitinfoService.findById(pd);	//根据ID读取
		mv.setViewName("wp/stocklimitinfo/stocklimitinfo_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除StockLimitInfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			stocklimitinfoService.deleteAll(ArrayDATA_IDS);
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
		mv.setViewName("wp/stocklimitinfo/uploadexcel");
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
		FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Stock_limit_info.xlsx",
				"Stock_limit_info.xlsx");
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
		logManager.save(Jurisdiction.getUsername(), "从EXCEL导入股票受限信息到数据库");
		ModelAndView mv = this.getModelAndView();
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		}
		MapResult mapResult = readExcel(file, SLI_IMPORT_TEMPLATE_CODE);
		/* 存入数据库操作====================================== */
		List<Map> maps = mapResult.getResult();
		stocklimitinfoService.saveBatch(maps);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出StockLimitInfo到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("期间");	//1
		titles.add("公司代码");	//2
		titles.add("基金代码");	//3
		titles.add("科目代码");	//4
		titles.add("股票代码");	//5
		titles.add("股票名称");	//6
		titles.add("交易市场");	//7
		titles.add("子类型");	//8
		titles.add("期末交易状态");	//9
		titles.add("流通受限类型");	//10
		titles.add("认购日");	//11
		titles.add("认购价格");	//12
		titles.add("可流通日");	//13
		titles.add("Createdby");	//14
		titles.add("ReviewedBy");	//15
		titles.add("启用");	//16
		titles.add("状态");	//17
		dataMap.put("titles", titles);
		List<PageData> varOList = stocklimitinfoService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("PERIOD"));	    //1
			vpd.put("var2", varOList.get(i).getString("FIRM_CODE"));	    //2
			vpd.put("var3", varOList.get(i).getString("FUND_ID"));	    //3
			vpd.put("var4", varOList.get(i).getString("ACCOUNT_NUM"));	    //4
			vpd.put("var5", varOList.get(i).getString("STOCK_CODE"));	    //5
			vpd.put("var6", varOList.get(i).getString("STOCK_NAME"));	    //6
			vpd.put("var7", varOList.get(i).getString("MARKET"));	    //7
			vpd.put("var8", varOList.get(i).getString("SUB_TYPE"));	    //8
			vpd.put("var9", varOList.get(i).getString("TRX_STATUS"));	    //9
			vpd.put("var10", varOList.get(i).getString("RESTRICT_TYPE"));	    //10
			vpd.put("var11", varOList.get(i).getString("SUBSCRIBE_DATE"));	    //11
			vpd.put("var12", varOList.get(i).get("SUBSCRIBE_PRICE").toString());	//12
			vpd.put("var13", varOList.get(i).getString("LEFTING_DATE"));	    //13
			vpd.put("var14", varOList.get(i).getString("CREATOR"));	    //14
			vpd.put("var15", varOList.get(i).getString("REVIEWER"));	    //15
			vpd.put("var16", varOList.get(i).getString("ACTIVE"));	    //16
			vpd.put("var17", varOList.get(i).getString("STATUS"));	    //17
			vpd.put("var18", varOList.get(i).getString("LIMIT_LEN"));	    //18 20220630新增
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
