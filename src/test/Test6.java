package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.spi.DirStateFactory.Result;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.log4j.chainsaw.Main;
import org.apache.tika.Tika;

import test.StringUtil;

public class Test6 {
	public static void main(String[] args) throws Exception { 

	File path = new File("C:/Users/牛硕/Desktop/1");
	
	Tika tika = new Tika();
	for(File file:path.listFiles()){
		String topic = "";
		if(file.isDirectory()){
			topic = file.getName();//文件夹名字就是主题的名称
			System.out.println("topic=" + topic);

			int topicWordCount = 0;//主题下的词的数量
			for(File f:file.listFiles()){
				System.out.println(" f=" + f.getName());

				String text = tika.parseToString(f);//利用tika对文本进行抽取
//				System.out.println(text);
				org.ansj.domain.Result parse = ToAnalysis.parse(text);//对文本进行分词
				for(Term term:parse){
					topicWordCount++;
					String word = term.getName();
					System.out.println(word);
}
				System.out.println(topicWordCount);
}
		}
	}
	}
}