package com.ey.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.codehaus.jackson.map.util.JSONPObject;

/**
 * 接口参数校验
 */
public class AppUtil  {

	protected static Logger logger = Logger.getLogger(AppUtil.class);

	/**
	 * 批量导入的默认条数
	 */
	public static int BATCH_INSERT_COUNT = 500;

	/**
	 * 全局CVS文件分隔符
	 */
	public static String CSV_DELIMITER = "♈";

	/**检查参数是否完整
	 * @param method
	 * @param pd
	 * @return
	 */
	public static boolean checkParam(String method, PageData pd){
		boolean result = false;

		int falseCount = 0;
		String[] paramArray = new String[20];
		String[] valueArray = new String[20];
		String[] tempArray  = new String[20];  //临时数组

		if("registerSysUser".equals(method)){// 注册
			paramArray = Const.SYSUSER_REGISTERED_PARAM_ARRAY;  //参数
			valueArray = Const.SYSUSER_REGISTERED_VALUE_ARRAY;  //参数名称

		}else if("getAppuserByUsernmae".equals(method)){//根据用户名获取会员信息
			paramArray = Const.APP_GETAPPUSER_PARAM_ARRAY;
			valueArray = Const.APP_GETAPPUSER_VALUE_ARRAY;
		}
		int size = paramArray.length;
		for(int i=0;i<size;i++){
			String param = paramArray[i];
			if(!pd.containsKey(param)){
				tempArray[falseCount] = valueArray[i]+"--"+param;
				falseCount += 1;
			}
		}
		if(falseCount>0){
			logger.error(method+"接口，请求协议中缺少 "+falseCount+"个 参数");
			for(int j=1;j<=falseCount;j++){
				logger.error("   第"+j+"个："+ tempArray[j-1]);
			}
		} else {
			result = true;
		}

		return result;
	}

	/**
	 * 设置分页的参数
	 * @param pd
	 * @return
	 */
	public static PageData setPageParam(PageData pd){
		String page_now_str = pd.get("page_now").toString();
		int pageNowInt = Integer.parseInt(page_now_str)-1;
		String page_size_str = pd.get("page_size").toString(); //每页显示记录数
		int pageSizeInt = Integer.parseInt(page_size_str);
		String page_now = pageNowInt+"";
		String page_start = (pageNowInt*pageSizeInt)+"";
		pd.put("page_now", page_now);
		pd.put("page_start", page_start);
		return pd;
	}

	/**
	 * @param pd
	 * @param map
	 * @return
	 */
	public static Object returnObject(PageData pd, Map<?, ?> map){
		if(pd.containsKey("callback")){
			String callback = pd.get("callback").toString();
			return new JSONPObject(callback, map);
		}else{
			return map;
		}
	}

	/**
	 * 过滤特殊字符
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]\\\"<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

}
