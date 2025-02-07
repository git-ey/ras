package com.ey.service.wp.output;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @name HSumExportManager
 * @description 底稿H汇总导出服务类
 * @author Dai Zong	2017年12月6日
 */
public interface HSumExportManager extends BaseExportManager{

    /**
     * 导出底稿到HttpServletResponse
     * @author Dai Zong 2017年12月6日
     *
     * @param firmCode 公司代码
     * @param periodStr 期间[例:"20172131"]
     * @param request HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @return
     * @throws Exception
     */
    boolean doExport(String firmCode, String periodStr, HttpServletRequest request, HttpServletResponse response, String templatePath) throws Exception;

    /**
     * 导出底稿到磁盘
     * @author Dai Zong 2017年12月6日
     *
     * @param folederName 文件夹名(有效路径)
     * @param fileName 文件名
     * @param firmCode 公司代码
     * @param peroidStr 期间[例:"20172131"]
     * @return
     * @throws Exception
     */
    boolean doExport(String folederName, Object fileName, String firmCode, String periodStr, String templatePath) throws Exception;

}
