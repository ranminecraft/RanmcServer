package cc.ranmc.constant;

import cc.ranmc.util.DataFile;

public class Data {
    public static final String AUTHOR = "Ranica";
    public static final String VERSION = "2.0";
    public static final String WEB_SITE = "https://www.ranmc.cc/";
    public static final int PORT = 2263;
    public static final String HTTP_PATH = "/point";
    public static final String TOKEN = DataFile.read("token");
    public static final String EMAIL = DataFile.read("email");
}