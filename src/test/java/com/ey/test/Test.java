package com.ey.test;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于小测试
 * @author andyChen
 *
 */
public class Test {
	
	public static boolean matchStr(Object str){
		Pattern p = Pattern.compile("^.*[制  表|打  印].*");
		Matcher m = p.matcher(str.toString());
		return m.find();
	}
	
	public static void main(String[] args) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1","天风证券");
		map.put("2","上海合计：");
		map.put("3","");
		map.put("4","制  表：audit");
		map.put("5","打  印：audit");
		Set<Entry<String, Object>> ms = map.entrySet();
		for(Entry<String, Object> it : ms){
			if(Test.matchStr(it.getValue())){
				System.out.println(it.getKey()+":"+it.getValue());
			}
		}
	}

}
