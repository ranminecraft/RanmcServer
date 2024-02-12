package cc.ranmc.util;

import cc.ranmc.bean.Confirm;
import cc.ranmc.constant.Code;
import cc.ranmc.constant.Email;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfirmUtil {

    @Getter
    private static final Map<String, Confirm> confirmMap = new HashMap<>();
    
    public static int check(String player, String qq, String mode) {
        String action = "0".equals(mode) ? "绑定游戏账号" : "重置游戏密码";
        if (confirmMap.containsKey(qq)) {
            Confirm confirm = confirmMap.get(qq);
            if (confirm.isPass()) {
                confirmMap.remove(qq);
                Logger.info("成功" + action + "：" + player + "(" + qq + ")");
                return Code.SUCCESS;
            } else if (confirm.getTime() < new Date().getTime()) {
                confirmMap.remove(qq);
                Logger.info("等待确认超时：" + qq);
                return Code.TIME_OUT;
            }
        } else {
            String key = KeyGenerator.get();
            Confirm confirm = new Confirm();
            confirm.setMode(mode);
            confirm.setPlayer(player);
            confirm.setKey(key);
            confirmMap.put(qq, confirm);
            String url = "http://43.248.185.80:2263/point?mode=verify&key=" + key;
            try {
                OhMyEmail.subject("【桃花源】" + action + "确认")
                        .from("ranica@qq.com")
                        .to(qq + "@qq.com")
                        .html(Email.text
                                .replace("%player%", player)
                                .replace("%url%", url)
                                .replace("%action%", action))
                        .send();
            } catch (SendMailException e) {
                Logger.info(e.getMessage());
                Logger.info("发送邮件失败");
                return Code.ERROR;
            }
        }
        return Code.WAITING;
    }
}
