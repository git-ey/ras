package com.ey.controller.wp.output;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ey.controller.base.BaseController;
import com.ey.service.wp.output.CExportManager;
import com.ey.util.PageData;

/**
 * 说明： 底稿输出Controller
 */
@Controller
@RequestMapping(value = "/wpExport")
public class ExportController extends BaseController {
	// 底稿C
	@Resource(name = "cExportService")
	private CExportManager cExportService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}

	/**
	 * 导出到excel
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/C")
	public void outputC(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PageData pd = this.getPageData();
		String fundId = (pd.getString("FUND_ID")==null?StringUtils.EMPTY:pd.getString("FUND_ID")).replace("_", "#");
		String periodStr = pd.getString("PEROID");//"2017";
		if(fundId == null || periodStr == null) {
		    throw new IllegalArgumentException("基金ID和期间不能为空");
		}
		this.cExportService.doExport(request, response, fundId, Long.parseLong(periodStr));
	}
}
