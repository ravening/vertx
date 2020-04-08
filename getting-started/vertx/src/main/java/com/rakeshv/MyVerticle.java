package com.rakeshv;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MyVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        vertx
            .createHttpServer()
            .requestHandler(r ->
                r.response().end("<h1>Hello from my first Vert.x application</h1>"))
                .listen(8081, result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }
}