package test;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.sis.util.Static;

/*
 * 用wmd的思想，实现一个句子相似度的计算
 */

public class Test7 {
	public static void main(String[] args) {
//		wd.loadJavaModel(modelFile);
		String s1 = "";
		String s2 = "";
		
		List<Term> tm1 = (List<Term>) ToAnalysis.parse(s1);
		List<Term> tm2 = (List<Term>) ToAnalysis.parse(s2);
		
		List<float[]> sv1 = new ArrayList<>();
		List<float[]> sv2 = new ArrayList<>();
		
		for(Term term1:tm1){
			String word = term1.getName();
			System.out.println(word);
//			float[] v = wd.getWordVector(word);
//			sv1.add(v);
		}
		
		for(Term term2:tm2){
			String word = term2.getName();
			System.out.println(word);
//			float[] v = wd.getWordVector(word);
//			sv2.add(v);
		}
		
		List<Float> dv1 = new ArrayList<>();
		List<Float> dv2 = new ArrayList<>();
		for(float[] v1 :sv1){
			float min = (float) 999999999999999999999.0;
			for(float[] v2 : sv2){
				float dis = multi(v1,v2);
				float dist = 1 / dis;
				if(dist < min) min = dist;
			}
			dv1.add(1 / min);
		}
		int x = 0;
		float sum = 0;
		for(Float fl : dv1){
			sum += fl;
			x++;
		}
		float averageDis = sum / x;
		System.out.println(averageDis);
	}
		private static float multi(float[] a ,float[] b){
		float sum = 0;
		float sum1 = 0;
		float sum2 = 0;
	
		for(int i = 0;i < a.length;i++){
			sum = sum + a[i] * b[i];
		}
		for(int m = 0; m < a.length;m++){
			sum1 = sum1 + a[m] * a[m];
		}
		for(int n = 0; n < b.length;n++){
			sum2 = sum2 + b[n] * b[n];
		}
		float cos = (float) (sum/(Math.sqrt(sum1) * Math.sqrt(sum2)));
		return Math.abs(cos);
	}
}
