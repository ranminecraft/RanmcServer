package cc.ranmc;

import cc.ranmc.network.Server;
import cc.ranmc.util.Logger;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import io.github.biezhi.ome.OhMyEmail;
import lombok.Getter;

import java.net.InetSocketAddress;

import static cc.ranmc.constant.Data.AUTHOR;
import static cc.ranmc.constant.Data.EMAIL;
import static cc.ranmc.constant.Data.HTTP_PATH;
import static cc.ranmc.constant.Data.PORT;
import static cc.ranmc.constant.Data.VERSION;
import static cc.ranmc.constant.Data.WEB_SITE;

@Getter
public final class Main {

    public static void main(String[] args) {

        System.out.println("-----------------------");
        System.out.println("RanmcPointServer By " + AUTHOR);
        System.out.println("Version: " + VERSION);
        System.out.println(WEB_SITE);
        System.out.println("-----------------------");

        HttpServer httpserver;
        try {
            HttpServerProvider provider = HttpServerProvider.provider();
            httpserver = provider.createHttpServer(new InetSocketAddress(PORT), 100);
            httpserver.createContext(HTTP_PATH, new Server());
            httpserver.setExecutor(null);
            httpserver.start();
        } catch (Exception e) {
            Logger.info(e.getMessage());
        }

        Logger.info("已成功运行在端口" + PORT);

        // 初始化邮件
        OhMyEmail.config(OhMyEmail.SMTP_QQ(false), "ranica@qq.com", EMAIL);
    }


}