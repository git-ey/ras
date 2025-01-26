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
     * 导出底稿到HttpServletResponse
     * @author Dai Zong 2017年8月26日
     *
     * @param request HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param fundId 基金ID
     * @param peroidStr 期间[例:"20172131"]
     * @return
     * @throws Exception
     */
    boolean doExport(HttpServletRequest request, HttpServletResponse response, String fundId, String periodStr, String templatePath) throws Exception;

    /**
     * 导出底稿到磁盘
     * @author Dai Zong 2017年10月17日
     *
     * @param folederName 文件夹名(有效路径)
     * @param fileName 文件名
     * @param fundId 基金ID
     * @param peroidStr 期间[例:"20172131"]
     * @return
     * @throws Exception
     */
    boolean doExport(String folederName, String fileName, String fundId, String periodStr, String templatePath) throws Exception;
}

