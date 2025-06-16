package cc.ranmc.server.network;

import io.javalin.http.Context;

public class BaseHandler {
    public static void handle(Context context) {
        context.contentType("text/html");
        context.result("<html><head><meta http-equiv=\"refresh\" content=\"0;url=https://www.ranmc.cc\"></head><body></body></html>");
    }
}
