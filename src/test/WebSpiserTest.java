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
 * ���URL��Ӧ��Դ�룬�ҳ������ӵ�ַ
 * @author ţ˶
 *@adte 2018.9.16
 */
//��������ҳ��Դ�������
public class WebSpiserTest {
	public  static String getURLContent(String urlStr,String charset) throws IOException{
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(urlStr);
			//����ҳ����ת������Ϊ��gbk��
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
	 * ����Ҫ����վ����ͨ������ƥ�����
	 * @param destStr
	 * @param regexStr
	 * @return
	 */
	public static List<String> getMatcherSubstrs(String destStr,String regexStr){

		//Pattern p =  Pattern.compile("<a[\\s\\S]+?</a>");//�����������,ȡ�����������ӵ�����
		Pattern p =  Pattern.compile("href=\"(.+?)\"");//�����������,ȡ�������ӵĵ�ַ
		Matcher m = p.matcher(destStr);
		List<String> result = new ArrayList<String>();

		while(m.find()){
			System.out.println(m.group(1));

		}
		return result;
	}
	/**
	 * ������վ��ַ
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String destStr = getURLContent("http://163.com","gbk");//�����վ��Ӧ�ĵ�ַ

		List<String> result = getMatcherSubstrs(destStr, "href=\"([\\w\\s./:]+?)\"");

		for(String temp: result) {
			System.out.println(temp);
		}
	}
}
