package test;
/**
 * ������ͳ��󷽷�ʵ�ֵ�һ������
 * @author ţ˶
 *
 */

public class Test2 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		cat c = new cat();
		c.cry();
		c.smile();
		c.run();
	}
}

// �����һ��������
abstract class Animal {
	String name;
	int age;
	// ������
	//��ȷ��������ô�еġ�����ɳ��󷽷�����������෽���Ĳ�ȷ���ԡ����󷽷��ڸ����в���ʵ�֣�����û�к����塣���ں����ڼ̳�ʱ��Ҫ����ʵ�ִ˷�����
	public abstract void cry(); 
	public abstract void smile(); 
	public abstract void run();
}

// ��������Ա��̳�
// ���̳еĸ����ǳ�����ʱ����Ҫ���������е����г��󷽷�ȫ��ʵ�֡�
class cat extends Animal {
	// ʵ�ָ����cry���󷽷�
	public void cry() {
		System.out.println("è��:������");
		}
	public void smile(){
		System.out.println("èЦ���ٺٺ�");
	}
	public void run(){
		System.out.println("è��������ľ��������");
	}
}