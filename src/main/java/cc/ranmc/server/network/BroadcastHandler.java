package cc.ranmc.server.network;

import cc.ranmc.server.Main;
import cc.ranmc.server.constant.Code;
import cc.ranmc.server.constant.Data;
import cc.ranmc.server.constant.Prams;
import com.alibaba.fastjson2.JSONObject;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import io.javalin.http.Context;

public class BroadcastHandler {

    public static void handle(Context context) {
        context.contentType("application/json");
        JSONObject json = new JSONObject();
        if (!context.queryParamMap().containsKey(Prams.TOKEN) ||
                !Data.TOKEN.equals(context.queryParam(Prams.TOKEN))) {
            json.put(Prams.CODE, Code.NO_PERMISSION);
            context.result(json.toString());
            return;
        }
        if (context.queryParamMap().containsKey(Prams.MSG)) {
            //String msg = URLDecoder.decode(map.get(Prams.MSG), StandardCharsets.UTF_8);
            String msg = context.queryParam(Prams.MSG);
            try {
                OhMyEmail.subject("服务器消息")
                        .from("【桃花源】")
                        .to("xyfwdy@qq.com")
                        .html(msg)
                        .send();
                json.put(Prams.CODE, Code.SUCCESS);
            } catch (SendMailException e) {
                Main.getLogger().info("发送邮件失败：{}", e.getMessage());
                json.put(Prams.CODE, Code.ERROR);
            }
            Main.getLogger().info("发出广播:{}", msg);
        } else {
            json.put(Prams.CODE, Code.UNKOWN_REQUEST);
        }
        context.result(json.toString());
    }
}

