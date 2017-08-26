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
    
    boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, Long peroid) throws Exception;
}

