package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * ∂¡»°Õ®”√¥ ¥ µ‰
 * @author ≈£À∂
 *
 */
public class StringUtil {
public static Set<String> getStopWordList(String stopWordFile) throws FileNotFoundException, IOException {
	Set<String> stopList = new HashSet<>();
	
	try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stopWordFile)))) {
		String temp = null;
		while((temp = br.readLine()) != null) {
			if(temp.length() < 3) continue;
			stopList.add(temp);
		}
	}
	stopList.add("  ");
	return stopList;
	}
}
