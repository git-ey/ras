package com.ey.controller.wp.subledger;

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
import com.ey.service.wp.subledger.SubLedgerManager;
import com.ey.util.AppUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;

/** 
 * 说明：EY明细账
 * 创建人：andychen
 * 创建时间：2017-11-20
 */
@Controller
@RequestMapping(value="/subledger")
public class SubLedgerController extends BaseController {
	
	String menuUrl = "subledger/list.do"; //菜单地址(权限用)
	@Resource(name="subledgerService")
	private SubLedgerManager subledgerService;
	@Resource(name="fundService")
	private FundManager fundService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增SubLedger");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("SUBLEDGER_ID", this.get32UUID());	//主键
		subledgerService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除SubLedger");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		subledgerService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改SubLedger");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		subledgerService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表SubLedger");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> fundList = fundService.listAllFund(pd);	//列出所有基金列表
		List<PageData> varList = subledgerService.list(page);	//列出SubLedger列表
		mv.setViewName("wp/subledger/subledger_list");
		mv.addObject("fundList", fundList);
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
		mv.setViewName("wp/subledger/subledger_edit");
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
		pd = subledgerService.findById(pd);	//根据ID读取
		mv.setViewName("wp/subledger/subledger_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除SubLedger");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			subledgerService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		}else{
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出SubLedger到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("基金");	//1
		titles.add("期间");	//2
		titles.add("科目代码");	//3
		titles.add("凭证序号");	//4
		titles.add("记账日期");	//5
		titles.add("凭证编号");	//6
		titles.add("摘要");	//7
		titles.add("币种");	//8
		titles.add("借方份额");	//9
		titles.add("贷方份额");	//10
		titles.add("借方金额");	//11
		titles.add("贷方金额");	//12
		titles.add("年末借贷方");	//13
		titles.add("年末份额");	//14
		titles.add("年末金额");	//15
		titles.add("启用");	//16
		titles.add("状态");	//17
		dataMap.put("titles", titles);
		List<PageData> varOList = subledgerService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FUND_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("PERIOD"));	    //2
			vpd.put("var3", varOList.get(i).getString("ACCOUNT_NUM"));	    //3
			vpd.put("var4", varOList.get(i).getString("LINE"));	    //4
			vpd.put("var5", varOList.get(i).getString("EFFECTIVE_DATE"));	    //5
			vpd.put("var6", varOList.get(i).getString("SQUENCE_NUM"));	    //6
			vpd.put("var7", varOList.get(i).getString("DESCRIPTION"));	    //7
			vpd.put("var8", varOList.get(i).getString("CURRENCY"));	    //8
			vpd.put("var9", varOList.get(i).get("DR_UNITS").toString());	//9
			vpd.put("var10", varOList.get(i).get("CR_UNITS").toString());	//10
			vpd.put("var11", varOList.get(i).get("DR_AMOUNT").toString());	//11
			vpd.put("var12", varOList.get(i).get("CR_AMOUNT").toString());	//12
			vpd.put("var13", varOList.get(i).get("END_DRCR").toString());	//13
			vpd.put("var14", varOList.get(i).get("END_UNITS").toString());	//14
			vpd.put("var15", varOList.get(i).get("END_BALANCE").toString());	//15
			vpd.put("var16", varOList.get(i).getString("ACTIVE"));	    //16
			vpd.put("var17", varOList.get(i).getString("STATUS"));	    //17
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
