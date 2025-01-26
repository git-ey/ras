package com.ey.controller.system.workpaper;

import java.util.concurrent.Callable;

import com.ey.service.system.workpaper.WorkPaperManager;
import com.ey.util.PageData;
import com.ey.util.StringUtil;

/**
 * 说明：报告进程类 创建人：andychen 创建时间：2017-12-05
 */
public class WorkPaperRunWorker implements Callable<Boolean> {
	
	private WorkPaperManager workPaperService;
	private PageData pd;
	
	public WorkPaperRunWorker(WorkPaperManager workPaperService, PageData pd){
		this.workPaperService = workPaperService;
		this.pd = pd;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			workPaperService.exportWorkPaper(pd);
		} catch (Exception ex) {
			pd.put("RESULT", "E");
			pd.put("MESSAGE", StringUtil.getStringByLength(ex.getMessage(),240));
		}
		workPaperService.edit(pd);
		return Boolean.TRUE;
	}

}
