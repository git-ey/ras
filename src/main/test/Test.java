import org.apache.commons.lang3.StringUtils;

/**
 * 用于小测试
 * @author andyChen
 *
 */
public class Test {
	
	public static void main(String[] args) {
		for(String str : "1,2,3,4,5".split("[^\\D]")){
			if(StringUtils.isNotBlank(str)){
				System.out.println(str);
				break;
			}
		}
		int idx = 1;
		idx++;
		System.out.println(idx);
	}

}
