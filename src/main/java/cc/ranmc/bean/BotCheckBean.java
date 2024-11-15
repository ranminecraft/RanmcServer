package cc.ranmc.bean;

import lombok.Data;

import java.util.Date;

@Data
public class BotCheckBean {
    private String player;
    private String key = "";
    private String address = "";
    private String agent = "";
    private long time = new Date().getTime() + (60 * 1000);
    private boolean pass = false;
}
