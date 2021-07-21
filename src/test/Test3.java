package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;

import com.uwyn.jhighlight.fastutil.Hash;

/**
 * @author 牛硕
 *传递n个int型的参数，并且计算这些参数的和。
 */
public class Test3 {
	public static void main(String[] args) {
		int[] nums = {2,7,11,15};
		int target = 9;
		int[] a =test(nums,target);
		
		System.out.println(a[0]);
		System.out.println(a[1]);
	}

	private static int[] test(int[] nums, int target) {
		// TODO Auto-generated method stub
		int[] result = new int[2];
        Map<Integer,Integer> map = new HashMap<>();
        for(int i = 0; i < nums.length;++i){
            map.put(nums[i],i);
        }
            for(int i = 0; i < nums.length;++i){
            int answer = target - nums[i];
            if(map.containsKey(answer) && i != map.get(answer))
//            	result[0] = i;
//            result[1] = map.get(answer);
//            break;
                 return new int[]{i, map.get(answer)};
        }
        return result;
	} 
}
