package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 测试java中正则表达式对象的基本用法
 * @author 牛硕
 *
 */
public class Zhengze {
public static void main(String[] args) {
	Pattern  p = Pattern.compile("[<]");//创建表达式对象，注意java中所有遇到\需要变为\\
	Matcher m = p.matcher("aa232**ssd24622<,<<");//创建Matcher对象
//	System.out.println(m);
//	boolean yesorno = m.matches();//尝试将字符序列与该模式匹配
//	System.out.println(yesorno);
//	
//	boolean yesorno1 = m.find();//该方法扫描输入的序列，查找与该模式下匹配的子序列
//	System.out.println(yesorno1);
//	
//	System.out.println(m.find());//打印出与查找该模式下匹配的子序列，由于一次只能打印一个，因此需要循环调用（打印是否匹配到）
//	System.out.println(m.find());//打印出内容
//	
	while(m.find()){
//		System.out.println(m.group());//group()与group(0)表示一样
//		System.out.println(m.group(0));
//		System.out.println(m.group(1));//表示不同的捕获组
	}
}
}
