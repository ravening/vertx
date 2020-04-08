package com.rakeshv.springbootvertx.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> promise) throws Exception {
        super.start();
        Router router = Router.router(vertx);
        router.get("/api/articles")
                .handler(this::getAllArticlesHandler);
        router.get("/api/articles/:message")
                .handler(this::getOneArticle);

        router.route("/").handler(rc -> {
            HttpServerResponse response = rc.response();
            response.putHeader("content-type", "text/html")
                    .end("<pre>" +
                            "<h1>Hello from the Spring Boot vertx web application</h1></pre>" +
                            "<h2>Please navigate to /api/articles</h2>");
        });

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(
            config -> {
                if (config.failed()) {
                    promise.fail(config.cause());
                } else {
                    vertx
                        .createHttpServer()
                        .requestHandler(router)
                        .listen(config.result().getInteger("HTTP_PORT", 8083),
                                result -> {
                                    if (result.succeeded()) {
                                        promise.complete();
                                    } else {
                                        promise.fail(result.cause());
                                    }
                                });
                }
            }
        );
    }

    private void getAllArticlesHandler(RoutingContext routingContext) {
        log.info("Sending request to fetch all articles");
        sendMessage(ArticleRecipientVerticle.GET_ALL_ARTICLES, "", routingContext);
    }

    private void getOneArticle(RoutingContext routingContext) {
        String message = routingContext.request().getParam("message");
        log.info("Requesting db to search for {}", message);
        sendMessage(ArticleRecipientVerticle.GET_ONE_ARTICLE, message, routingContext);

    }

    private void sendMessage(String messageType, String message, RoutingContext routingContext) {
        vertx.eventBus()
                .<String>send(messageType, message, result -> {
                    if (result.succeeded()) {
                        routingContext.response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(result.result().body());
                    } else {
                        routingContext.response()
                                .setStatusCode(500)
                                .end(result.result().body());
                    }
                });
    }
}
