package com.ey.controller.system.concruning;

import java.util.Date;
import java.util.concurrent.Callable;

import com.ey.service.system.concruning.ConcRuningManager;
import com.ey.util.PageData;
import com.ey.util.Tools;

public class ConcRunWorker implements Callable<Boolean> {
	
	private ConcRuningManager concruningService;
	private PageData pd;
	
	public ConcRunWorker(ConcRuningManager concruningService, PageData pd){
		this.concruningService = concruningService;
		this.pd = pd;
	}

	@Override
	public Boolean call() throws Exception {
		concruningService.runProcedure(pd);
		// 更新状态
		pd.put("END_DATETIME", Tools.date2Str(new Date()));
		concruningService.edit(pd);
		return Boolean.TRUE;
	}

}
