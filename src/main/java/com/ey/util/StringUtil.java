package com.ey.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串相关方法
 *
 */
public class StringUtil {

	/**
	 * 将以逗号分隔的字符串转换成字符串数组
	 * @param valStr
	 * @return String[]
	 */
	public static String[] StrList(String valStr){
	    int i = 0;
	    String TempStr = valStr;
	    String[] returnStr = new String[valStr.length() + 1 - TempStr.replace(",", "").length()];
	    valStr = valStr + ",";
	    while (valStr.indexOf(',') > 0)
	    {
	        returnStr[i] = valStr.substring(0, valStr.indexOf(','));
	        valStr = valStr.substring(valStr.indexOf(',')+1 , valStr.length());
	        
	        i++;
	    }
	    return returnStr;
	}
	
	/**
	 * 获取指定长度的字符串
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getStringByLength(String str,int len){
		if(len < str.length()){
			return str.substring(0,len);
		}
		return str;
	}
	
	/**获取字符串编码
	 * @param str
	 * @return
	 */
	public static String getEncoding(String str) {      
	       String encode = "GB2312";      
	      try {      
	          if (str.equals(new String(str.getBytes(encode), encode))) {      
	               String s = encode;      
	              return s;      
	           }      
	       } catch (Exception exception) {      
	       }      
	       encode = "ISO-8859-1";      
	      try {      
	          if (str.equals(new String(str.getBytes(encode), encode))) {      
	               String s1 = encode;      
	              return s1;      
	           }      
	       } catch (Exception exception1) {      
	       }      
	       encode = "UTF-8";      
	      try {      
	          if (str.equals(new String(str.getBytes(encode), encode))) {      
	               String s2 = encode;      
	              return s2;      
	           }      
	       } catch (Exception exception2) {      
	       }      
	       encode = "GBK";      
	      try {      
	          if (str.equals(new String(str.getBytes(encode), encode))) {      
	               String s3 = encode;      
	              return s3;      
	           }      
	       } catch (Exception exception3) {      
	       }      
	      return "";      
	   } 
	
	/**
     * 字符串拆成2段
     * @author Dai Zong 2017年12月24日
     * 
     * @param src 源
     * @param splitor 分隔符
     * @param frontCut 在分隔符 (true 前)/ (false 后) 截断
     * @return 包含前后两段元素的String[](length一定为2)
     */
    public static String[] splitStringPair(String src,  String splitor, boolean frontCut) {
        src = (src == null ? StringUtils.EMPTY : src);
        splitor = (splitor == null ? StringUtils.SPACE : splitor);
        int splitIndex = src.indexOf(splitor);
        String A,B;
        if(frontCut) {
            if(splitIndex < 0) {
                A = StringUtils.EMPTY;
                B = src;
            }else {
                A = src.substring(0, splitIndex);
                B = src.substring(splitIndex);
            }
        }else {
            if(splitIndex <= 0) {
                A = src;
                B = StringUtils.EMPTY;
            }else {
                A = src.substring(0, splitIndex + 1);
                B = src.substring(splitIndex + 1);
            }
        }
        return new String[] {A, B};
    }

	
}
