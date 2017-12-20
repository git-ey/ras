package com.ey.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

import com.ey.util.fileexport.FreeMarkerUtils;

/**
 * doc文档处理工具类
 * @author andychen 2017-12-11
 *
 */
public class DocUtil {
    
    /**
     * 默认字符集--UTF-8
     */
    private static final String DEF_CHARSET = "UTF-8";

	/**
	 * 获取指定word文件的body
	 * 
	 * @param docPath
	 * @return
	 * @throws Exception
	 */
	public static String getDocBody(String docPath) throws Exception {

		InputStream in = null;

		OPCPackage opcPackage = null;

		String bodyXmlStr = "";

		in = new FileInputStream(docPath);

		opcPackage = OPCPackage.open(in);

		try(XWPFDocument xwpfDocument = new XWPFDocument(opcPackage)){
		    
		    CTBody ctBody = xwpfDocument.getDocument().getBody();
//		    Node domNode = xwpfDocument.getDocument().getDomNode();
		    bodyXmlStr = ctBody.xmlText();
		    
		    return bodyXmlStr;
		    
		}catch (Exception e) {
            throw e;
        }

	}
	
	/**
	 * <p>从xml 2003格式的doc文档中截取需要的内容</p>
	 * <p>(通过正则表达式)</p>
	 * @author Dai Zong 2017年12月19日
	 * 
	 * @param filePath 文件路径
	 * @param pattern 正则表达式
	 * @param grouoIndex 需要返回哪个group里的数据
	 * @return 匹配成功: 返回指定group中的文本; 匹配失败: null
	 * @throws IOException 
	 */
	public static String getXml2003Content(String filePath,final String pattern, int grouoIndex) throws IOException{
	    String xml = FileUtils.readFileToString(new File(filePath), DEF_CHARSET);
        Pattern regex = Pattern.compile(pattern, Pattern.MULTILINE);
        Matcher matcher = regex.matcher(xml);
        if(matcher.find()) {
            return matcher.group(grouoIndex);
        }else {
            return null;
        }
	}
	
	public static void main(String[] args) {
		
		try {
			/*String str = getXml2003Content("D:\\EY_Report_Template\\P4_FSO_DEF.xml", "<w:body><wx:sect><wx:sub-section>(.*)</wx:sub-section>", 1);
			System.out.println(str);*/
		    Map<String,Object> a = new HashMap<>();
		    Calendar ca = Calendar.getInstance();
		    ca.set(2017, 11, 01);
		    a.put("data", null);
		    System.out.println(FreeMarkerUtils.processTemplateToStrUseAbsPath(a, "D:\\", "test.ftl"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
