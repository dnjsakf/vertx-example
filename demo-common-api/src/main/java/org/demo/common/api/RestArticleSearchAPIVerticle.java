package org.demo.common.api;

import org.demo.common.RestAPIVerticle;

import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.json.JsonObject;

public class RestArticleSearchAPIVerticle extends RestAPIVerticle {
    private static final String SERVICE_NAME = "board-api-search";
    public static JsonObject CONFIG = null;
    public static void main(String[] args) {
        try {

            Launcher launcher = new Launcher();
            String[] arg = { "run", "java:" + RestArticleSearchAPIVerticle.class.getCanonicalName() };
            launcher.dispatch(arg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        /**
         * create HTTP server and publish REST service
         */
        CONFIG = config();
        String host = CONFIG.getString("api.search.http.address", "0.0.0.0");
        int port = CONFIG.getInteger("api.search.http.port", 8083);
        createHttpServer(router, host, port).compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future.completer());

    }
}
