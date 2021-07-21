package test;
/**
 * 抽象类和抽象方法实现的一个例子
 * @author 牛硕
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

// 这就是一个抽象类
abstract class Animal {
	String name;
	int age;
	// 动物会叫
	//不确定动物怎么叫的。定义成抽象方法，来解决父类方法的不确定性。抽象方法在父类中不能实现，所以没有函数体。但在后续在继承时，要具体实现此方法。
	public abstract void cry(); 
	public abstract void smile(); 
	public abstract void run();
}

// 抽象类可以被继承
// 当继承的父类是抽象类时，需要将抽象类中的所有抽象方法全部实现。
class cat extends Animal {
	// 实现父类的cry抽象方法
	public void cry() {
		System.out.println("猫叫:喵喵喵");
		}
	public void smile(){
		System.out.println("猫笑：嘿嘿嘿");
	}
	public void run(){
		System.out.println("猫跑起来是木有声音滴");
	}
}