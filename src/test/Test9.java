package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Test9 {
public static void main(String[] args) {

    String fileContent = "";
    try {
        File f = new File("C:\\Users\\牛硕\\Desktop\\1.txt");
        if (f.isFile() && f.exists()) {
            InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF-8");
            BufferedReader reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent += line;
            }
            read.close();
        }
    } catch (Exception e) {
        System.out.println("读取文件内容操作出错");
        e.printStackTrace();
    }
  System.out.println(fileContent);
}
}

