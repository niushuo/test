package test;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class SplitWords {
	public static void main(String[] args) throws IOException {
		List<String> list1 = new ArrayList<>();
		FileReader fr = new FileReader("C:/Users/牛硕/Desktop/1.txt");
		BufferedReader br = new BufferedReader(fr);
		String str = null;
		
		while((str = br.readLine()) != null){
			list1.add(str);
		}
		
		System.out.println(Split(list1));
		FileOutputStream out = new FileOutputStream("C:/Users/牛硕/Desktop/2.txt",true);
		out.write(Split(list1).getBytes());
		out.flush();
		out.close();//将控制台打印的信息输出到文件中并保存
	}
	
	public static String Split(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for(String str:list) {
			Result result = ToAnalysis.parse(str);
			sb.append(result + "\r\n");
		}
		return sb.toString();
	}

}
