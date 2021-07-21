package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import com.drew.metadata.photoshop.PsdHeaderDescriptor;

/**
 * ��ȡһ��Ŀ¼�µĶ�������ı�������ÿ���ı�����һ�����
 * @author ţ˶
 *@date 2018.9.19
 */
public class ChangeToLine {
	public static void main(String[] args) throws IOException, TikaException {

		/**
		 * д���ַ�����һ���ļ���
		 */
		
			//���ļ�
			FileOutputStream fos=new FileOutputStream("C:/Users/ţ˶/Desktop/result.txt");	//д����ļ�·��
			//���ñ��뼯
		
			OutputStreamWriter osw=new OutputStreamWriter(fos,"utf-8");
		Tika tika = new Tika();
		File path = new File("F:/�����ı�����/test_corpus");//�����ļ���·��
		
		for(File file:path.listFiles()){
			String label = "";
			
			if(file.isDirectory()){
				label = file.getName();
				for(File f:file.listFiles()){
					String text = tika.parseToString(f);
					text = text.replace("\r", "").replace("\n", "");
					String line = label + " " + text + "\n";
//					System.out.println(line);
					
						
						/**
						 * ���������Զ�ˢ�µ�PrinterWriter��ÿ������ʹ��println()/print()
						 * ����д���ַ����󣬶����Զ�����flush()����
						 * ����,������ɻ�����д������������д��Ч��
						 */
						//д������
					PrintWriter pw=new PrintWriter(osw);
					pw.write(line);
					
					
			}
				
		}
	}
	}
}
