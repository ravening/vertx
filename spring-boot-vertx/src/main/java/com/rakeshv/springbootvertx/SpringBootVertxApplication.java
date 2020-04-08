package com.rakeshv.springbootvertx;

import com.rakeshv.springbootvertx.verticles.ArticleRecipientVerticle;
import com.rakeshv.springbootvertx.verticles.ServerVerticle;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SpringBootVertxApplication {

    @Autowired
    private ServerVerticle serverVerticle;
    @Autowired
    private ArticleRecipientVerticle articleRecipientVerticle;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootVertxApplication.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(serverVerticle);
        vertx.deployVerticle(articleRecipientVerticle);

    }
}
