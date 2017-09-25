package com.ey.service.wp.output;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @name BaseExportManager
 * @description 基础底稿导出服务类 
 * @author Dai Zong	2017年9月6日
 */
public interface BaseExportManager{
    
    /**
     * 导出底稿
     * @author Dai Zong 2017年8月26日
     * 
     * @param request HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param fundId 基金ID
     * @param peroid 期间[例:2017]
     * @return
     * @throws Exception
     */
    boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long period) throws Exception;
}

