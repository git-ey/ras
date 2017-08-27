package com.ey.service.pbc.output;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * 说明： EY余额表接口
 * 创建人：andychen
 * 创建时间：2017-08-12
 * @version
 */
public interface CExportManager{
    
    /**
     * 导出底稿C
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

