package com.ey.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

/**
 * doc文档处理工具类
 * @author andychen 2017-12-11
 *
 */
public class DocUtil {

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

		XWPFDocument xwpfDocument = new XWPFDocument(opcPackage);

		CTBody ctBody = xwpfDocument.getDocument().getBody();

		bodyXmlStr = ctBody.xmlText();

		return bodyXmlStr;
	}
	
	public static void main(String[] args) {
		
		try {
			String str = DocUtil.getDocBody("D:\\EY_Report_Template\\P2_FSO_DEF.docx");
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
