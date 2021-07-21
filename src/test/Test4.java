package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import com.google.common.base.Ticker;import opennlp.tools.stemmer.snowball.irishStemmer;

/*
 * 短文（句）相似度计算
 * @anthor 牛硕
 * @date 2019.5.30
 * 相似度的基本处理思路是:
 * 1) 找出两篇短文（句）的关键字
 * 2) 每篇文章各取出关键字，合并成一个集合，计算每篇文章对于这个集合的词频
 * 3) 生成两篇文章的词频向量（可在集合中统计）
 * 4）计算出两个向量的余弦相似度，值越大就表示越相似（取值在[0，1]之间）
 */
public class Test4 {
	Map<Character, int[]> vectorMap = new HashMap<Character, int[]>();

	int[] tempArray = null;
	public Test4(String string1, String string2) {

		for (Character character1 : string1.toCharArray()) {
			if (vectorMap.containsKey(character1)) {
				vectorMap.get(character1)[0]++;
			} else {
				tempArray = new int[2];
				tempArray[0] = 1;
				tempArray[1] = 0;
				vectorMap.put(character1, tempArray);
			}
		}
		for (Character character2 : string2.toCharArray()) {
			if (vectorMap.containsKey(character2)) {
				vectorMap.get(character2)[1]++;
			} else {
				tempArray = new int[2];
				tempArray[0] = 0;
				tempArray[1] = 1;
				vectorMap.put(character2, tempArray);
			}
		}
	}

	// 求余弦相似度
	public double sim() {
		double result = 0;
		result = pointMulti(vectorMap) / sqrtMulti(vectorMap);
		return result;
	}

	private double sqrtMulti(Map<Character, int[]> paramMap) {
		double result = 0;
		result = squares(paramMap);
		result = Math.sqrt(result);
		return result;
	}
	// 求平方和
	private double squares(Map<Character, int[]> paramMap) {
		double result1 = 0;
		double result2 = 0;
		Set<Character> keySet = paramMap.keySet();
		for (Character character : keySet) {
			int temp[] = paramMap.get(character);
			result1 += (temp[0] * temp[0]);
			result2 += (temp[1] * temp[1]);
		}
		return result1 * result2;
	}

	// 点乘法
	private double pointMulti(Map<Character, int[]> paramMap) {
		double result = 0;
		Set<Character> keySet = paramMap.keySet();
		for (Character character : keySet) {
			int temp[] = paramMap.get(character);
			result += (temp[0] * temp[1]);
		}
		return result;
	}

	private static String[] getText(File filePath,int row) throws IOException, TikaException {
		// TODO Auto-generated method stub
		String[] texts = new String[row];

		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String tmp = null;
		int i = 0;
		StringBuffer str = new StringBuffer();
		while((tmp = br.readLine()) != null){
			org.ansj.domain.Result parse = ToAnalysis.parse(tmp);
			StringBuffer sb = new StringBuffer();
			for(Term term:parse){
				String word = term.getName();
				//				System.out.println(word);
				sb.append(word);
			}
			texts[i] = sb.toString();
			i++;
		}
		return texts;
	}
	
	/*
	 * 获取所要读取的文件一共含有多少个短文本
	 * 文件中短文本的数量用行表示，一行代表一篇短文（句）
	 */
	private static int getRowNums(File filePaths) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader brs = new BufferedReader(new FileReader(filePaths));
		String tmp = null;
		int i = 0;
		StringBuffer str = new StringBuffer();
		while((tmp = brs.readLine()) != null){
			i++;
		}
		return i;
	}
	
	public static void main(String[] args) throws IOException, TikaException {
		File file = new File("C:/Users/牛硕/Desktop/1.txt");//读取的文件路径
		int rowNums = getRowNums(file);//获得短文（句）数目
		String[] phrase = getText(file,rowNums);//将每个短文（句）用String[]装起来
		for(int j = 0;j < phrase.length;j++){
			for(int k = 1;k < phrase.length;k++){
				String s1 = phrase[j];
				String s2 = phrase[k];
				Test4 similarity = new Test4(s1,s2);//调用方法进行句子相似度比较
				System.out.println("第" + j +"句与第" + k +"句的句子相似度：");
				System.out.println(similarity.sim());
				System.out.println("--------------");
			}
		}
	}
}
