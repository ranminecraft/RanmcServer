package cc.ranmc.network;

import cc.ranmc.bean.Confirm;
import cc.ranmc.constant.Code;
import cc.ranmc.constant.Data;
import cc.ranmc.constant.Prams;
import cc.ranmc.entries.Point;
import cc.ranmc.util.ConfirmUtil;
import cc.ranmc.util.Logger;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONObject;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;

import java.io.UnsupportedEncodingException;

public class PointHandler {

    private final Point point = new Point();

    public void handle(HttpServerRequest req, HttpServerResponse res) throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        if (!req.getParams().containsKey(Prams.MODE)) {
            json.set(Prams.CODE, Code.UNKOWN_REQUEST);
            res.write(json.toString(), ContentType.JSON.toString());
            return;
        }
        String mode = req.getParams(Prams.MODE).getFirst();
        if (mode.equals(Prams.VERIFY)) {
            String result = "超时或不存在，请重新验证。";
            for (String qq : ConfirmUtil.getConfirmMap().keySet()) {
                Confirm confirm = ConfirmUtil.getConfirmMap().get(qq);
                String key = confirm.getKey();
                if (!key.isEmpty() && req.getParams(Prams.KEY).getFirst().equals(key)) {
                    if (confirm.isPass()) {
                        result = "已确认，请勿重复操作。";
                    } else {
                        confirm.setPass(true);
                        result = "成功，请在游戏内查看结果。";
                    }
                    break;
                }
            }
            res.write(result.getBytes("GBK"), ContentType.TEXT_PLAIN.toString());
            return;
        }
        if (!req.getParams().containsKey(Prams.TOKEN) ||
                !req.getParams(Prams.TOKEN).getFirst().equals(Data.TOKEN)) {
            json.set(Prams.CODE, Code.NO_PERMISSION);
            res.write(json.toString(), ContentType.JSON.toString());
            return;
        }
        switch (mode) {
            case Prams.POINT -> {
                json.set(Prams.CODE, Code.SUCCESS);
                json.set(Prams.POINT, point.check(req.getParams(Prams.QQ).getFirst()));
                json.set(Prams.RANK, point.getRank(req.getParams(Prams.QQ).getFirst()));
                Logger.info("查询积分" + req.getParams(Prams.QQ).getFirst() + "：" + json.get(Prams.POINT));
            }
            case Prams.TOP -> {
                json.set(Prams.CODE, Code.SUCCESS);
                json.set(Prams.MSG, point.getRankList());
                Logger.info("获取排行榜");
            }
            case Prams.SUB -> {
                if (point.sub(Long.parseLong(req.getParams(Prams.QQ).getFirst()),
                        Integer.parseInt(req.getParams(Prams.POINT).getFirst()))) {
                    json.set(Prams.CODE, Code.SUCCESS);
                } else {
                    json.set(Prams.CODE, Code.UNCHANGED);
                }
                Logger.info("消耗积分" + req.getParams(Prams.QQ).getFirst() + ":" + req.getParams(Prams.POINT));
            }
            case Prams.PLUS -> {
                point.plus(Long.parseLong(req.getParams(Prams.QQ).getFirst()),
                        Integer.parseInt(req.getParams(Prams.POINT).getFirst()));
                json.set(Prams.CODE, Code.SUCCESS);
                Logger.info("获得积分" + req.getParams(Prams.QQ).getFirst() + ":" + req.getParams(Prams.POINT));
            }
            case Prams.CONFIRM -> {
                if (req.getParams().containsKey(Prams.CODE) && req.getParams().containsKey(Prams.PLAYER)) {
                    json.set(Prams.CODE, ConfirmUtil.check(
                            req.getParams(Prams.PLAYER).getFirst(),
                            req.getParams(Prams.QQ).getFirst(),
                            req.getParams(Prams.CODE).getFirst()));
                } else {
                    json.set(Prams.CODE, 403);
                }
            }
            case Prams.BROADCAST -> {
                if (req.getParams().containsKey(Prams.MSG) && req.getParams().containsKey(Prams.QQ)) {
                    //String msg = URLDecoder.decode(map.get(Prams.MSG), StandardCharsets.UTF_8);
                    String msg = req.getParams(Prams.MSG).getFirst();
                    try {
                        OhMyEmail.subject("【桃花源】服务器消息")
                                .from("ranica@qq.com")
                                .to(req.getParams(Prams.QQ).getFirst() + "@qq.com")
                                .html(msg)
                                .send();
                    } catch (SendMailException e) {
                        Logger.info("发送邮件失败：" + e.getMessage());
                    }
                    json.set(Prams.CODE, Code.SUCCESS);
                    Logger.info("发出广播" + req.getParams(Prams.QQ).getFirst() + ":" + msg);
                } else {
                    json.set(Prams.CODE, Code.UNKOWN_REQUEST);
                }
            }
            default -> json.set(Prams.CODE, Code.UNKOWN_REQUEST);
        }
        res.write(json.toString(), ContentType.JSON.toString());
    }
}

