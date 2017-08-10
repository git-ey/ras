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
		
	}

}
