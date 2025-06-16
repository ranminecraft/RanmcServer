package cc.ranmc.server.network;

import cc.ranmc.server.Main;
import cc.ranmc.server.constant.Code;
import cc.ranmc.server.constant.Prams;
import cc.ranmc.server.util.DataFile;
import cc.ranmc.sqlite.SQLite;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class BanlistHandler {

    private static final SQLite data = new SQLite(DataFile.read("sqlite"));
    private static int lastUpdate = -1;
    private static List<JSONObject> banlist;

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
        Main.getLogger().info("{}请求封禁列表",
                context.header("X-Real-IP"));

        JSONObject json = new JSONObject();

        // 检查请求
        if (HandlerType.GET != context.method() || !context.queryParamMap().containsKey(Prams.PAGE)) {
            json.put(Prams.CODE, Code.UNKOWN_REQUEST);
            context.status(Code.UNKOWN_REQUEST);
            context.result(json.toString());
            return;
        }
        // 获取页数
        int page = 1;
        try {
            page = Integer.parseInt(Objects.requireNonNull(context.queryParam(Prams.PAGE)));
            if (page < 1) page = 1;
        } catch (Exception ignore) {}
        updateBanlist();
        List<JSONObject> list = new ArrayList<>();
        // 过滤玩家名字
        if (context.queryParamMap().containsKey(Prams.PLAYER)) {
            banlist.forEach(obj -> {
                String searchName = context.queryParam(Prams.PLAYER);
                if (searchName != null &&
                        !searchName.isEmpty() &&
                        obj.getString("player").toLowerCase().
                                contains(searchName.toLowerCase())) {
                    list.add(obj);
                }
            });
        } else {
            list.addAll(banlist);
        }
        // 是否倒叙
        if (context.queryParamMap().containsKey(Prams.SORT_ORDER) &&
                "desc"
                        .equalsIgnoreCase(context.queryParam(Prams.SORT_ORDER))) {
            Collections.reverse(list);
        }
        // 获取单页显示数量
        int limit = 30;
        if (context.queryParamMap().containsKey(Prams.LIMIT)) {
            try {
                limit = Integer.parseInt(Objects.requireNonNull(context.queryParam(Prams.LIMIT)));
            } catch (Exception ignore) {}
        }
        if (limit > 50) limit = 50;
        // 返回结果
        context.status(200);
        json.put(Prams.CODE, Code.SUCCESS);
        json.put(Prams.TOTAL, list.size());
        json.put(Prams.TOTAL_NOT_FILTERED, banlist.size());
        JSONArray array = new JSONArray();
        page = (page - 1) * limit;
        for (int i = page; i < page + limit; i++) {
            if (i >= list.size()) break;
            array.add(list.get(i));
        }
        json.put(Prams.ROWS, array);
        context.result(json.toString());
    }

    /**
     * 更新列表
     */
    private static void updateBanlist() {
        int day = LocalDateTime.now().getDayOfYear();
        if (lastUpdate == day) return;
        lastUpdate = day;
        banlist = new ArrayList<>();
        AtomicInteger id = new AtomicInteger();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data.selectList("BANLIST").forEach(map -> {
            id.getAndIncrement();
            JSONObject json = new JSONObject();
            json.put("player", map.get("Player"));
            json.put("reason", map.get("Reason"));
            json.put("banTime", map.get("Date"));
            json.put("releaseTime", format.format(new Date(Long.parseLong(map.get("Time")))));
            json.put("operator", map.get("Admin"));
            json.put("id", id.get());
            banlist.add(json);
        });
    }

}
