package cc.ranmc.bean;

import lombok.Data;

import java.util.Date;

@Data
public class Confirm {
    private String player;
    private String mode;
    private String key = "";
    private long time = new Date().getTime() + (60 * 1000);
    private boolean pass = false;
}
