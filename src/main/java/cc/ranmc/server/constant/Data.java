package cc.ranmc.server.constant;

import static cc.ranmc.server.util.ConfigUtil.CONFIG;

public class Data {
    public static final String AUTHOR = "Ranica";
    public static final String VERSION = "Release 6.1";
    public static final String WEB_SITE = "https://www.ranmc.cc/";
    public static final String VERIFY_HOST = "verify.ranmc.cc";
    public static final String BOT_CHECK_WEB_SITE = "https://" + VERIFY_HOST + "/check?key=";
    public static final String VERIFY_WEB_SITE = "https://www.ranmc.cc/verify.html?key=";
    public static final int PORT = 2263;
    public static final String BASE_PATH = "/";
    public static final String VERIFY_PATH = "/verify";
    public static final String BROADCAST_PATH = "/broadcast";
    public static final String TOP_PATH = "/top";
    public static final String BANLIST_PATH = "/banlist";
    public static final String CHECK_PATH = "/check";
    public static final String TOKEN = CONFIG.getString("token");
    public static final String EMAIL_PWD = CONFIG.getString("email");
}
