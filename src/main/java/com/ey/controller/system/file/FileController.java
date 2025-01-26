package com.ey.controller.system.file;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ey.controller.base.BaseController;
import com.ey.entity.Page;
import com.ey.service.system.config.ConfigManager;
import com.ey.service.system.file.FileManager;
import com.ey.util.AppUtil;
import com.ey.util.DelAllFile;
import com.ey.util.FileDownload;
import com.ey.util.FileUtil;
import com.ey.util.Jurisdiction;
import com.ey.util.PageData;
import com.ey.util.PathUtil;
import com.ey.util.Tools;

/**
 * 说明：文件上传管理
 */
@Controller
@RequestMapping(value = "/file")
public class FileController extends BaseController {

	String menuUrl = "file/list.do"; // 菜单地址(权限用)
	@Resource(name = "fileService")
	private FileManager fileService;
	@Resource(name = "configService")
	private ConfigManager configService;

	public static final String DATA_UPLOAD_PATH = "DATA_UPLOAD_PATH";

	String uploadPath;

	FileController() {
		try {
			uploadPath = configService.findByCode(DATA_UPLOAD_PATH);
		} catch (Exception e) {
			uploadPath = PathUtil.getClasspath();
		}
	}

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增file");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("FILE_ID", this.get32UUID()); // 主键
		pd.put("CTIME", Tools.date2Str(new Date())); // 上传时间
		pd.put("USERNAME", Jurisdiction.getUsername()); // 上传者
		pd.put("DEPARTMENT_ID", Jurisdiction.getDEPARTMENT_ID()); // 部门ID
		pd.put("FILESIZE", FileUtil.getFilesize(uploadPath + pd.getString("FILEPATH"))); // 文件大小
		fileService.save(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "删除Fhfile");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = fileService.findById(pd);
		fileService.delete(pd);
		DelAllFile.delFolder(uploadPath + pd.getString("FILEPATH")); // 删除文件
		out.write("success");
		out.close();
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表file");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		String item = Jurisdiction.getDEPARTMENT_IDS();
		if ("0".equals(item) || "无权".equals(item)) {
			pd.put("item", ""); // 根据部门ID过滤
		} else {
			pd.put("item", item.replaceFirst("\\(", "\\('" + Jurisdiction.getDEPARTMENT_ID() + "',"));
		}
		page.setPd(pd);
		List<PageData> varList = fileService.list(page); // 列出file列表
		mv.setViewName("system/file/file_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 去新增页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/file/file_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 批量删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "批量删除Fhfile");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return null;
		} // 校验权限
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (null != DATA_IDS && !"".equals(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			PageData fpd = new PageData();
			for (int i = 0; i < ArrayDATA_IDS.length; i++) {
				fpd.put("FILE_ID", ArrayDATA_IDS[i]);
				fpd = fileService.findById(fpd);
				DelAllFile.delFolder(uploadPath + fpd.getString("FILEPATH")); // 删除物理文件
			}
			fileService.deleteAll(ArrayDATA_IDS); // 删除数据库记录
			pd.put("msg", "ok");
		} else {
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}

	/**
	 * 下载
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/download")
	public void downExcel(HttpServletResponse response) throws Exception {
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = fileService.findById(pd);
		String fileName = pd.getString("FILEPATH");
		FileDownload.fileDownload(response, uploadPath + fileName,
				pd.getString("NAME") + fileName.substring(19, fileName.length()));
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}
}
