package test;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * ���ַ���д���ļ���
 * PrintWriter:�Ǿ����Զ���ˢ�µĻ����ַ������(�߼���)
 * @author ţ˶
 *
 */
public class WriteStringTofiles {
	public static void main(String[] args) {
		
		try {
			//���ļ�
			FileOutputStream fos=new FileOutputStream("C:/Users/ţ˶/Desktop/result.txt");
			//���ñ��뼯
			OutputStreamWriter osw=new OutputStreamWriter(fos,"UTF-8");

			/**
			 * ���������Զ�ˢ�µ�PrinterWriter��ÿ������ʹ��println()/print()
			 * ����д���ַ����󣬶����Զ�����flush()����
			 * ����,������ɻ�����д������������д��Ч��
			 */
			//д������
			PrintWriter pw=new PrintWriter(osw,true);
			pw.println("�ַ�����");
			pw.append("��������");
			pw.write("������");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}