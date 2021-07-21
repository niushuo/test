package test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Nss {

	public static void main(String[] args) throws Exception {

		List<String> wordA = getWords("C:/Users/牛硕/Desktop/1.txt");
		List<String> wordB = getWords("C:/Users/牛硕/Desktop/2.txt");
		// A单词中B里面有的集合
		List<String> wordC = new ArrayList<String>();
		// A单词中B里面没有的集合
		List<String> wordD = new ArrayList<String>();

		for (String words : wordA) {
			if (wordB.contains(words)) {
				wordC.add(words);
			} else {
				wordD.add(words);
			}
		}

		// 如果wordD为空，说明A的单词B里面都有
		if (wordD.isEmpty()) {
			return;
		}

		File a = new File("C:/Users/牛硕/Desktop/1.txt");
		File c = new File("C:/Users/牛硕/Desktop/result.txt");
		c.createNewFile();

		// 向A文件中写入B中存在的单词
		BufferedWriter bw = new BufferedWriter(new FileWriter(a));
		for (String w : wordC) {
			bw.write(w + "");
		}
		bw.flush();
		bw.close();

		// 向C文件中写入B中不存在的单词
		bw = new BufferedWriter(new FileWriter(c));
		for (String w : wordD) {
			bw.write(w + "");
		}
		bw.flush();
		bw.close();
	}

	/**
	 * 从文件中读取单词
	 * @param filePath 文件完整路径
	 * @return
	 * @throws IOException 
	 */
	static List<String> getWords(String filePath) throws IOException {
		List<String> words = new ArrayList<String>();
		File file = new File(filePath);

		if (!file.exists() || file.isDirectory()) {
			System.out.println("文件路径无效");
			return words;
		}

		BufferedReader br = new BufferedReader(new FileReader(file));
		String tmp = null;

		while ((tmp = br.readLine()) != null) {
			words.add(tmp);
		}

		br.close();
		return words;
	}

}