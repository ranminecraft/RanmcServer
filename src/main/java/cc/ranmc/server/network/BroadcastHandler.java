package cc.ranmc.server.network;

import cc.ranmc.server.Main;
import cc.ranmc.server.constant.Code;
import cc.ranmc.server.constant.Data;
import cc.ranmc.server.constant.Prams;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONObject;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;

public class BroadcastHandler extends BaseHandler {

    @Override
    public void handle(HttpServerRequest req, HttpServerResponse res) {

        JSONObject json = new JSONObject();
        if (!req.getParams().containsKey(Prams.TOKEN) ||
                !req.getParams(Prams.TOKEN).getFirst().equals(Data.TOKEN)) {
            json.set(Prams.CODE, Code.NO_PERMISSION);
            res.write(json.toString(), ContentType.JSON.toString());
            return;
        }
        if (req.getParams().containsKey(Prams.MSG)) {
            //String msg = URLDecoder.decode(map.get(Prams.MSG), StandardCharsets.UTF_8);
            String msg = req.getParams(Prams.MSG).getFirst();
            try {
                OhMyEmail.subject("服务器消息")
                        .from("【桃花源】")
                        .to("xyfwdy@qq.com")
                        .html(msg)
                        .send();
                json.set(Prams.CODE, Code.SUCCESS);
            } catch (SendMailException e) {
                Main.getLogger().info("发送邮件失败：{}", e.getMessage());
                json.set(Prams.CODE, Code.ERROR);
            }
            Main.getLogger().info("发出广播:{}", msg);
        } else {
            json.set(Prams.CODE, Code.UNKOWN_REQUEST);
        }
        res.write(json.toString(), ContentType.JSON.toString());
    }
}

