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
	
	public static void main(String[] args) {
		
		String str = "";
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]|^$");
		Matcher m = p.matcher(str);
		System.out.println(m.find());
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("1", "1");
		map.put("2", 1000);
		map.put("3", null);
		
		Set<Entry<String, Object>> ms = map.entrySet();
		for(Entry<String, Object> it : ms){
			System.out.println(it.getKey()+":"+it.getValue());
		}
	}

}
