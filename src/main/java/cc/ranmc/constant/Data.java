package cc.ranmc.constant;

import cc.ranmc.util.DataFile;

public class Data {
    public static final String AUTHOR = "Ranica";
    public static final String VERSION = "Release 3.5";
    public static final String WEB_SITE = "https://www.ranmc.cc/";
    public static final String VERIFY_WEB_SITE = "https://www.ranmc.cc/verify.html?key=";
    public static final int PORT = 2263;
    public static final String BASE_PATH = "/";
    public static final String VERIFY_PATH = "/verify";
    public static final String BROADCAST_PATH = "/broadcast";
    public static final String BANLIST_PATH = "/banlist";
    public static final String TOKEN = DataFile.read("token");
    public static final String EMAIL_PWD = DataFile.read("email");
}
