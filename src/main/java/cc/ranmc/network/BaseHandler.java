package cc.ranmc.network;

import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;

public class BaseHandler {
    public void handle(HttpServerRequest req, HttpServerResponse res) {
        res.write("ranmc.cc", ContentType.TEXT_PLAIN.toString());
    }
}
