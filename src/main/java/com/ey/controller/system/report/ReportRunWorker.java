package com.ey.controller.system.report;

import java.util.concurrent.Callable;

import com.ey.service.system.report.ReportManager;
import com.ey.util.PageData;
import com.ey.util.StringUtil;

/**
 * 说明：报告进程类 创建人：andychen 创建时间：2017-12-05
 */
public class ReportRunWorker implements Callable<Boolean> {
	
	private ReportManager reportService;
	private PageData pd;
	
	public ReportRunWorker(ReportManager reportService, PageData pd){
		this.reportService = reportService;
		this.pd = pd;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			reportService.exportReport(pd);
		} catch (Exception ex) {
			pd.put("RESULT", "E");
			pd.put("MESSAGE", StringUtil.getStringByLength(ex.getMessage(),240));
		}
		reportService.edit(pd);
		return Boolean.TRUE;
	}

}
