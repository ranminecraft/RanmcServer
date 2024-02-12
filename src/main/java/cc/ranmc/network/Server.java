package cc.ranmc.network;

import cc.ranmc.bean.Confirm;
import cc.ranmc.constant.Code;
import cc.ranmc.constant.Data;
import cc.ranmc.constant.Prams;
import cc.ranmc.entries.Point;
import cc.ranmc.util.ConfirmUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static cc.ranmc.constant.Data.HTTP_PATH;

public class Server implements HttpHandler {

    private final Point point = new Point();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        JSONObject json = new JSONObject();
        Map<String,String> map = new HashMap<>();
        String[] args = exchange.getRequestURI().toString().replace(HTTP_PATH + "?", "").split("&");
        for (String arg : args) {
            String[] prams = arg.split("=");
            map.put(prams[0], prams[1]);
        }
        if (!map.containsKey(Prams.MODE)) {
            json.put(Prams.CODE, Code.UNKOWN_REQUEST);
            os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }
        if (map.get(Prams.MODE).equals(Prams.VERIFY)) {
            String result = "超时或不存在，请重新验证。";
            for (String qq : ConfirmUtil.getConfirmMap().keySet()) {
                Confirm confirm = ConfirmUtil.getConfirmMap().get(qq);
                String key = confirm.getKey();
                if (!key.isEmpty() && map.get(Prams.KEY).equals(key)) {
                    if (confirm.isPass()) {
                        result = "已确认，请勿重复操作。";
                    } else {
                        confirm.setPass(true);
                        result = "成功，请在游戏内查看结果。";
                    }
                    break;
                }
            }
            os.write(result.getBytes("GBK"));
            os.close();
            return;
        }
        if (!map.get(Prams.TOKEN).equals(Data.TOKEN)) {
            json.put(Prams.CODE, Code.NO_PERMISSION);
            os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }
        switch (map.get(Prams.MODE)) {
            case Prams.POINT -> {
                json.put(Prams.CODE, Code.SUCCESS);
                json.put(Prams.POINT, point.check(map.get(Prams.QQ)));
                json.put(Prams.RANK, point.getRank(map.get(Prams.QQ)));
                Logger.info("查询积分" + map.get(Prams.QQ) + "：" + json.get(Prams.POINT));
            }
            case Prams.TOP -> {
                json.put(Prams.CODE, Code.SUCCESS);
                json.put(Prams.MSG, point.getRankList());
                Logger.info("获取排行榜");
            }
            case Prams.SUB -> {
                if (point.sub(Long.parseLong(map.get(Prams.QQ)), Integer.parseInt(map.get(Prams.POINT)))) {
                    json.put(Prams.CODE, Code.SUCCESS);
                } else {
                    json.put(Prams.CODE, Code.UNCHANGED);
                }
                Logger.info("消耗积分" + map.get(Prams.QQ) + ":" + map.get(Prams.POINT));
            }
            case Prams.PLUS -> {
                point.plus(Long.parseLong(map.get(Prams.QQ)), Integer.parseInt(map.get(Prams.POINT)));
                json.put(Prams.CODE, Code.SUCCESS);
                Logger.info("获得积分" + map.get(Prams.QQ) + ":" + map.get(Prams.POINT));
            }
            case Prams.CONFIRM -> {
                if (map.containsKey(Prams.CODE) && map.containsKey(Prams.PLAYER)) {
                    json.put(Prams.CODE, ConfirmUtil.check(map.get(Prams.PLAYER), map.get(Prams.QQ), map.get(Prams.CODE)));
                } else {
                    json.put(Prams.CODE, 403);
                }
            }
            case Prams.BROADCAST -> {
                if (map.containsKey(Prams.MSG) && map.containsKey(Prams.QQ)) {
                    String msg = URLDecoder.decode(map.get(Prams.MSG), StandardCharsets.UTF_8);
                    try {
                        OhMyEmail.subject("【桃花源】服务器消息")
                                .from("ranica@qq.com")
                                .to(map.get(Prams.QQ) + "@qq.com")
                                .html(msg)
                                .send();
                    } catch (SendMailException e) {
                        Logger.info("发送邮件失败：" + e.getMessage());
                    }
                    json.put(Prams.CODE, Code.SUCCESS);
                    Logger.info("发出广播" + map.get(Prams.QQ) + ":" + msg);
                } else {
                    json.put(Prams.CODE, Code.UNKOWN_REQUEST);
                }
            }
            default -> json.put(Prams.CODE, Code.UNKOWN_REQUEST);
        }

        os.write(json.toString().getBytes("GBK"));
        os.close();
    }
}

