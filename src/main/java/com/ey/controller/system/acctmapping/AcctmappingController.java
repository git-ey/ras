package com.ey.controller.system.acctmapping;

import java.io.File;
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
import com.ey.entity.system.ImportConfig;
import com.ey.service.system.acctmapping.AcctmappingManager;
import com.ey.service.system.loger.LogerManager;
import com.ey.util.AppUtil;
import com.ey.util.Const;
import com.ey.util.FileDownload;
import com.ey.util.FileUpload;
import com.ey.util.Jurisdiction;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.UuidUtil;
import com.ey.util.fileimport.FileImportException;
import com.ey.util.fileimport.FileImportExecutor;
import com.ey.util.fileimport.ImportConfigParser;
import com.ey.util.fileimport.MapResult;
import com.ey.util.fileimport.XlsToCsv;
import com.ey.util.fileimport.XlsxToCsv;

/** 
 * 说明：科目映射
 * 创建人：andychen
 * 创建时间：2017-08-09
 */
@Controller
@RequestMapping(value="/acctmapping")
public class AcctmappingController extends BaseController {
	
	String menuUrl = "acctmapping/list.do"; //菜单地址(权限用)
	@Resource(name="acctmappingService")
	private AcctmappingManager acctmappingService;
	// 数据导入配置服务
	@Resource(name="importConfigParser")
	private ImportConfigParser importConfigParser;
	@Resource(name="logService")
	private LogerManager logManager;
	
	/**
	 * 导入设置代码
	 */
	private final static String IMPORT_FILE_CODE = "AM";
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Acctmapping");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ACCTMAPPING_ID", this.get32UUID());	//主键
		acctmappingService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除Acctmapping");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		acctmappingService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改Acctmapping");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		acctmappingService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表Acctmapping");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = acctmappingService.list(page);	//列出Acctmapping列表
		mv.setViewName("system/acctmapping/acctmapping_list");
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
		mv.setViewName("system/acctmapping/acctmapping_edit");
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
		pd = acctmappingService.findById(pd);	//根据ID读取
		mv.setViewName("system/acctmapping/acctmapping_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除Acctmapping");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			acctmappingService.deleteAll(ArrayDATA_IDS);
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
		mv.setViewName("system/acctmapping/uploadexcel");
		return mv;
	}
	
	/**下载模版
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/downExcel")
	public void downExcel(HttpServletResponse response) throws Exception{
		FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Acct_Mapping.xlsx", "Acct_Mapping.xlsx");
	}
	
	/**从EXCEL导入到数据库
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/readExcel")
	public ModelAndView readExcel(
			@RequestParam(value="excel",required=false) MultipartFile file
			) throws Exception{
		logManager.save(Jurisdiction.getUsername(), "从EXCEL导入到数据库");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;}
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getClasspath() + Const.FILEPATHFILE;
			String fileNameStr = UuidUtil.get32UUID();
			String fileName =  FileUpload.fileUp(file, filePath, fileNameStr);	
			// 获取数据导入配置
			ImportConfig configuration = null;
			try {
				configuration = importConfigParser.getConfig(IMPORT_FILE_CODE);
			} catch (Exception e) {
				throw new Exception("获取数据导入配置失败:" + IMPORT_FILE_CODE + "," + e.getMessage());
			}
			String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
			String excelFilePath = filePath + fileName;
			String csvFilePath = filePath + fileNameStr + ".csv";
			switch (extension) {
			case "xls":
				// 先执行Excel转换成CSV
				XlsToCsv xlsTocsv = new XlsToCsv(excelFilePath, csvFilePath);
				xlsTocsv.process();
				break;
			case "xlsx":
				// 先执行Excel转换成CSV
				XlsxToCsv xlsxTocsv = new XlsxToCsv(excelFilePath, csvFilePath);
				xlsxTocsv.process();
				break;
			case "csv":
				csvFilePath = excelFilePath;
				break;
			}
			MapResult mapResult = null;
			File csvFile = new File(csvFilePath);
			if(!csvFile.exists()){
				throw new Exception(excelFilePath+" 未生成临时的CSV文件");
			}
			try {
				mapResult = (MapResult) FileImportExecutor.importFile(configuration, csvFile, csvFile.getName());
			} catch (FileImportException e) {
				throw new Exception("导入CSV文件数据失败:" + csvFile.getName() + "," + e.getMessage());
			}
			/*存入数据库操作======================================*/
			List<Map> maps = mapResult.getResult();
			for (Map<String, Object> map : maps) {
				pd.put("ACCTMAPPING_ID", UuidUtil.get32UUID());
				pd.put("FUND_ID", map.get("FUND_ID"));
				pd.put("ACCOUNT_NUM", map.get("ACCOUNT_NUM"));
				pd.put("ACCOUNT_DESCRIPTION", map.get("ACCOUNT_DESCRIPTION"));
				pd.put("LEVEL", map.get("LEVEL"));
				pd.put("CURRENCY", map.get("CURRENCY"));
				pd.put("TYPE", map.get("TYPE"));
				pd.put("ENTERABLE", map.get("ENTERABLE"));
				pd.put("EY_ACCOUNT_NUM", map.get("EY_ACCOUNT_NUM"));
				pd.put("ATTR1", map.get("ATTR1"));
				pd.put("ATTR2", map.get("ATTR2"));
				pd.put("ATTR3", map.get("ATTR3"));
				pd.put("ATTR4", map.get("ATTR4"));
				pd.put("ATTR5", map.get("ATTR5"));
				pd.put("ATTR6", map.get("ATTR6"));
				pd.put("ACTIVE", map.get("ACTIVE"));
				pd.put("STATUS", "");
				acctmappingService.save(pd);
			}
			mv.addObject("msg","success");
		}
		mv.setViewName("save_result");
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
