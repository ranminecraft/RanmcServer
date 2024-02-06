package cc.ranmc.util;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class DataFile {

    public static String read(String name) {
        try {
            File file = new File(System.getProperty("user.dir") + "/config/" + name + ".txt");
            return FileUtils.fileRead(file, "utf8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public static void write(String name,String text) {
        try {
            File file = new File(System.getProperty("user.dir") + "/config/" + name + ".txt");
            FileUtils.fileWrite(file, text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
