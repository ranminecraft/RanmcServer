package cc.ranmc.server.network;

import cc.ranmc.server.bean.VerifyBean;
import cc.ranmc.server.constant.Data;
import cc.ranmc.server.constant.Prams;
import cc.ranmc.server.util.VerifyUtil;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.Objects;

import static cc.ranmc.server.constant.Code.BAD_REQUEST;

public class VerifyHandler {

    public static void handle(Context context) {

        // 允许跨域
        context.header("Access-Control-Allow-Origin", "*");
        context.header("Access-Control-Allow-Methods", "*");
        context.header("Access-Control-Allow-Headers", "*");
        context.header("Access-Control-Max-Age", "*");
        context.header("Access-Control-Allow-Credentials", "true");
        if (HandlerType.OPTIONS == context.method()) {
            context.status(200);
            return;
        }
        context.contentType("application/json");

        if (context.queryParamMap().containsKey(Prams.TOKEN) &&
                Data.TOKEN.equals(context.queryParam(Prams.TOKEN))) {
            JSONObject json = new JSONObject();
            if (context.queryParamMap().containsKey(Prams.EMAIL) &&
                    context.queryParamMap().containsKey(Prams.MODE) &&
                    context.queryParamMap().containsKey(Prams.PLAYER)) {
                json.put(Prams.CODE, VerifyUtil.check(
                        context.queryParam(Prams.PLAYER),
                        context.queryParam(Prams.EMAIL),
                        Objects.requireNonNull(context.queryParam(Prams.MODE))));
            } else {
                json.put(Prams.CODE, BAD_REQUEST);
            }
            context.result(json.toString());
            return;
        }

        String result = "超时或不存在，请重新验证。";
        for (String qq : VerifyUtil.getVerifyMap().keySet()) {
            VerifyBean verifyBean = VerifyUtil.getVerifyMap().get(qq);
            String key = verifyBean.getKey();
            if (!key.isEmpty() && key.equals(context.queryParam(Prams.KEY))) {
                if (verifyBean.isPass()) {
                    result = "已确认，请勿重复操作。";
                } else {
                    verifyBean.setPass(true);
                    result = "验证成功，请在游戏内查看结果。";
                }
                break;
            }
        }
        context.contentType("text/plain");
        context.result(result);
    }
}

