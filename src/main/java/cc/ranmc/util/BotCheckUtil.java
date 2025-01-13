package cc.ranmc.util;

import cc.ranmc.Main;
import cc.ranmc.bean.BotCheckBean;
import cc.ranmc.constant.Code;
import cc.ranmc.constant.Prams;
import cn.hutool.json.JSONObject;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cc.ranmc.constant.Data.BOT_CHECK_WEB_SITE;

public class BotCheckUtil {

    @Getter
    private static final Map<String, BotCheckBean> botCheckMap = new HashMap<>();
    
    public static JSONObject check(String player) {
        JSONObject json = new JSONObject();
        if (botCheckMap.containsKey(player)) {
            BotCheckBean botCheckBean = botCheckMap.get(player);
            if (botCheckBean.isPass()) {
                botCheckMap.remove(player);
                Main.getLogger().info("{}通过人机验证({})",
                        player,
                        botCheckBean.getAddress());
                json.set(Prams.CODE, Code.SUCCESS);
            } else if (botCheckBean.getTime() < new Date().getTime()) {
                botCheckMap.remove(player);
                Main.getLogger().info("{}人机验证超时", player);
                json.set(Prams.CODE, Code.TIME_OUT);
            } else {
                json.set(Prams.URL, BOT_CHECK_WEB_SITE + botCheckBean.getKey());
                json.set(Prams.CODE, Code.WAITING);
            }
        } else {
            String key = KeyGenerator.get();
            BotCheckBean botCheckBean = new BotCheckBean();
            botCheckBean.setPlayer(player);
            botCheckBean.setKey(key);
            botCheckMap.put(player, botCheckBean);
            json.set(Prams.URL, BOT_CHECK_WEB_SITE + key);
            json.set(Prams.CODE, Code.WAITING);
        }
        return json;
    }
}
