package cc.ranmc.server.network;

import cc.ranmc.server.Main;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AuthHandler extends BaseHandler {

    @Override
    public void handle(HttpServerRequest req, HttpServerResponse res) {
        JSONObject json = new JSONObject();
        Main.getLogger().info("{} {}\nparams {}\nbody {}", req.getMethod(), req.getURI(), req.getParams(), req.getBody());

        if (req.getMethod().equals("GET")) {
            // 验证是否进入服务器
            if (req.getURI().toString().startsWith("/auth/hasJoined")) {
                res.write(getProfile("Ranica").toString(), ContentType.JSON.toString());
                return;
            }

        } else if (req.getMethod().equals("POST")) {

            if (req.getBody() == null ||
                    req.getBody().isEmpty() ||
                    !req.getBody().startsWith("{") ||
                    !req.getBody().endsWith("}")) {
                Main.getLogger().info("{}认证:空请求", req.getClientIP("X-Real-IP"));
                res.write(getErrorRes("错误请求"), ContentType.JSON.toString());
                return;
            }
            JSONObject params = null;
            try {
                params = new JSONObject(req.getBody());
            } catch (Exception ignored) {}
            if (params == null) {
                Main.getLogger().info("{}认证:错误请求", req.getClientIP("X-Real-IP"));
                res.write(getErrorRes("错误请求"), ContentType.JSON.toString());
                return;
            }

            // 登陆
            if ("/auth/authserver/authenticate".equalsIgnoreCase(req.getURI().toString())) {
                String name = params.getStr("username", "");
                if (name.isEmpty()) {
                    Main.getLogger().info("{}认证:空玩家名", req.getClientIP("X-Real-IP"));
                    res.write(getErrorRes("玩家名不能为空"), ContentType.JSON.toString());
                    return;
                }
                String password = params.getStr("password", "");
                if (password.isEmpty()) {
                    Main.getLogger().info("{}认证:空密码", req.getClientIP("X-Real-IP"));
                    res.write(getErrorRes("密码不能为空"), ContentType.JSON.toString());
                    return;
                }
                String clientToken = params.getStr("clientToken", "");
                if (clientToken.isEmpty()) {
                    Main.getLogger().info("{}认证:空客户端令牌", req.getClientIP("X-Real-IP"));
                    res.write(getErrorRes("客户端令牌不能为空"), ContentType.JSON.toString());
                    return;
                }

                String id = String.valueOf(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)));
                json.set("user", getProfile(name));

                json.set("clientToken", clientToken);
                json.set("accessToken", clientToken);

                JSONArray profileArray = new JSONArray();
                JSONObject profile = new JSONObject();
                profile.set("name", name);
                profile.set("id", id);
                profileArray.add(profile);
                json.set("availableProfiles", profileArray);
                json.set("selectedProfile", profile);

                res.write(json.toString(), ContentType.JSON.toString());
                Main.getLogger().info("{}认证:成功登陆{}", req.getClientIP("X-Real-IP"), json);
                return;
            }

            // 验证令牌有效
            if ("/auth/sessionserver/session/minecraft/join".equalsIgnoreCase(req.getURI().toString()) ||
                    "/auth/authserver/validate".equalsIgnoreCase(req.getURI().toString())) {
                if (true) {
                    res.send(204, -1);
                } else {
                    res.send(403);
                    res.write(getErrorRes("令牌无效"), ContentType.JSON.toString());
                }
                return;
            }

        }

        Main.getLogger().info("{}认证:未知请求", req.getClientIP("X-Real-IP"));
        res.write(getErrorRes("未知请求"), ContentType.JSON.toString());
    }

    private static String getErrorRes(String error) {
        JSONObject json = new JSONObject();
        json.set("error", error);
        json.set("cause", error);
        json.set("errorMessage", error);
        return json.toString();
    }

    private static JSONObject getProfile(String name) {
        String id = String.valueOf(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)));
        JSONObject json = new JSONObject();
        json.set("id", id);
        json.set("username", name);
        json.set("name", name);
        JSONArray properties = new JSONArray();
        /*JSONObject textures = new JSONObject();
        textures.set("name", "textures");
        textures.set("signature", "FkgjIEXg+Mo0PIxUciGbFvztrGqFHKE4sZphlmUbR4l91Ddl84f97rKpFROBnamZ/Jz4tmN8C7ljexq7DgYpG+zy2cE5GmH0DZhfXXmPZyXI169hEPTiDhB7xHaBCFiMqEUHikUoWZ2Ji+HrCwhjTOP0WwZ3LZz4fNDyFkDZNUt5djFNdtWRzvC4fWFAY44FXvKDkCRnvreBmWfRU73NV+ZCTFXdgDG9f+yn3c+VH6vOCf42VnsDTbXSqPMmKdh3EAI2r4wHqV2Q7bDD5oioObj02gU/0QOQcF0o6ZUC0jcRbeh4aQld40jJfBKqZjKAHGZ2N7+txDDhSG0T9YQeUgUPcp5LaNyS3BgyoJl7klGmoBWTQIBodnSEKVpRuSznRsJRp+QGKaZgJXbJlWP71vdqkPAGo9CFhCol3sWP6MzrtfkG2IBon1bLGK+UIzp38/4lYfy8Q+AMsvj0BIswOrdLTtsJHsFF7WYrJjjVnm59fTjUrykt4EJBIg9f66UdxCn7/41i07ZInCTayuL4B9fSueKETFKSqychzq2iby1n190uDQp4D/fvUfkY+1ArA3u2fZK9cDB0D36/feiXgtEEwaKPXevrmW8syJqsYq3E25GlLEzWsmuccVO9WmfzlUiaa+oaDXr9ihwq5QkdI0i9q6YPH6PWXIu2SiQQyxA=");
        textures.set("value", "ewogICJ0aW1lc3RhbXAiIDogMTcwMDA5MTcxNzM1OCwKICAicHJvZmlsZUlkIiA6ICJmZjIwZjY5ZTVmNmM0NzZjOGEyMTVkMjg2MDg1OTZhYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYW5pY2EiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM0YTI3ZmM3NWQ0ODY4YmNjZjlmZWY0OGIyYzA5ZDFlOTNmNmJhYzQ0YzMyYzBmZjQ2YjViN2MwN2FjODIxZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FmZDU1M2IzOTM1OGEyNGVkZmUzYjhhOWE5MzlmYTVmYTRmYWE0ZDlhOWMzZDZhZjhlYWZiMzc3ZmEwNWMyYmIiCiAgICB9CiAgfQp9");
        properties.add(textures);*/
        json.set("properties", properties);
        Main.getLogger().info("getProfile {}", json.toString());
        return json;
    }

}
