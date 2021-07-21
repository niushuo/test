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
 * 读取一个目录下的多个中文文本，并将每个文本作文一行输出
 * @author 牛硕
 *@date 2018.9.19
 */
public class ChangeToLine {
	public static void main(String[] args) throws IOException, TikaException {

		/**
		 * 写入字符串到一个文件中
		 */
		
			//打开文件
			FileOutputStream fos=new FileOutputStream("C:/Users/牛硕/Desktop/result.txt");	//写入的文件路径
			//设置编码集
		
			OutputStreamWriter osw=new OutputStreamWriter(fos,"utf-8");
		Tika tika = new Tika();
		File path = new File("F:/复旦文本分类/test_corpus");//读入文件的路径
		
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
						 * 创建具有自动刷新的PrinterWriter后，每当我们使用println()/print()
						 * 方法写出字符串后，都会自动调用flush()方法
						 * 但是,这个无疑会增加写出次数而降低写出效率
						 */
						//写出数据
					PrintWriter pw=new PrintWriter(osw);
					pw.write(line);
					
					
			}
				
		}
	}
	}
}
