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
 * ���ģ��䣩���ƶȼ���
 * @anthor ţ˶
 * @date 2019.5.30
 * ���ƶȵĻ�������˼·��:
 * 1) �ҳ���ƪ���ģ��䣩�Ĺؼ���
 * 2) ÿƪ���¸�ȡ���ؼ��֣��ϲ���һ�����ϣ�����ÿƪ���¶���������ϵĴ�Ƶ
 * 3) ������ƪ���µĴ�Ƶ���������ڼ�����ͳ�ƣ�
 * 4������������������������ƶȣ�ֵԽ��ͱ�ʾԽ���ƣ�ȡֵ��[0��1]֮�䣩
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

	// ���������ƶ�
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
	// ��ƽ����
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

	// ��˷�
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
	 * ��ȡ��Ҫ��ȡ���ļ�һ�����ж��ٸ����ı�
	 * �ļ��ж��ı����������б�ʾ��һ�д���һƪ���ģ��䣩
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
		File file = new File("C:/Users/ţ˶/Desktop/1.txt");//��ȡ���ļ�·��
		int rowNums = getRowNums(file);//��ö��ģ��䣩��Ŀ
		String[] phrase = getText(file,rowNums);//��ÿ�����ģ��䣩��String[]װ����
		for(int j = 0;j < phrase.length;j++){
			for(int k = 1;k < phrase.length;k++){
				String s1 = phrase[j];
				String s2 = phrase[k];
				Test4 similarity = new Test4(s1,s2);//���÷������о������ƶȱȽ�
				System.out.println("��" + j +"�����" + k +"��ľ������ƶȣ�");
				System.out.println(similarity.sim());
				System.out.println("--------------");
			}
		}
	}
}
