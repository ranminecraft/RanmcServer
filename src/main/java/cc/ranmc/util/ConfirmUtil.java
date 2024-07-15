package cc.ranmc.util;

import cc.ranmc.Main;
import cc.ranmc.bean.Confirm;
import cc.ranmc.constant.Code;
import cc.ranmc.constant.Email;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cc.ranmc.constant.Data.VERIFY_WEB_SITE;

public class ConfirmUtil {

    @Getter
    private static final Map<String, Confirm> confirmMap = new HashMap<>();
    
    public static int check(String player, String email, String mode) {
        String action = "0".equals(mode) ? "绑定游戏账号" : "重置游戏密码";
        if (confirmMap.containsKey(email)) {
            Confirm confirm = confirmMap.get(email);
            if (confirm.isPass()) {
                confirmMap.remove(email);
                Main.getLogger().info("成功{}：{}({})", action, player, email);
                return Code.SUCCESS;
            } else if (confirm.getTime() < new Date().getTime()) {
                confirmMap.remove(email);
                Main.getLogger().info("等待确认超时：{}", email);
                return Code.TIME_OUT;
            }
        } else {
            String key = KeyGenerator.get();
            Confirm confirm = new Confirm();
            confirm.setMode(mode);
            confirm.setPlayer(player);
            confirm.setKey(key);
            confirmMap.put(email, confirm);
            String url = VERIFY_WEB_SITE + key;
            try {
                OhMyEmail.subject(action + "确认")
                        .from("【桃花源】")
                        .to(email)
                        .html(Email.text
                                .replace("%player%", player)
                                .replace("%url%", url)
                                .replace("%action%", action))
                        .send();
                Main.getLogger().error("发送邮件成功:{}", email);
            } catch (SendMailException e) {
                Main.getLogger().error(e.getMessage());
                Main.getLogger().error("发送邮件失败:{}", email);
                return Code.ERROR;
            }
        }
        return Code.WAITING;
    }
}
