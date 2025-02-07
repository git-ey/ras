package com.ey.util.fileexport;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @name InterfaceFreeMarkerUtil
 * @description 接口专用FreeMarker工具
 * @author gaokuo.dai@hand-china.com	2017年5月15日下午2:35:23
 * @version 1.0
 */
public class FreeMarkerUtils {
	private static Configuration freeMarkerConfig = null;
	
	private static int DEF_STR_WRITER_BUF_SIZE = 1024;
	
	private FreeMarkerUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
	
	private static void initConfiguration(){
	    // lazy load with double check lock
		if(freeMarkerConfig == null){
		    synchronized (Configuration.class) {
		        if(freeMarkerConfig == null){
		            freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_21);
		            freeMarkerConfig.setDefaultEncoding(StandardCharsets.UTF_8.name());
		        }
            }
		}
	}
	
	/**
	 * 通过文件名向指定文件的FreeMarker模板绑定数据，将渲染的结果以String输出
	 * @author gaokuo.dai@hand-china.com
	 * 2017年5月15日
	 * 
	 * @param templateData 要绑定进模板的数据，可空
	 * @param templateFolderPath 模板所在的文件夹【以classpath:为根，例如:"/SAP"】
	 * @param templateFileName 模板的文件名全称【例如:"test.ftl"】
	 * @return FreeMarker渲染的结果,以String输出
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws TemplateException
	 */
	public static String processTemplateToString(Map<String, Object> templateData, String templateFolderPath, String templateFileName) throws IOException, URISyntaxException, TemplateException{
		//参数校验
		if(templateData == null){
			templateData = new HashMap<String, Object>();
		}
		if(StringUtils.isEmpty(templateFolderPath) || StringUtils.isEmpty(templateFileName)){
			throw new IllegalArgumentException("Template Folder Path or Template File Name is EMPTY!");
		}
		if('/' != templateFolderPath.charAt(0)){
			templateFolderPath = '/' + templateFolderPath;
		}
		//模板加载
		FreeMarkerUtils.initConfiguration();
		FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(freeMarkerConfig.getClass().getClassLoader().getResource(templateFolderPath).toURI()));
		freeMarkerConfig.setTemplateLoader(fileTemplateLoader);
		//渲染
		StringWriter writer = new StringWriter(DEF_STR_WRITER_BUF_SIZE);
		Template template = freeMarkerConfig.getTemplate(templateFileName, StandardCharsets.UTF_8.name());
		template.process(templateData, writer);
		return writer.toString();
	}
	
	/**
	 * 基于${KEY}的字符串简单替换
	 * @author Dai Zong 2017年11月8日
	 * 
	 * @param source 模板
	 * @param dataMap 数据源
	 * @return 替换后的模板
	 */
	public static String simpleReplace(String source, Map<String, String> dataMap) {
	    if(source == null || dataMap == null) {
	        return null;
	    }
	    for(Entry<String,String> entry : dataMap.entrySet()) {
	        source = source.replace("${" + entry.getKey() + "}", entry.getValue());
	    }
	    return source;
	}
	
	/**
     * 通过文件名向指定文件的FreeMarker模板绑定数据，将渲染的结果以String输出
     * <p>路径为模板所在文件夹绝对路径</p>
     * @author gaokuo.dai@hand-china.com
     * 2017年12月11日
     * 
     * @param templateData 要绑定进模板的数据，可空
     * @param templateFolderPath 模板所在文件夹绝对路径
     * @param templateFileName 模板的文件名全称【例如:"test.ftl"】
     * @return FreeMarker渲染的结果,以String输出
     * @throws IOException
     * @throws TemplateException
     */
    public static String processTemplateToStrUseAbsPath(Map<String, Object> templateData, String templateFolderPath, String templateFileName) throws IOException, TemplateException{
        //参数校验
        if(templateData == null){
            templateData = new HashMap<>();
        }
        if(StringUtils.isEmpty(templateFolderPath) || StringUtils.isEmpty(templateFileName)){
            throw new IllegalArgumentException("Template Folder Path or Template File Name is EMPTY!");
        }
        if('/' != templateFolderPath.charAt(0)){
            templateFolderPath = '/' + templateFolderPath;
        }
        //模板加载
        FreeMarkerUtils.initConfiguration();
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(templateFolderPath));
        freeMarkerConfig.setTemplateLoader(fileTemplateLoader);
        //渲染
        StringWriter writer = new StringWriter(DEF_STR_WRITER_BUF_SIZE);
        Template template = freeMarkerConfig.getTemplate(templateFileName);
        template.process(templateData, writer);
        return writer.toString();
    }
	
}