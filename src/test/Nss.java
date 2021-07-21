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

		List<String> wordA = getWords("C:/Users/ţ˶/Desktop/1.txt");
		List<String> wordB = getWords("C:/Users/ţ˶/Desktop/2.txt");
		// A������B�����еļ���
		List<String> wordC = new ArrayList<String>();
		// A������B����û�еļ���
		List<String> wordD = new ArrayList<String>();

		for (String words : wordA) {
			if (wordB.contains(words)) {
				wordC.add(words);
			} else {
				wordD.add(words);
			}
		}

		// ���wordDΪ�գ�˵��A�ĵ���B���涼��
		if (wordD.isEmpty()) {
			return;
		}

		File a = new File("C:/Users/ţ˶/Desktop/1.txt");
		File c = new File("C:/Users/ţ˶/Desktop/result.txt");
		c.createNewFile();

		// ��A�ļ���д��B�д��ڵĵ���
		BufferedWriter bw = new BufferedWriter(new FileWriter(a));
		for (String w : wordC) {
			bw.write(w + "");
		}
		bw.flush();
		bw.close();

		// ��C�ļ���д��B�в����ڵĵ���
		bw = new BufferedWriter(new FileWriter(c));
		for (String w : wordD) {
			bw.write(w + "");
		}
		bw.flush();
		bw.close();
	}

	/**
	 * ���ļ��ж�ȡ����
	 * @param filePath �ļ�����·��
	 * @return
	 * @throws IOException 
	 */
	static List<String> getWords(String filePath) throws IOException {
		List<String> words = new ArrayList<String>();
		File file = new File(filePath);

		if (!file.exists() || file.isDirectory()) {
			System.out.println("�ļ�·����Ч");
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