package selfservicecopier;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestStringReplacement {
	@Test
	public void tryStringReplacement(){
		FolderCopier fc = new FolderCopier("C:/test", "C:/test2"); //sample file.
		
		String[] commentTest = new String[]{"This is a menu <!--long comment-->","This is a menu \n"};
		String[] spaceTest = new String[]{"This is a string with  double\t\t unwanted spaces.","This is a string with double unwanted spaces.\n"};
		String[] newLine = new String[]{"This is a new line \r\n","This is a new line \n"};
		String[] urlReplacement = new String[]{"src=\"http://localhost:8080/selfserviceweb\"","src=\"http://service.jomjaring.com\"\n"};
		
		List<String[]> testStrings = Arrays.asList(commentTest, spaceTest, newLine, urlReplacement); 
		
		for(String[] testString: testStrings){
			String output = fc.doStringReplacement(testString[0]);
			Assert.assertEquals(testString[1], output);
		}
		
	}
}
