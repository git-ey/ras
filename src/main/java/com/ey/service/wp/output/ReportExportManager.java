package com.ey.service.wp.output;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @name ReportExportManager
 * @description 底稿报告导出服务类
 * @author andy.chen	2017年9月7日
 */
public interface ReportExportManager{
    /**
     * 导出底稿到HttpServletResponse
     * @author Dai Zong 2017年8月26日
     *
     * @param request HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param exportParam 运行参数集合
     * @return
     * @throws Exception
     */
    boolean doExport(HttpServletRequest request, HttpServletResponse response, Map<String,Object> exportParam, String templatePath) throws Exception;

    /**
     * 导出底稿到磁盘
     * @author Dai Zong 2017年10月17日
     *
     * @param folederName 文件夹名(有效路径)
     * @param fileName 文件名
     * @param exportParam 运行参数集合
     * @return
     * @throws Exception
     */
    boolean doExport(String folederName, String fileName, Map<String,Object> exportParam, String templatePath) throws Exception;
}
