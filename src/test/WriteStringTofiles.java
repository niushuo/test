package test;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 将字符串写到文件中
 * PrintWriter:是具有自动行刷新的缓冲字符输出流(高级流)
 * @author 牛硕
 *
 */
public class WriteStringTofiles {
	public static void main(String[] args) {
		
		try {
			//打开文件
			FileOutputStream fos=new FileOutputStream("C:/Users/牛硕/Desktop/result.txt");
			//设置编码集
			OutputStreamWriter osw=new OutputStreamWriter(fos,"UTF-8");

			/**
			 * 创建具有自动刷新的PrinterWriter后，每当我们使用println()/print()
			 * 方法写出字符串后，都会自动调用flush()方法
			 * 但是,这个无疑会增加写出次数而降低写出效率
			 */
			//写出数据
			PrintWriter pw=new PrintWriter(osw,true);
			pw.println("字符缓冲");
			pw.append("测试数据");
			pw.write("哈哈哈");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}