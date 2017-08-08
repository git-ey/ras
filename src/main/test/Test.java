import java.io.File;

/**
 * 用于小测试
 * @author andyChen
 *
 */
public class Test {
	
	public static void main(String[] args) {
		File file = new File("D:\\importdata\\019090261d1d49aaa1ffbfb47d865528.csv");
		file.deleteOnExit();
	}

}
