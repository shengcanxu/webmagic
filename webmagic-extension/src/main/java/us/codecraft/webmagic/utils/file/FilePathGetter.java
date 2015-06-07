package us.codecraft.webmagic.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cano on 2015/6/7.
 */
public class FilePathGetter {

    public static void  recursion(String root, FileOutputStream fos) throws IOException {
        File file = new File(root);
        File[] subFile = file.listFiles();
        for (int i = 0; i < subFile.length; i++) {
            if (subFile[i].isDirectory()) {
                recursion(subFile[i].getAbsolutePath(), fos);
            }else{
                String path = subFile[i].getPath();
                System.out.println(path);
                path = path + "\n";
                fos.write(path.getBytes());
            }
        }
    }

    public static void main(String[] args) {
        String filePath = "D:/software/redis/redisClient/test.txt";
        try {
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            recursion("D:/software/redis/redisClient", fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
