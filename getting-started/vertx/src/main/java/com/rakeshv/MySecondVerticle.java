package com.rakeshv;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MySecondVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(
            config -> {
                if (config.failed()) {
                    future.fail(config.cause());
                } else {
                    vertx
                        .createHttpServer()
                        .requestHandler(r ->
                            r.response().end("<h1>" + config.result().getString("message") + "</h1>"))
                            .listen(config.result().getInteger("HTTP_PORT", 8082), result -> {
                                if (result.succeeded()) {
                                    future.complete();
                                } else {
                                    future.fail(result.cause());
                                }
                            });
                }
            }
        );
    }
}