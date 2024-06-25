package cc.ranmc.network;

import cc.ranmc.bean.Confirm;
import cc.ranmc.constant.Prams;
import cc.ranmc.util.ConfirmUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;

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

        String result = "超时或不存在，请重新验证。";
        for (String qq : ConfirmUtil.getConfirmMap().keySet()) {
            Confirm confirm = ConfirmUtil.getConfirmMap().get(qq);
            String key = confirm.getKey();
            if (!key.isEmpty() && req.getParams(Prams.KEY).getFirst().equals(key)) {
                if (confirm.isPass()) {
                    result = "已确认，请勿重复操作。";
                } else {
                    confirm.setPass(true);
                    result = "验证成功，请在游戏内查看结果。";
                }
                break;
            }
        }
        res.write(result, ContentType.TEXT_PLAIN.toString());
    }
}

