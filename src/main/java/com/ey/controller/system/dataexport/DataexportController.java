package com.ey.controller.system.dataexport;

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
import com.ey.service.system.mgrcompany.MgrcompanyManager;
import com.ey.util.AppUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.PageData;

/** 
 * 说明：导出工作台
 * 创建人：andychen
 * 创建时间：2017-08-22
 */
@Controller
@RequestMapping(value="/dataexport")
public class DataexportController extends BaseController {
	
	String menuUrl = "dataexport/list.do"; //菜单地址(权限用)
	@Resource(name="dataexportService")
	private DataexportManager dataexportService;
	@Resource(name="mgrcompanyService")
	private MgrcompanyManager mgrcompanyService;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Dataexport");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = dataexportService.list(page);	//列出Dataexport列表
		List<PageData> companyList = mgrcompanyService.listAll(pd);
		List<PageData> periodList = dataexportService.listPeriod(pd);
		mv.setViewName("system/dataexport/dataexport_list");
		mv.addObject("varList", varList);
		mv.addObject("companyList", companyList);
		mv.addObject("periodList", periodList);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	

	
	 /**批量导出
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/exportAll")
	@ResponseBody
	public Object deleteAll() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"批量导出Dataexport");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			pd.put("msg", "ok");
		}else{
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
