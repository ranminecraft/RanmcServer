package cc.ranmc.bean;

import lombok.Data;

import java.util.Date;

@Data
public class VerifyBean {
    private String player;
    private String mode;
    private String key = "";
    private long time = new Date().getTime() + (10 * 60 * 1000);
    private boolean pass = false;
}
