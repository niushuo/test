package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * ����java��������ʽ����Ļ����÷�
 * @author ţ˶
 *
 */
public class Zhengze {
public static void main(String[] args) {
	Pattern  p = Pattern.compile("[<]");//�������ʽ����ע��java����������\��Ҫ��Ϊ\\
	Matcher m = p.matcher("aa232**ssd24622<,<<");//����Matcher����
//	System.out.println(m);
//	boolean yesorno = m.matches();//���Խ��ַ��������ģʽƥ��
//	System.out.println(yesorno);
//	
//	boolean yesorno1 = m.find();//�÷���ɨ����������У��������ģʽ��ƥ���������
//	System.out.println(yesorno1);
//	
//	System.out.println(m.find());//��ӡ������Ҹ�ģʽ��ƥ��������У�����һ��ֻ�ܴ�ӡһ���������Ҫѭ�����ã���ӡ�Ƿ�ƥ�䵽��
//	System.out.println(m.find());//��ӡ������
//	
	while(m.find()){
//		System.out.println(m.group());//group()��group(0)��ʾһ��
//		System.out.println(m.group(0));
//		System.out.println(m.group(1));//��ʾ��ͬ�Ĳ�����
	}
}
}
