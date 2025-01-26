package com.ey.controller.system.reportcontent;

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
import com.ey.service.system.reportcontent.ReportContentManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.FileDownload;
import com.ey.util.Jurisdiction;
import com.ey.util.ObjectExcelView;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.fileimport.MapResult;

/** 
 * 说明：报告文本信息表
 * 创建人：andychen
 * 创建时间：2017-12-31
 */
@Controller
@RequestMapping(value="/reportcontent")
public class ReportContentController extends BaseController {
	
	String menuUrl = "reportcontent/list.do"; //菜单地址(权限用)
	@Resource(name="reportcontentService")
	private ReportContentManager reportcontentService;
	@Resource(name = "logService")
	private LogerManager logManager;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增ReportContent");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("REPORTCONTENT_ID", this.get32UUID());	//主键
		reportcontentService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除ReportContent");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		reportcontentService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改ReportContent");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		reportcontentService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表ReportContent");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = reportcontentService.list(page);	//列出ReportContent列表
		mv.setViewName("system/reportcontent/reportcontent_list");
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
		mv.setViewName("system/reportcontent/reportcontent_edit");
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
		pd = reportcontentService.findById(pd);	//根据ID读取
		mv.setViewName("system/reportcontent/reportcontent_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除ReportContent");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			reportcontentService.deleteAll(ArrayDATA_IDS);
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
		mv.setViewName("system/reportcontent/uploadexcel");
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
		FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Report_Content.xlsx",
				"Report_Content.xlsx");
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
		logManager.save(Jurisdiction.getUsername(), "从EXCEL导入报告文本信息表到数据库");
		ModelAndView mv = this.getModelAndView();
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		}
		MapResult mapResult = readExcel(file, SRC_IMPORT_TEMPLATE_CODE);
		/* 存入数据库操作====================================== */
		List<Map> maps = mapResult.getResult();
		reportcontentService.saveBatch(maps);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出ReportContent到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("基金代码");	//1
		titles.add("财务报表负责人的职位描述1");	//2
		titles.add("财务报表负责人的职位描述2");	//3
		titles.add("财务报表负责人的职位描述3");	//4
		titles.add("应支付关联方的佣金NOTES");	//5
		titles.add("管理费描述1");	//6
		titles.add("管理费描述2");	//7
		titles.add("管理费描述3");	//8
		titles.add("管理费描述4");	//9
		titles.add("管理费描述5");	//10
		titles.add("托管费描述1");	//11
		titles.add("托管费描述2");	//12
		titles.add("托管费描述3");	//13
		titles.add("托管费描述4");	//14
		titles.add("托管费描述5");	//15
		titles.add("销售服务费描述1"); //16
		titles.add("销售服务费描述2"); //17
		titles.add("销售服务费描述3"); //18
		titles.add("销售服务费描述4"); //19
		titles.add("销售服务费描述5"); //20
		titles.add("市场风险描述"); //21
		titles.add("利率风险描述"); //22
		titles.add("利率风险敞口描述"); //23
		titles.add("利率风险的敏感性分析");	//24
		titles.add("外汇风险描述"); //25
		titles.add("利率风险变动上升");	//26
		titles.add("利率风险变动下降");	//27
		titles.add("价格风险变动上升");	//28
		titles.add("价格风险变动下降");	//29
		titles.add("其他价格风险");	//30
		titles.add("其他价格风险敞口");	//31
		titles.add("他价格风险的敏感性分析"); //32
		titles.add("公允价值所属层次间的重大变动");	//33
		titles.add("金融工具风险及管理"); //34
		titles.add("风险管理政策和组织架构"); //34
		titles.add("信用风险");	//35
		titles.add("启用");	//36
		dataMap.put("titles", titles);
		List<PageData> varOList = reportcontentService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FUND_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("FS_ATTR1"));	    //2
			vpd.put("var3", varOList.get(i).getString("FS_ATTR2"));	    //3
			vpd.put("var4", varOList.get(i).getString("FS_ATTR3"));	    //4
			vpd.put("var5", varOList.get(i).getString("COMISSION_NOTE"));	    //5
			vpd.put("var6", varOList.get(i).getString("MF_ATTR1"));	    //6
			vpd.put("var7", varOList.get(i).getString("MF_ATTR2"));	    //7
			vpd.put("var8", varOList.get(i).getString("MF_ATTR3"));	    //8
			vpd.put("var9", varOList.get(i).getString("MF_ATTR4"));	    //9
			vpd.put("var10", varOList.get(i).getString("MF_ATTR5"));	    //10
			vpd.put("var11", varOList.get(i).getString("CF_ATTR1"));	    //11
			vpd.put("var12", varOList.get(i).getString("CF_ATTR2"));	    //12
			vpd.put("var13", varOList.get(i).getString("CF_ATTR3"));	    //13
			vpd.put("var14", varOList.get(i).getString("CF_ATTR4"));	    //14
			vpd.put("var15", varOList.get(i).getString("CF_ATTR5"));	    //15
			vpd.put("var16", varOList.get(i).getString("SF_ATTR1"));       //16
			vpd.put("var17", varOList.get(i).getString("SF_ATTR2"));       //17
			vpd.put("var18", varOList.get(i).getString("SF_ATTR3"));       //18
			vpd.put("var19", varOList.get(i).getString("SF_ATTR4"));      //19
			vpd.put("var20", varOList.get(i).getString("SF_ATTR5"));     //20
			vpd.put("var21", varOList.get(i).getString("MR"));   //21
			vpd.put("var22", varOList.get(i).getString("IR"));  //22
			vpd.put("var23", varOList.get(i).getString("IR_EXP"));   //23
			vpd.put("var24", varOList.get(i).getString("IR_ATTR1"));	    //24
			vpd.put("var25", varOList.get(i).getString("ER"));   //25
			vpd.put("var26", varOList.get(i).getString("IR_UP"));	    //26
			vpd.put("var27", varOList.get(i).getString("IR_DOWN"));	    //27
			vpd.put("var28", varOList.get(i).getString("PR_UP"));	    //28
			vpd.put("var29", varOList.get(i).getString("PR_DOWN"));	    //29
			vpd.put("var30", varOList.get(i).getString("PR_ATTR1"));	    //30
			vpd.put("var31", varOList.get(i).getString("PR_ATTR2"));	    //31
			vpd.put("var33", varOList.get(i).getString("PR_ATTR3"));	    //32
			vpd.put("var34", varOList.get(i).getString("FV_ATTR1"));	    //33
			vpd.put("var35", varOList.get(i).getString("FV_ATTR2"));	    //35
			vpd.put("var36", varOList.get(i).getString("FI"));
			vpd.put("var37", varOList.get(i).getString("RM"));	    //34
			vpd.put("var38", varOList.get(i).getString("CR"));	    //34
			vpd.put("var39", varOList.get(i).getString("CR_LOSS"));	    //34
			vpd.put("var40", varOList.get(i).getString("ACTIVE"));	    //34
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
