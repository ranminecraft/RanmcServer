package cc.ranmc.server;

import cc.ranmc.server.network.*;
import cn.hutool.http.HttpUtil;
import io.github.biezhi.ome.OhMyEmail;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cc.ranmc.server.constant.Data.*;
import static io.github.biezhi.ome.OhMyEmail.SMTP_QQ;

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
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.sohu.com");
        props.put("mail.smtp.port", "465");
        OhMyEmail.config(props, "minelive@sohu.com", EMAIL_PWD);*/
        OhMyEmail.config(SMTP_QQ(false), "xyfwdy@qq.com", EMAIL_PWD);

        HttpUtil.createServer(PORT)
                .addAction(BASE_PATH, new BaseHandler()::handle)
                .addAction(BANLIST_PATH, new BanlistHandler()::handle)
                .addAction(BROADCAST_PATH, new BroadcastHandler()::handle)
                .addAction(VERIFY_PATH, new VerifyHandler()::handle)
                .addAction(CHECK_PATH, new CheckHandler()::handle)
                //.addAction(AUTH_PATH, new AuthHandler()::handle)
                .start();

        getLogger().info("已成功运行在端口" + PORT);
    }


}