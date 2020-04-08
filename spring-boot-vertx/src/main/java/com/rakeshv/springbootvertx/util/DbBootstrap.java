package com.rakeshv.springbootvertx.util;

import com.rakeshv.springbootvertx.models.Article;
import com.rakeshv.springbootvertx.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class DbBootstrap implements CommandLineRunner {
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public void run(String... args) throws Exception {
        IntStream.range(0, 10)
                .forEach(count -> articleRepository
                        .save(Article.builder().article(UUID.randomUUID().toString()).build()));
    }
}
