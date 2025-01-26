package com.ey.controller.system.concruning;

import java.util.Date;
import java.util.concurrent.Callable;

import com.ey.service.system.concruning.ConcRuningManager;
import com.ey.util.PageData;
import com.ey.util.StringUtil;
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
		try {
			pd.put("RESULT", "R"); // 运行状态
			concruningService.edit(pd);
			concruningService.runProcedure(pd);
		} catch (Exception ex) {
			pd.put("RESULT", "E");
			pd.put("MESSAGE", StringUtil.getStringByLength(ex.getMessage(),240));
		}
		// 更新状态
		pd.put("END_DATETIME", Tools.date2Str(new Date()));
		concruningService.edit(pd);
		return Boolean.TRUE;
	}

}
