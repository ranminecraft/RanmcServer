package cc.ranmc;

import cc.ranmc.network.BanlistHandler;
import cc.ranmc.network.PointHandler;
import cc.ranmc.network.VerifyHandler;
import cn.hutool.http.HttpUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cc.ranmc.constant.Data.AUTHOR;
import static cc.ranmc.constant.Data.BANLIST_PATH;
import static cc.ranmc.constant.Data.POINT_PATH;
import static cc.ranmc.constant.Data.PORT;
import static cc.ranmc.constant.Data.VERIFY_PATH;
import static cc.ranmc.constant.Data.VERSION;
import static cc.ranmc.constant.Data.WEB_SITE;

@Getter
public final class Main {

    @Getter
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        System.out.println("-----------------------");
        System.out.println("RanmcServer By " + AUTHOR);
        System.out.println("Version: " + VERSION);
        System.out.println(WEB_SITE);
        System.out.println("-----------------------");

        // 初始化邮件
        /*Properties props = defaultConfig(false);
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");
        OhMyEmail.config(props, "thyranmc@outlook.com", EMAIL_PWD);*/
        HttpUtil.createServer(PORT)
                .addAction(BANLIST_PATH, new BanlistHandler()::handle)
                .addAction(POINT_PATH, new PointHandler()::handle)
                .addAction(VERIFY_PATH, new VerifyHandler()::handle)
                .start();

        getLogger().info("已成功运行在端口" + PORT);
    }


}