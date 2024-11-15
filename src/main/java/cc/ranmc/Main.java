package cc.ranmc;

import cc.ranmc.network.BanlistHandler;
import cc.ranmc.network.BaseHandler;
import cc.ranmc.network.BroadcastHandler;
import cc.ranmc.network.CheckHandler;
import cc.ranmc.network.VerifyHandler;
import cn.hutool.http.HttpUtil;
import io.github.biezhi.ome.OhMyEmail;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static cc.ranmc.constant.Data.AUTHOR;
import static cc.ranmc.constant.Data.BANLIST_PATH;
import static cc.ranmc.constant.Data.BASE_PATH;
import static cc.ranmc.constant.Data.BROADCAST_PATH;
import static cc.ranmc.constant.Data.CHECK_PATH;
import static cc.ranmc.constant.Data.EMAIL_PWD;
import static cc.ranmc.constant.Data.PORT;
import static cc.ranmc.constant.Data.VERIFY_PATH;
import static cc.ranmc.constant.Data.VERSION;
import static cc.ranmc.constant.Data.WEB_SITE;
import static io.github.biezhi.ome.OhMyEmail.defaultConfig;

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
        Properties props = defaultConfig(false);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.sohu.com");
        props.put("mail.smtp.port", "465");
        OhMyEmail.config(props, "minelive@sohu.com", EMAIL_PWD);

        HttpUtil.createServer(PORT)
                .addAction(BASE_PATH, new BaseHandler()::handle)
                .addAction(BANLIST_PATH, new BanlistHandler()::handle)
                .addAction(BROADCAST_PATH, new BroadcastHandler()::handle)
                .addAction(VERIFY_PATH, new VerifyHandler()::handle)
                .addAction(CHECK_PATH, new CheckHandler()::handle)
                .start();

        getLogger().info("已成功运行在端口" + PORT);
    }


}