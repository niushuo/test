package test;
/**
 * ²âÊÔstatic
 * @author Å£Ë¶
 *
 */
public class Test1 {
	int id;
	String name;
	String pwd;
	static String company = "Å£Ë¶Ë¶µÄ¹«Ë¾";


	public Test1(int id,String name){
		this.id = id;
		this.name = name;
	}
	public void login(){
		System.out.println("µÇÂ¼£º" + name);
	}
	public static void printCompany(){
		System.out.println(company);
	}
	public static void main(String[] args) {
		Test1 t = new Test1(1, "Å£Ë¶");
		t.printCompany();
		t.login();
//		Test1.printCompany();
//		Test1.company = "Å£Ë¶µÄ¹«Ë¾";
//		Test1.printCompany();
	}
}