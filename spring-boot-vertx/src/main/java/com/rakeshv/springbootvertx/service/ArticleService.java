package com.rakeshv.springbootvertx.service;

import com.rakeshv.springbootvertx.models.Article;
import com.rakeshv.springbootvertx.repositories.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getAllArticles() {
        log.info("Finding all articles");
        return articleRepository.findAll();
    }

    public Article getOneArticle(String message) {
        log.info("Finding article with id {}", message);
        Optional<Article> articleOptional = articleRepository.findByArticleEqualsIgnoreCase(message);
        return articleOptional.orElse(Article.builder().article("Unable to find article").build());
    }
}
