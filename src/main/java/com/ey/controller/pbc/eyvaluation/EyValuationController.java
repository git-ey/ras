package com.ey.controller.pbc.eyvaluation;

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
import com.ey.util.AppUtil;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.Jurisdiction;
import com.ey.util.Tools;
import com.ey.service.pbc.eyvaluation.EyValuationManager;

/** 
 * 说明：EY估值表
 * 创建人：andychen
 * 创建时间：2017-08-13
 */
@Controller
@RequestMapping(value="/eyvaluation")
public class EyValuationController extends BaseController {
	
	String menuUrl = "eyvaluation/list.do"; //菜单地址(权限用)
	@Resource(name="eyvaluationService")
	private EyValuationManager eyvaluationService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Eyvaluation");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("VALUATION_ID", this.get32UUID());	//主键
		eyvaluationService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除Eyvaluation");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		eyvaluationService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改Eyvaluation");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		eyvaluationService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表Eyvaluation");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = eyvaluationService.list(page);	//列出Eyvaluation列表
		mv.setViewName("pbc/eyvaluation/eyvaluation_list");
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
		mv.setViewName("pbc/eyvaluation/eyvaluation_edit");
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
		pd = eyvaluationService.findById(pd);	//根据ID读取
		mv.setViewName("pbc/eyvaluation/eyvaluation_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除Eyvaluation");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			eyvaluationService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出Eyvaluation到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("基金ID");	//1
		titles.add("期间");	//2
		titles.add("估值日期");	//3
		titles.add("EY科目代码");	//4
		titles.add("应收应付");	//5
		titles.add("币种");	//6
		titles.add("汇率");	//7
		titles.add("数量");	//8
		titles.add("单位成本");	//9
		titles.add("成本_原币");	//10
		titles.add("成本_本位币");	//11
		titles.add("行情收市价");	//12
		titles.add("市值_原币");	//13
		titles.add("市值_本位币");	//14
		titles.add("估值增值_原币");	//15
		titles.add("估值增值_本位币");	//16
		titles.add("停牌信息");	//17
		titles.add("备注");	//18
		titles.add("启用");	//19
		titles.add("状态");	//20
		titles.add("属性1");	//21
		titles.add("属性2");	//22
		titles.add("属性3");	//23
		titles.add("属性4");	//24
		titles.add("属性5");	//25
		titles.add("属性6");	//26
		dataMap.put("titles", titles);
		List<PageData> varOList = eyvaluationService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FUND_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("PERIOD"));	    //2
			vpd.put("var3", varOList.get(i).getString("VDATE"));	    //3
			vpd.put("var4", varOList.get(i).getString("EY_ACCOUNT_NUM"));	    //4
			vpd.put("var5", varOList.get(i).getString("APAR"));	    //5
			vpd.put("var6", varOList.get(i).getString("CURRENCY"));	    //6
			vpd.put("var7", varOList.get(i).get("EXCHANGE_RATE").toString());	//7
			vpd.put("var8", varOList.get(i).get("QUANTITY").toString());	//8
			vpd.put("var9", varOList.get(i).get("UNIT_COST").toString());	//9
			vpd.put("var10", varOList.get(i).get("TOTAL_COST_ENTERED").toString());	//10
			vpd.put("var11", varOList.get(i).get("TOTAL_COST_CNY").toString());	//11
			vpd.put("var12", varOList.get(i).get("UNIT_PRICE").toString());	//12
			vpd.put("var13", varOList.get(i).get("MKT_VALUE_ENTERED").toString());	//13
			vpd.put("var14", varOList.get(i).get("MKT_VALUE_CNY").toString());	//14
			vpd.put("var15", varOList.get(i).get("APPRECIATION_ENTERED").toString());	//15
			vpd.put("var16", varOList.get(i).get("APPRECIATION_CNY").toString());	//16
			vpd.put("var17", varOList.get(i).getString("SUSPENSION_INFO"));	    //17
			vpd.put("var18", varOList.get(i).getString("DESCRIPTION"));	    //18
			vpd.put("var19", varOList.get(i).getString("ACTIVE"));	    //19
			vpd.put("var20", varOList.get(i).getString("STATUS"));	    //20
			vpd.put("var21", varOList.get(i).getString("ATTR1"));	    //21
			vpd.put("var22", varOList.get(i).getString("ATTR2"));	    //22
			vpd.put("var23", varOList.get(i).getString("ATTR3"));	    //23
			vpd.put("var24", varOList.get(i).getString("ATTR4"));	    //24
			vpd.put("var25", varOList.get(i).getString("ATTR5"));	    //25
			vpd.put("var26", varOList.get(i).getString("ATTR6"));	    //26
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
