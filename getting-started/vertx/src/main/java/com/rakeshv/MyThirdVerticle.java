package com.rakeshv;

import com.rakeshv.models.Article;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyThirdVerticle extends AbstractVerticle {
    private Map readingList = new LinkedHashMap();

    @Override
    public void start(Promise<Void> promise) throws Exception {
        createSomeData();
        Router router = Router.router(vertx);
        router.route("/").handler(rc -> {
            HttpServerResponse response = rc.response();
            response.putHeader("content-type", "text/html")
                    .end("<pre>" +
                            "<h1>Hello from the vertx web application</h1></pre>");
        });

        router.route("/assets/*")
                .handler(StaticHandler.create("assets"));
        router.get("/api/articles").handler(this::getAll);
        router.get("/api/articles/:id").handler(this::getOne);
        router.route("/api/articles*").handler(BodyHandler.create());
        router.post("/api/articles").handler(this::addOne);
        router.delete("/api/articles/:id").handler(this::deleteOne);
        router.put("/api/articles/:id").handler(this::updateOne);

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(
                config -> {
                    if (config.failed()) {
                        promise.fail(config.cause());
                    } else {
                        vertx
                                .createHttpServer()
                                .requestHandler(router::accept)
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

    private void createSomeData() {
        Article article1 = new Article(
                "Fallacies of distributed computing",
                "https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing");
        readingList.put(article1.getId(), article1);
        Article article2 = new Article(
                "Reactive Manifesto",
                "https://www.reactivemanifesto.org/");
        readingList.put(article2.getId(), article2);
    }

    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(readingList.values()));
    }

    private void addOne(RoutingContext routingContext) {
        Article article = routingContext.getBodyAsJson().mapTo(Article.class);
        readingList.put(article.getId(), article);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(article));
    }

    private void deleteOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        try {
            Integer idAsInteger = Integer.valueOf(id);
            readingList.remove(idAsInteger);
            routingContext.response().setStatusCode(204).end();
        } catch (NumberFormatException e) {
            routingContext.response().setStatusCode(400).end();
        }
    }


    private void getOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        try {
            Integer idAsInteger = Integer.valueOf(id);
            Article article = (Article) readingList.get(idAsInteger);
            if (article == null) {
                // Not found
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(article));
            }
        } catch (NumberFormatException e) {
            routingContext.response().setStatusCode(400).end();
        }
    }

    private void updateOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        try {
            Integer idAsInteger = Integer.valueOf(id);
            Article article = (Article) readingList.get(idAsInteger);
            if (article == null) {
                // Not found
                routingContext.response().setStatusCode(404).end();
            } else {
                JsonObject body = routingContext.getBodyAsJson();
                article.setTitle(body.getString("title")).setUrl(body.getString("url"));
                readingList.put(idAsInteger, article);
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(article));
            }
        } catch (NumberFormatException e) {
            routingContext.response().setStatusCode(400).end();
        }

    }
}
