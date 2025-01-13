package cc.ranmc.server.network;

import cc.ranmc.server.bean.VerifyBean;
import cc.ranmc.server.constant.Data;
import cc.ranmc.server.constant.Prams;
import cc.ranmc.server.util.VerifyUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONObject;

import static cc.ranmc.server.constant.Code.BAD_REQUEST;

public class VerifyHandler extends BaseHandler {

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
            if (req.getParams().containsKey(Prams.EMAIL) &&
                    req.getParams().containsKey(Prams.MODE) &&
                    req.getParams().containsKey(Prams.PLAYER)) {
                json.set(Prams.CODE, VerifyUtil.check(
                        req.getParams(Prams.PLAYER).getFirst(),
                        req.getParams(Prams.EMAIL).getFirst(),
                        req.getParams(Prams.MODE).getFirst()));
            } else {
                json.set(Prams.CODE, BAD_REQUEST);
            }
            res.write(json.toString(), ContentType.JSON.toString());
            return;
        }

        String result = "超时或不存在，请重新验证。";
        for (String qq : VerifyUtil.getVerifyMap().keySet()) {
            VerifyBean verifyBean = VerifyUtil.getVerifyMap().get(qq);
            String key = verifyBean.getKey();
            if (!key.isEmpty() && req.getParams(Prams.KEY).getFirst().equals(key)) {
                if (verifyBean.isPass()) {
                    result = "已确认，请勿重复操作。";
                } else {
                    verifyBean.setPass(true);
                    result = "验证成功，请在游戏内查看结果。";
                }
                break;
            }
        }

        res.write(result, ContentType.TEXT_PLAIN.toString());
    }
}

