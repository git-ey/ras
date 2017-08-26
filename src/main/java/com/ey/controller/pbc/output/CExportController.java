package com.ey.controller.pbc.output;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ey.controller.base.BaseController;
import com.ey.service.pbc.output.CExportManager;

/** 
 * 说明： 底稿输出测试Controller
 * 创建人：Dai Zong
 * 创建时间：2017-08-25
 */
@Controller
@RequestMapping(value="/export")
public class CExportController extends BaseController {
	@Resource(name="cExportService")
    private CExportManager cExportService;
	
    	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
	
	 /**导出到excel
     * @param
     * @throws Exception
     */
    @RequestMapping(value="/c",method=RequestMethod.GET, produces="application/x-www-form-urlencoded;charset=UTF-8")
    public void output(HttpServletRequest request, HttpServletResponse response){
        try {
        	// 基金ID
        	String fundId =  request.getParameter("FUND_ID");
        	// 期间
        	String peroid =  request.getParameter("PEROID");
            this.cExportService.doExport(request, response, fundId, Long.parseLong(peroid));
        } catch (Exception e) {
            this.logger.error("", e);
        }
    }
}
