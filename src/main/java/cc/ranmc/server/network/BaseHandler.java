package cc.ranmc.server.network;

import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;

public class BaseHandler {
    public void handle(HttpServerRequest req, HttpServerResponse res) {
        res.write("<html><head><meta http-equiv=\"refresh\" content=\"0;url=https://www.ranmc.cc\"></head><body></body></html>", ContentType.TEXT_HTML.toString());
    }
}
