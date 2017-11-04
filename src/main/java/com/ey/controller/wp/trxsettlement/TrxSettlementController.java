package com.ey.controller.wp.trxsettlement;

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
import com.ey.service.system.dataexport.DataexportManager;
import com.ey.service.wp.trxsettlement.TrxSettlementManager;
import com.ey.util.AppUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;

/** 
 * 说明：成交清算日报表
 * 创建人：andychen
 * 创建时间：2017-11-04
 */
@Controller
@RequestMapping(value="/trxsettlement")
public class TrxSettlementController extends BaseController {
	
	String menuUrl = "trxsettlement/list.do"; //菜单地址(权限用)
	@Resource(name="dataexportService")
	private DataexportManager dataexportService;
	@Resource(name="trxsettlementService")
	private TrxSettlementManager trxsettlementService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增TrxSettlement");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("TRXSETTLEMENT_ID", this.get32UUID());	//主键
		trxsettlementService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除TrxSettlement");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		trxsettlementService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改TrxSettlement");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		trxsettlementService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表TrxSettlement");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> periodList = dataexportService.listPeriod(pd);
		List<PageData>	varList = trxsettlementService.list(page);	//列出TrxSettlement列表
		mv.setViewName("wp/trxsettlement/trxsettlement_list");
		mv.addObject("varList", varList);
		mv.addObject("periodList", periodList);
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
		mv.setViewName("wp/trxsettlement/trxsettlement_edit");
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
		pd = trxsettlementService.findById(pd);	//根据ID读取
		mv.setViewName("wp/trxsettlement/trxsettlement_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除TrxSettlement");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			trxsettlementService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出TrxSettlement到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("基金ID");	//1
		titles.add("期间");	//2
		titles.add("交易日期");	//3
		titles.add("币种");	//4
		titles.add("EY买卖类型");	//5
		titles.add("EY股债类型");	//6
		titles.add("证券代码");	//7
		titles.add("证券名称");	//8
		titles.add("成交数量");	//9
		titles.add("成交金额");	//10
		titles.add("席位佣金");	//11
		titles.add("印花税");	//12
		titles.add("经手费");	//13
		titles.add("过户费");	//14
		titles.add("证管费");	//15
		titles.add("风险金");	//16
		titles.add("其他费用");	//17
		titles.add("券商过户费");	//18
		titles.add("债券利息");	//19
		titles.add("回购收益");	//20
		titles.add("实际清算金额");	//21
		titles.add("业务类型");	//22
		titles.add("交易市场");	//23
		titles.add("交易平台");	//24
		titles.add("席位号");	//25
		titles.add("均价");	//26
		titles.add("EY证券代码");	//27
		titles.add("EY证券名称");	//28
		titles.add("启用");	//29
		titles.add("状态");	//30
		dataMap.put("titles", titles);
		List<PageData> varOList = trxsettlementService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FUND_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("PERIOD"));	    //2
			vpd.put("var3", varOList.get(i).getString("TRANSACTION_DATE"));	    //3
			vpd.put("var4", varOList.get(i).getString("CURRENCY"));	    //4
			vpd.put("var5", varOList.get(i).getString("EY_BUYSELL_TYPE"));	    //5
			vpd.put("var6", varOList.get(i).getString("EY_STOCKBOND_TYPE"));	    //6
			vpd.put("var7", varOList.get(i).getString("STOCK_CODE"));	    //7
			vpd.put("var8", varOList.get(i).getString("STOCK_NAME"));	    //8
			vpd.put("var9", varOList.get(i).get("QUANTITY").toString());	//9
			vpd.put("var10", varOList.get(i).get("AMOUNT").toString());	//10
			vpd.put("var11", varOList.get(i).get("SEAT_COMMISSION").toString());	//11
			vpd.put("var12", varOList.get(i).get("STAMPS").toString());	//12
			vpd.put("var13", varOList.get(i).get("BROKERAGE_FEE").toString());	//13
			vpd.put("var14", varOList.get(i).get("TRANSFER_FEE").toString());	//14
			vpd.put("var15", varOList.get(i).get("SEC_FEE").toString());	//15
			vpd.put("var16", varOList.get(i).get("CONTINGENCY_FEE").toString());	//16
			vpd.put("var17", varOList.get(i).get("OTHER_FEE").toString());	//17
			vpd.put("var18", varOList.get(i).get("BROKERAGE_TRANSFER_FEE").toString());	//18
			vpd.put("var19", varOList.get(i).get("BOND_INTEREST").toString());	//19
			vpd.put("var20", varOList.get(i).get("REPO_EARNINGS").toString());	//20
			vpd.put("var21", varOList.get(i).get("NET_AMOUNT").toString());	//21
			vpd.put("var22", varOList.get(i).getString("TRX_TYPE"));	    //22
			vpd.put("var23", varOList.get(i).getString("MARKET"));	    //23
			vpd.put("var24", varOList.get(i).getString("AGENT"));	    //24
			vpd.put("var25", varOList.get(i).getString("SEAT_NUM"));	    //25
			vpd.put("var26", varOList.get(i).get("AVERAGE_PRICE").toString());	//26
			vpd.put("var27", varOList.get(i).getString("EY_SECURITY_CODE"));	    //27
			vpd.put("var28", varOList.get(i).getString("EY_SECURITY_NAME"));	    //28
			vpd.put("var29", varOList.get(i).getString("ACTIVE"));	    //29
			vpd.put("var30", varOList.get(i).getString("STATUS"));	    //30
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
