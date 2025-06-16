package cc.ranmc.server.util;

import cc.ranmc.server.Main;
import cc.ranmc.server.bean.BotCheckBean;
import cc.ranmc.server.constant.Code;
import cc.ranmc.server.constant.Prams;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cc.ranmc.server.constant.Data.BOT_CHECK_WEB_SITE;

public class BotCheckUtil {

    @Getter
    private static final Map<String, BotCheckBean> botCheckMap = new HashMap<>();
    
    public static JSONObject check(String player) {
        JSONObject json = new JSONObject();
        if (botCheckMap.containsKey(player)) {
            BotCheckBean botCheckBean = botCheckMap.get(player);
            if (botCheckBean.isPass()) {
                botCheckMap.remove(player);
                Main.getLogger().info("{}({})通过人机验证",
                        player,
                        botCheckBean.getAddress());
                json.put(Prams.CODE, Code.SUCCESS);
            } else if (botCheckBean.getTime() < new Date().getTime()) {
                botCheckMap.remove(player);
                Main.getLogger().info("{}人机验证超时", player);
                json.put(Prams.CODE, Code.TIME_OUT);
            } else {
                json.put(Prams.URL, BOT_CHECK_WEB_SITE + botCheckBean.getKey());
                json.put(Prams.CODE, Code.WAITING);
            }
        } else {
            String key = KeyGenerator.get();
            BotCheckBean botCheckBean = new BotCheckBean();
            botCheckBean.setPlayer(player);
            botCheckBean.setKey(key);
            botCheckMap.put(player, botCheckBean);
            json.put(Prams.URL, BOT_CHECK_WEB_SITE + key);
            json.put(Prams.CODE, Code.WAITING);
        }
        return json;
    }
}
