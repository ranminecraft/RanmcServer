package cc.ranmc.server.network;

import cc.ranmc.server.bean.BotCheckBean;
import cc.ranmc.server.constant.Data;
import cc.ranmc.server.constant.Prams;
import cc.ranmc.server.util.BotCheckUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONObject;

import static cc.ranmc.server.constant.Code.BAD_REQUEST;
import static cc.ranmc.server.constant.Data.TOKEN;

public class CheckHandler extends BaseHandler {

    @Override
    public void handle(HttpServerRequest req, HttpServerResponse res) {

        // 允许跨域
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", "*");
        res.addHeader("Access-Control-Allow-Headers", "*");
        res.addHeader("Access-Control-Max-Age", "*");
        res.addHeader("Access-Control-Allow-Credentials", "true");
        if ("OPTIONS".equals(req.getMethod())) {
            res.sendOk();
            return;
        }

        if (req.getParams().containsKey(Prams.TOKEN) &&
                req.getParams(Prams.TOKEN).getFirst().equals(Data.TOKEN)) {
            JSONObject json = new JSONObject();
            if (req.getParams().containsKey(Prams.PLAYER)) {
                json = BotCheckUtil.check(req.getParams(Prams.PLAYER).getFirst());
            } else {
                json.set(Prams.CODE, BAD_REQUEST);
            }
            res.write(json.toString(), ContentType.JSON.toString());
            return;
        }
        int code = 3;
        if (req.getHeaders().containsKey("RanmcToken") &&
                req.getHeader("RanmcToken").equals(TOKEN) &&
                req.getParams().containsKey(Prams.KEY)) {
            for (String player : BotCheckUtil.getBotCheckMap().keySet()) {
                BotCheckBean botCheckBean = BotCheckUtil.getBotCheckMap().get(player);
                String key = botCheckBean.getKey();
                if (!key.isEmpty() && req.getParams(Prams.KEY).getFirst().equals(key)) {
                    if (botCheckBean.isPass()) {
                        code = 2;
                    } else {
                        botCheckBean.setAddress(req.getClientIP("X-Real-IP"));
                        botCheckBean.setAgent(req.getUserAgentStr());
                        botCheckBean.setPass(true);
                        code = 1;
                    }
                    break;
                }
            }
        }
        res.write("<html><head><meta http-equiv=\"refresh\" content=\"0;url=https://www.ranmc.cc/check.html?result=" + code + "\"></head><body></body></html>", ContentType.TEXT_HTML.toString());
    }
}

