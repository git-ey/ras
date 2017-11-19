package com.ey.util.fileexport;

/**
 * 文件导出常量
 * @author Dai Zong
 */
public interface Constants {
    /**
     * 导出模板位置
     */
    static final String EXPORT_TEMPLATE_FOLDER_PATH = "/export_template";
    /**
     * 模板名称--Report
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_REPORT = "Report.ftl";
    /**
     * 底稿名称--Report
     */
    static final String EXPORT_AIM_FILE_NAME_REPORT = "${companyShortName}_${fundId}_${fundShortName}_年审报告_${periodStr}.doc";
    /**
     * 模板名称--C
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_C = "C.ftl";
    /**
     * 底稿名称--C
     */
    static final String EXPORT_AIM_FILE_NAME_C = "${companyShortName}_C_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--G
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_G = "G.ftl"; 
    /**
     * 底稿名称--G
     */
    static final String EXPORT_AIM_FILE_NAME_G = "${companyShortName}_G_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--N
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_N = "N.ftl"; 
    /**
     * 底稿名称--N
     */
    static final String EXPORT_AIM_FILE_NAME_N = "${companyShortName}_N_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--P
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_P = "P.ftl"; 
    /**
     * 底稿名称--P
     */
    static final String EXPORT_AIM_FILE_NAME_P = "${companyShortName}_P_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--E
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_E = "E.ftl"; 
    /**
     * 底稿名称--E
     */
    static final String EXPORT_AIM_FILE_NAME_E = "${companyShortName}_E_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--U
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_U = "U.ftl"; 
    /**
     * 底稿名称--U
     */
    static final String EXPORT_AIM_FILE_NAME_U = "${companyShortName}_U_${fundId}_${fundShortName}_${periodStr}.xml";
}
