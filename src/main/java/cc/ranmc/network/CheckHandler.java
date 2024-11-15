package cc.ranmc.network;

import cc.ranmc.bean.BotCheckBean;
import cc.ranmc.constant.Data;
import cc.ranmc.constant.Prams;
import cc.ranmc.util.BotCheckUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONObject;

import static cc.ranmc.constant.Code.BAD_REQUEST;

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

        String result = "超时或不存在，请重新验证。";
        for (String player : BotCheckUtil.getBotCheckMap().keySet()) {
            BotCheckBean botCheckBean = BotCheckUtil.getBotCheckMap().get(player);
            String key = botCheckBean.getKey();
            if (!key.isEmpty() && req.getParams(Prams.KEY).getFirst().equals(key)) {
                if (botCheckBean.isPass()) {
                    result = "已确认，请勿重复操作。";
                } else {
                    botCheckBean.setAddress(req.getClientIP("X-Real-IP"));
                    botCheckBean.setAgent(req.getHeader("user-agent"));
                    botCheckBean.setPass(true);
                    result = "验证成功，请在游戏内查看结果。";
                }
                break;
            }
        }

        res.write(result, ContentType.TEXT_PLAIN.toString());
    }
}

