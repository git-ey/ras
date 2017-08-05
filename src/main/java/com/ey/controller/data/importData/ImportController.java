package com.ey.controller.data.importData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
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
import com.ey.service.data.importData.ImportManager;
import com.ey.service.data.importData.impl.ImportDataWroker;
import com.ey.service.system.importconfig.ImportConfigManager;
import com.ey.service.system.loger.LogerManager;
import com.ey.util.AppUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.PageData;
import com.ey.util.Tools;
import com.ey.util.excel.ImportConfigParser;

/** 
 * 说明：导入工作台
 * 创建人：andychen
 * 创建时间：2017-08-04
 */
@Controller
@RequestMapping(value="/importData")
public class ImportController extends BaseController {
	
	String menuUrl = "importData/list.do"; //菜单地址(权限用)
	@Resource(name="importService")
	private ImportManager importService;
	// 数据导入配置解析器
	@Resource(name="importConfigParser")
	private ImportConfigParser importConfigParser;
	// 数据导入配置服务
	@Resource(name="importConfigService")
	private ImportConfigManager importConfigService;
	@Autowired
	@Qualifier("taskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;
	@Resource(name = "logService")
	private LogerManager logManager;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Import");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("IMPORT_ID", this.get32UUID());	//主键
		pd.put("START_DATETIME", Tools.date2Str(new Date()));	//开始时间
		pd.put("OPERATOR_ID", Jurisdiction.getUserId());	//操作人ID
		pd.put("OPERATOR_NAME", Jurisdiction.getUsername());	//操作人
		pd.put("IMPORT_STATUS", "R");	//导入状态--导入中
		// 执行处理导入的线程
		if(StringUtils.isNotBlank(pd.getString("IMPORT_FILE_TYPE")) && StringUtils.isNotBlank(pd.getString("IMPORT_FILE_PATH"))){
			taskExecutor.submit(new ImportDataWroker(importConfigParser,importConfigService,importService,pd));
			importService.save(pd);
		}
		logManager.save(Jurisdiction.getUsername(), "发起了数据导入");
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
		logBefore(logger, Jurisdiction.getUsername()+"列表Import");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = importService.list(page);	//列出Import列表
		mv.setViewName("data/importData/import_list");
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
		mv.setViewName("data/importData/import_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	/**去查看导入文件页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/listImportFile")
	public ModelAndView listImportFile(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Import File");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = importService.listImportFile(page);	//列出Import列表
		mv.setViewName("data/importData/import_file");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	
	@RequestMapping(value="/deleteImportFile")
	@ResponseBody
	public Object deleteImportFile() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除 Import File");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null ;} //校验权限
		Map<String,String> map = new HashMap<String,String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errInfo = "success";
		importService.deleteImportFile(pd);
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
