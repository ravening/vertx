package com.rakeshv.springbootvertx.verticles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakeshv.springbootvertx.service.ArticleService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class ArticleRecipientVerticle extends AbstractVerticle {
    public static final String GET_ALL_ARTICLES = "get.articles.all";
    public static final String GET_ONE_ARTICLE = "get.articles.one";

    private final ObjectMapper mapper = Json.mapper;

    @Autowired
    ArticleService articleService;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start();

        vertx.eventBus()
                .<String>consumer(GET_ALL_ARTICLES)
                .handler(getAllArticlesService(articleService));

//        vertx.eventBus()
//                .<String>consumer(GET_ONE_ARTICLE)
//                .handler(getOneArticleService(articleService));

        vertx.eventBus()
                .<String>consumer(GET_ONE_ARTICLE, message -> {
                    log.info("Received article id {}", message.body());
                    try {
                        String response = getOneArticle(articleService, message.body());
                        message.reply(response);
                    } catch (Exception e) {
                        log.error("Exception happened: {}", e);
                    }

                });
    }

    private Handler<Message<String>> getAllArticlesService(ArticleService articleService) {
        return msg -> vertx.<String>executeBlocking(future -> {
                try {
                    log.info("Received request to fetch all articles");
                    future.complete(mapper.writeValueAsString(articleService.getAllArticles()));
                } catch (JsonProcessingException e) {
                    System.out.println("Failed to serialize result");
                    future.fail(e);
                }
            }, result -> {
                if (result.succeeded()) {
                    msg.reply(result.result());
                } else {
                    msg.reply(result.cause()
                            .toString());
                }
            });
    }


    private Handler<Message<String>> getOneArticleService(ArticleService articleService, String message) {
        return msg -> vertx.<String>executeBlocking(future -> {
                try {
                    log.error("Received request to serach for {}", message);
                    future.complete(mapper.writeValueAsString(articleService.getOneArticle(message)));
                } catch (JsonProcessingException e) {
                    System.out.println("Failed to serialize result");
                    future.fail(e);
                }
            }, result -> {
                if (result.succeeded()) {
                    msg.reply(result.result());
                } else {
                    msg.reply(result.cause()
                            .toString());
                }
            }
        );
    }

    private String getOneArticle(ArticleService articleService, String message) {
        return articleService.getOneArticle(message).toString();
    }
}
