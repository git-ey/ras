package com.ey.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

/**
 * VBS工具类
 * 
 * @author logsin37
 *
 */
public class VbsUtil {
    
    private static final Logger logger = Logger.getLogger(VbsUtil.class);
    
    /**
     * vbs资源文件夹路径
     */
    public static final String VBS_RESOURCES_FOLDER_PATH = "vbs/";
    
    /**
     * cmd调用命令前缀
     */
    private static final String CMD_COMMAND_PREIFX = "cmd /C ";
    
    /**
     * 已知的脚本
     * 
     * @author logsin37
     *
     */
    public enum Scripts{
        
        /**
         * 底稿转xlsx<br/>
         * 报告转docx
         */
        WORKPAPER_AND_REPORT_CONVERTER("workpaperAndReportConverter.vbs");
        
        private Scripts(String scriptFileName) {
            this.scriptFileName = scriptFileName;
        }
        
        private String scriptFileName;
        
        public String getScriptFileName() {
            return scriptFileName;
        }
        
    }
    
    /**
     * 运行VBS(同步)
     * 
     * @param script 已知的脚本
     * @param args 参数
     * @throws IOException 
     */
    public static void callScript(Scripts script, String... args) throws IOException {
        if(script == null) {
            return;
        }
        String scriptFilePath = VbsUtil.class.getClassLoader().getResource(VBS_RESOURCES_FOLDER_PATH + script.getScriptFileName()).getPath();
        if(scriptFilePath.charAt(0) == '/' || scriptFilePath.charAt(0) == '\\') {
            scriptFilePath = scriptFilePath.substring(1);
        }
        StringBuilder commandBuilder =  new StringBuilder(CMD_COMMAND_PREIFX).append(scriptFilePath).append(StringUtils.SPACE);
        if(args != null && args.length > 0) {
            for(int i=0 ; i<args.length ; i++) {
                commandBuilder.append(args[i]).append(StringUtils.SPACE);
            }
        }
        String command = commandBuilder.toString();
        logger.debug("call vbs start : " + command);
        Runtime.getRuntime().exec(command);
        logger.debug("call vbs end : " + command);
    }
    
    /**
     * 运行VBS(异步)
     * 
     * @param script 已知的脚本
     * @param args 参数
     */
    public static void asyncCallScript(Scripts script, String... args) {
        new Thread(() -> {
            try {
                callScript(script, args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
