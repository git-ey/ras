package com.ey.util.fileexport;

/**
 * 文件导出常量
 * @author Dai Zong
 */
public interface Constants {
    /**
     * 导出模板位置
     */
//    static final String EXPORT_TEMPLATE_FOLDER_PATH = "/export_template";


    public enum ExportPathEnum {
        BJ("/export_template/BJ"),
        SZ("/export_template/SH"),
        DEFAULT("/export_template");

        private final String path;

        ExportPathEnum(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public static String getExportPath(String wpPathC) {
            if ("BJ".equals(wpPathC)) {
                return ExportPathEnum.BJ.getPath();
            } else if ("SZ".equals(wpPathC)) {
                return ExportPathEnum.SZ.getPath();
            } else {
                return ExportPathEnum.DEFAULT.getPath();
            }
        }
    }


    /**
     * 报告组件位置
     */
//    static final String REPORT_TEMPLATES_FOLDER_PATH = EXPORT_TEMPLATE_FOLDER_PATH + "/report_parts";
    /**
     * 模板名称--Report
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_REPORT = "Report.ftl";
    /**
     * 底稿名称--Report
     */
    // 20200507，yury，新增报告类型到报告名称中
    static final String EXPORT_AIM_FILE_NAME_REPORT = "${companyShortName}_${fundId}_${fundShortName}_${repType}_${periodStr}.doc";
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
    /**
     * 模板名称--V
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_V = "V.ftl";
    /**
     * 底稿名称--V
     */
    static final String EXPORT_AIM_FILE_NAME_V = "${companyShortName}_V_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--T
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_T = "T.ftl";
    /**
     * 底稿名称--T
     */
    static final String EXPORT_AIM_FILE_NAME_T = "${companyShortName}_T_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--H_SUM
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_H_SUM = "H_SUM.ftl";
    /**
     * 底稿名称--H_SUM
     */
    static final String EXPORT_AIM_FILE_NAME_H_SUM = "${shortName}_H_旗下基金总表_${periodStr}.xml";
    /**
     * 模板名称--H
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_H = "H.ftl";
    /**
     * 底稿名称--H
     */
    static final String EXPORT_AIM_FILE_NAME_H = "${companyShortName}_H_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--I
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_I = "I.ftl";
    /**
     * 底稿名称--I
     */
    static final String EXPORT_AIM_FILE_NAME_I = "${companyShortName}_I_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--O
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_O = "O.ftl";
    /**
     * 底稿名称--O
     */
    static final String EXPORT_AIM_FILE_NAME_O = "${companyShortName}_O_${fundId}_${fundShortName}_${periodStr}.xml";
    /**
     * 模板名称--SA
     */
    static final String EXPORT_TEMPLATE_FILE_NAME_SA = "SA.ftl";
    /**
     * 底稿名称--SA
     */
    static final String EXPORT_AIM_FILE_NAME_SA = "${shortName}_SiginificantAccount_${periodStr}.xml";
}
