package cc.ranmc.network;

import cc.ranmc.constant.Code;
import cc.ranmc.constant.Prams;
import cc.ranmc.entries.SQLite;
import cc.ranmc.util.DataFile;
import cc.ranmc.util.Logger;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BanlistHandler {

    private final SQLite data = new SQLite(DataFile.read("sqlite"));
    private int lastUpdate = -1;
    private List<JSONObject> banlist;
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
        Logger.info(req.getClientIP("X-Real-IP") + "请求封禁列表");

        JSONObject json = new JSONObject();

        // 检查请求
        if (!req.getMethod().equals("GET") || !req.getParams().containsKey(Prams.PAGE)) {
            json.set(Prams.CODE, Code.UNKOWN_REQUEST);
            res.send(Code.UNKOWN_REQUEST);
            res.write(json.toString(), ContentType.JSON.toString());
            return;
        }
        // 获取页数
        int page = 1;
        try {
            page = Integer.parseInt(req.getParams(Prams.PAGE).getFirst());
            if (page < 1) page = 1;
        } catch (NumberFormatException ignore) {}
        updateBanlist();
        List<JSONObject> list = new ArrayList<>();
        // 过滤玩家名字
        if (req.getParams().containsKey(Prams.PLAYER)) {
            banlist.forEach(obj -> {
                String searchName = req.getParams(Prams.PLAYER).getFirst();
                if (searchName != null &&
                        !searchName.isEmpty() &&
                        obj.getStr("player").toLowerCase().
                                contains(searchName.toLowerCase())) {
                    list.add(obj);
                }
            });
        } else {
            list.addAll(banlist);
        }
        // 是否倒叙
        if (req.getParams().containsKey(Prams.SORT_ORDER) &&
                req.getParams(Prams.SORT_ORDER).getFirst()
                        .equalsIgnoreCase("desc")) {
            Collections.reverse(list);
        }
        // 获取单页显示数量
        int limit = 30;
        if (req.getParams().containsKey(Prams.LIMIT)) {
            try {
                limit = Integer.parseInt(req.getParams(Prams.LIMIT).getFirst());
            } catch (NumberFormatException ignore) {}
        }
        if (limit > 50) limit = 50;
        // 返回结果
        res.sendOk();
        json.set(Prams.CODE, Code.SUCCESS);
        json.set(Prams.TOTAL, list.size());
        json.set(Prams.TOTAL_NOT_FILTERED, banlist.size());
        JSONArray array = new JSONArray();
        page = (page - 1) * limit;
        for (int i = page; i < page + limit; i++) {
            if (i >= list.size()) break;
            array.put(list.get(i));
        }
        json.set(Prams.ROWS, array);
        res.write(json.toString(), ContentType.JSON.toString());
    }

    /**
     * 更新列表
     */
    private void updateBanlist() {
        int day = LocalDateTime.now().getDayOfYear();
        if (lastUpdate == day) return;
        lastUpdate = day;
        banlist = new ArrayList<>();
        AtomicInteger id = new AtomicInteger();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data.findList("BANLIST").forEach(map -> {
            id.getAndIncrement();
            JSONObject json = new JSONObject();
            json.set("player", map.get("Player"));
            json.set("reason", map.get("Reason"));
            json.set("banTime", map.get("Date"));
            json.set("releaseTime", format.format(new Date(Long.parseLong(map.get("Time")))));
            json.set("operator", map.get("Admin"));
            json.set("id", id.get());
            banlist.add(json);
        });
    }

}
