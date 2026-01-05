package cc.ranmc.server.util;

import cc.ranmc.server.Main;
import cc.ranmc.server.minecraft.MinecraftPing;
import cc.ranmc.server.minecraft.MinecraftPingOptions;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static cc.ranmc.server.network.BroadcastHandler.broadcast;

public class MinecraftUtil {

    @Getter
    private static Map<String,Boolean> serverStatusMap = new TreeMap<>();
    private static final Map<String,String> serverSrvMap = new TreeMap<>();
    private static long recordId = 0;
    @Getter
    private static long lastCheckTime = 0;
    private static boolean lastCheckStatus = true;

    public static void updateServerStatus() {
        HttpUtil.post("https://dnsapi.cn/Record.List",
                "login_token=" + ConfigUtil.CONFIG.getString("dnspod") + "&domain=ranmc.cc&format=json&length=3000",
                body -> {
                    if (!body.startsWith("{")) {
                        Main.getLogger().warn("获取记录列表失败");
                        return;
                    }
                    serverSrvMap.clear();
                    Map<String,Boolean> newServerStatusMap = new TreeMap<>();
                    JSONObject.parseObject(body).getJSONArray("records").forEach(record -> {
                        JSONObject json = JSONObject.parseObject(record.toString());
                        String name = json.getString("name");
                        String srv = json.getString("value");
                        if (name.startsWith("_minecraft._tcp.")
                                && !name.contains("test")
                                && !name.contains("city")) {
                            String serverName = name.replace("_minecraft._tcp.", "") + ".ranmc.cc";
                            newServerStatusMap.put(serverName, isServerOnline(srv));
                            serverSrvMap.put(serverName, srv);
                        } else if (name.equals("_minecraft._tcp")) {
                            serverSrvMap.put("ranmc.cc", srv);
                            recordId = json.getLong("id");
                        }
                    });

                    String mainSrv = ConfigUtil.CONFIG.getString("srv");
                    boolean mainServerOnline = isServerOnline(mainSrv);
                    newServerStatusMap.put("ranmc.cc", mainServerOnline);
                    mainSrv += ".";
                    if (mainServerOnline && !serverSrvMap.get("ranmc.cc").equals(mainSrv)) {
                        modifyRecord(mainSrv);
                        broadcast("主线已恢复,更新解析记录 " + mainSrv);
                    }

                    if (!mainServerOnline && serverSrvMap.get("ranmc.cc").equals(mainSrv) && !lastCheckStatus) {
                        String backupSrv = "";
                        for (String key : newServerStatusMap.keySet()) {
                            if (newServerStatusMap.get(key)) {
                                backupSrv = serverSrvMap.get(key);
                            }
                        }
                        String backupServerInfo = "无备用线路可用";
                        if (!backupSrv.isEmpty()) {
                            backupServerInfo = "切换到备用线路 " + backupSrv;
                            modifyRecord(backupSrv);
                        }
                        broadcast("检测到主线路离线," + backupServerInfo);
                    }

                    lastCheckStatus = mainServerOnline;
                    lastCheckTime = System.currentTimeMillis();
                    serverStatusMap = newServerStatusMap;
                });
    }

    private static void modifyRecord(String value) {
        HttpUtil.post("https://dnsapi.cn/Record.Modify",
                "login_token=" + ConfigUtil.CONFIG.getString("dnspod") +
                        "&domain=ranmc.cc&sub_domain=_minecraft._tcp&record_type=SRV&record_line_id=0&value=" + value + "&record_id=" + recordId,
                body -> {
                    if (!body.startsWith("{")) {
                        Main.getLogger().warn("修改记录列表失败");
                        return;
                    }
                    Main.getLogger().warn("修改主线记录 {} 结果{}", value,
                            unicode(JSONObject.parseObject(body).getJSONObject("status").getString("message")));
                });
    }

    private static String unicode(String unicode) {
        Properties p = new Properties();
        try {
            p.load(new java.io.StringReader("key=" + unicode));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p.getProperty("key");
    }

    private static boolean isServerOnline(String srvValue) {
        String[] srvValueSplit = srvValue.split(" ");
        return isServerOnline(srvValueSplit[3], Integer.parseInt(srvValueSplit[2]));
    }

    private static boolean isServerOnline(String address, int port) {
        try {
            new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(address).setPort(port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
