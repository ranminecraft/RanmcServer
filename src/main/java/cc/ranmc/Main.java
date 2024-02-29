package cc.ranmc;

import cc.ranmc.network.BanlistHandler;
import cc.ranmc.network.PointHandler;
import cc.ranmc.util.Logger;
import cn.hutool.http.HttpUtil;
import io.github.biezhi.ome.OhMyEmail;
import lombok.Getter;

import static cc.ranmc.constant.Data.AUTHOR;
import static cc.ranmc.constant.Data.BANLIST_PATH;
import static cc.ranmc.constant.Data.EMAIL;
import static cc.ranmc.constant.Data.POINT_PATH;
import static cc.ranmc.constant.Data.PORT;
import static cc.ranmc.constant.Data.VERSION;
import static cc.ranmc.constant.Data.WEB_SITE;

@Getter
public final class Main {

    public static void main(String[] args) {

        System.out.println("-----------------------");
        System.out.println("RanmcServer By " + AUTHOR);
        System.out.println("Version: " + VERSION);
        System.out.println(WEB_SITE);
        System.out.println("-----------------------");

        // 初始化邮件
        OhMyEmail.config(OhMyEmail.SMTP_QQ(false), "ranica@qq.com", EMAIL);

        BanlistHandler banlistHandler = new BanlistHandler();
        PointHandler pointHandler = new PointHandler();
        HttpUtil.createServer(PORT)
                .addAction(BANLIST_PATH, banlistHandler::handle)
                .addAction(POINT_PATH, pointHandler::handle)
                .start();

        Logger.info("已成功运行在端口" + PORT);
    }


}