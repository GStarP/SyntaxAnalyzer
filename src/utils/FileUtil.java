package utils;

import java.io.*;

public class FileUtil {

    // 读取指定文件内容
    public static String readFile(String path) {
        try {
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String res = "";
            String line;
            while((line = reader.readLine()) != null) {
                res += line;
                res += '\n';
            }
            reader.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 将内容输出为指定文件
    public static void outputAsFile(String path, String content) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            writer.println(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
