package test;

import javax.measure.quantity.Length;

public class Test8 {
	public static void main(String[] args) {
		int num1 = 2;
		int num2 = 3;
		int target1 = (int) Math.pow(2,num1);
        int target2 = (int) Math.pow(2,num2);
        int i = 0;
        int a = 0;
        int b = 0;
        while(a != 1){
        	a = target1 >> 1;
            i++;
        }
         while(b != 1){
        	a = target1 >> 1;
            i++;
        } 
         System.out.println(i);
	}
}
	