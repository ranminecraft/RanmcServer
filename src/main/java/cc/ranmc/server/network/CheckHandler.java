package cc.ranmc.server.network;

import cc.ranmc.server.bean.BotCheckBean;
import cc.ranmc.server.constant.Data;
import cc.ranmc.server.constant.Prams;
import cc.ranmc.server.util.BotCheckUtil;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.http.ContentType;
import io.javalin.http.Context;

import static cc.ranmc.server.constant.Code.BAD_REQUEST;
import static cc.ranmc.server.constant.Data.TOKEN;

public class CheckHandler {

    public static void handle(Context context) {

        // 允许跨域
        context.header("Access-Control-Allow-Origin", "*");
        context.header("Access-Control-Allow-Methods", "*");
        context.header("Access-Control-Allow-Headers", "*");
        context.header("Access-Control-Max-Age", "*");
        context.header("Access-Control-Allow-Credentials", "true");

        context.contentType(ContentType.APPLICATION_JSON);

        if (context.queryParamMap().containsKey(Prams.TOKEN) &&
                Data.TOKEN.equals(context.queryParam(Prams.TOKEN))) {
            JSONObject json = new JSONObject();
            if (context.queryParamMap().containsKey(Prams.PLAYER)) {
                json = BotCheckUtil.check(context.queryParam(Prams.PLAYER));
            } else {
                json.put(Prams.CODE, BAD_REQUEST);
            }
            context.result(json.toString());
            return;
        }
        int code = 3;
        if (context.header("RanmcToken") != null &&
                TOKEN.equals(context.header("RanmcToken")) &&
                context.queryParamMap().containsKey(Prams.KEY)) {
            for (String player : BotCheckUtil.getBotCheckMap().keySet()) {
                BotCheckBean botCheckBean = BotCheckUtil.getBotCheckMap().get(player);
                String key = botCheckBean.getKey();
                if (!key.isEmpty() && key.equals(context.queryParam(Prams.KEY))) {
                    if (botCheckBean.isPass()) {
                        code = 2;
                    } else {
                        botCheckBean.setAddress(context.header("X-Real-IP"));
                        botCheckBean.setAgent(context.header("user-agent"));
                        botCheckBean.setPass(true);
                        code = 1;
                    }
                    break;
                }
            }
        }
        context.contentType(ContentType.TEXT_HTML);
        context.result("<html><head><meta http-equiv=\"refresh\" content=\"0;url=https://www.ranmc.cc/check.html?result=" + code + "\"></head><body></body></html>");
    }
}

