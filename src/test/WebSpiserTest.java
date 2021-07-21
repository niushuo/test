package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 获得URL对应的源码，找出找链接地址
 * @author 牛硕
 *@adte 2018.9.16
 */
//对网易主页的源码进爬虫
public class WebSpiserTest {
	public  static String getURLContent(String urlStr,String charset) throws IOException{
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(urlStr);
			//打开网页流，转换编码为“gbk”
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),Charset.forName(charset)));
			String temp = null;

			while((temp = reader.readLine()) != null){
				sb.append(temp);
			}

		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return sb.toString();

	}
	/**
	 * 将需要的网站内容通过正则匹配出来
	 * @param destStr
	 * @param regexStr
	 * @return
	 */
	public static List<String> getMatcherSubstrs(String destStr,String regexStr){

		//Pattern p =  Pattern.compile("<a[\\s\\S]+?</a>");//创建正则对象,取到整个超链接的内容
		Pattern p =  Pattern.compile("href=\"(.+?)\"");//创建正则对象,取到超链接的地址
		Matcher m = p.matcher(destStr);
		List<String> result = new ArrayList<String>();

		while(m.find()){
			System.out.println(m.group(1));

		}
		return result;
	}
	/**
	 * 读入网站地址
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String destStr = getURLContent("http://163.com","gbk");//获得网站对应的地址

		List<String> result = getMatcherSubstrs(destStr, "href=\"([\\w\\s./:]+?)\"");

		for(String temp: result) {
			System.out.println(temp);
		}
	}
}
