package test;
/**
 * ����static
 * @author ţ˶
 *
 */
public class Test1 {
	int id;
	String name;
	String pwd;
	static String company = "ţ˶˶�Ĺ�˾";


	public Test1(int id,String name){
		this.id = id;
		this.name = name;
	}
	public void login(){
		System.out.println("��¼��" + name);
	}
	public static void printCompany(){
		System.out.println(company);
	}
	public static void main(String[] args) {
		Test1 t = new Test1(1, "ţ˶");
		t.printCompany();
		t.login();
//		Test1.printCompany();
//		Test1.company = "ţ˶�Ĺ�˾";
//		Test1.printCompany();
	}
}